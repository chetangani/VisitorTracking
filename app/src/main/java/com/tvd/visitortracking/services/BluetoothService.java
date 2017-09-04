package com.tvd.visitortracking.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.tvd.visitortracking.goojprt.BTPrinting;
import com.tvd.visitortracking.goojprt.Canvas;
import com.tvd.visitortracking.goojprt.IOCallBack;
import com.tvd.visitortracking.goojprt.Pos;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothService extends Service implements IOCallBack {

    private static final int PRINTER_CONNECTED = 1;
    private static final int PRINTER_DISCONNECTED = 2;

    BTPrinting mBt = new BTPrinting();
    public static Pos mPos = new Pos();
    public static Canvas mCanvas = new Canvas();
    public static ExecutorService es = Executors.newFixedThreadPool(3);
    BluetoothService mActivity;
    BluetoothAdapter mBluetoothAdapter;
    public static boolean printer_connected = false;
    private static String DEVICE_NAME = "BP301-2";

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PRINTER_CONNECTED:
                        printer_connected = true;
                        logStatus("Handler Printer Connected");
                        break;

                    case PRINTER_DISCONNECTED:
                        printer_connected = false;
                        logStatus("Handler Printer Disconnected");
                        break;
                }
            }
        };
    }

    public BluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logStatus("Bluetooth Services onCreate");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();

        mActivity = this;
        mCanvas.Set(mBt);
        mPos.Set(mBt);
        mBt.SetCallBack(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(mReceiver, filter);
            }
        }, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getPairedDevices();
        return START_STICKY;
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            try {
                for (BluetoothDevice device : pairedDevice) {
                    if (device.getName().equals(DEVICE_NAME)) {
                        es.submit(new TaskOpen(mBt, device.getAddress(), mActivity));
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnOpen() {
        mHandler.sendEmptyMessage(PRINTER_CONNECTED);
        logStatus("OnOpen Printer");
        printer_connected = true;
    }

    @Override
    public void OnOpenFailed() {
        mHandler.sendEmptyMessage(PRINTER_DISCONNECTED);
        logStatus("OnOpenFailed Printer");
        printer_connected = false;
    }

    @Override
    public void OnClose() {
        mHandler.sendEmptyMessage(PRINTER_DISCONNECTED);
        logStatus("OnClose Printer");
        printer_connected = false;
    }

    public class TaskOpen implements Runnable {
        BTPrinting bt = null;
        String address = null;
        Context context = null;

        public TaskOpen(BTPrinting bt, String address, Context context) {
            this.bt = bt;
            this.address = address;
            this.context = context;
        }

        @Override
        public void run() {
            bt.Open(address, context);
        }
    }

    public class TaskClose implements Runnable {
        BTPrinting bt = null;

        public TaskClose(BTPrinting bt) {
            this.bt = bt;
        }

        @Override
        public void run() {
            bt.Close();
        }
    }

    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    if (device.getName().equals(DEVICE_NAME)) {
                        es.submit(new TaskOpen(mBt, device.getAddress(), mActivity));
                    }
                } else logStatus("ACTION_FOUND_UNPAIRED: "+device.getName());
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                logStatus("ACTION_CONNECTED: "+device.getName());
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                logStatus("ACTION_DISCOVERY_FINISHED");
                if (!printer_connected)
                    mBluetoothAdapter.startDiscovery();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                logStatus("ACTION_DISCOVERY_STARTED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                logStatus("ACTION_DISCONNECTED: "+device.getName());
                mHandler.sendEmptyMessage(PRINTER_DISCONNECTED);
                printer_connected = false;
                mBluetoothAdapter.startDiscovery();
            }
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    logStatus("Paired Device: "+device.getName());
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        logStatus("onDestroy Service");
        if (printer_connected)
            es.submit(new TaskClose(mBt));
        unregisterReceiver(mReceiver);
        mBluetoothAdapter.disable();
    }

    private void logStatus(String msg) {
        Log.d("debug", msg);
    }
}
