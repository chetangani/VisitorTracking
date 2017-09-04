package com.tvd.visitortracking.goojprt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class BTPrinting extends IO {
    private static final String TAG = "BTPrinting";
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothServerSocket mmServerSocket = null;
    private BluetoothSocket mmClientSocket = null;
    private DataInputStream is = null;
    private DataOutputStream os = null;
    private AtomicBoolean isOpened = new AtomicBoolean(false);
    private AtomicBoolean isReadyRW = new AtomicBoolean(false);
    private IOCallBack cb = null;
    private Vector<Byte> rxBuffer = new Vector();
    private AtomicLong nIdleTime = new AtomicLong(0L);
    private final ReentrantLock mCloseLocker = new ReentrantLock();
    private String address;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED".equals(action) || "android.bluetooth.device.action.ACL_DISCONNECTED".equals(action)) {
                if(device == null) {
                    return;
                }

                if(!device.getAddress().equalsIgnoreCase(BTPrinting.this.address)) {
                    return;
                }

                BTPrinting.this.Close();
            }

        }
    };
    private IntentFilter filter = new IntentFilter();
    private Context context;
    private static int nCheckFaildTimes = 0;
    private static int nMaxCheckFailedTimes = 30;

    public BTPrinting() {
    }

    private void RegisterReceiver() {
        if(!this.filter.hasAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED")) {
            this.filter.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        }

        if(!this.filter.hasAction("android.bluetooth.device.action.ACL_DISCONNECTED")) {
            this.filter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        }

        this.context.registerReceiver(this.receiver, this.filter);
        Log.i("BTPrinting", "RegisterReceiver");
    }

    private void UnregisterReceiver() {
        this.context.unregisterReceiver(this.receiver);
        Log.i("BTPrinting", "UnregisterReceiver");
    }

    public boolean Open(String BTAddress, Context mContext) {
        this.Lock();

        try {
            if(this.isOpened.get()) {
                throw new Exception("Already open");
            }

            if(mContext == null) {
                throw new Exception("Null Pointer mContext");
            }

            this.context = mContext;
            if(BTAddress == null) {
                throw new Exception("Null Pointer BTAddress");
            }

            this.address = BTAddress;
            this.isReadyRW.set(false);
            BluetoothAdapter ex = BluetoothAdapter.getDefaultAdapter();
            if(ex == null) {
                throw new Exception("Null BluetoothAdapter");
            }

            ex.cancelDiscovery();
            BluetoothDevice device = ex.getRemoteDevice(BTAddress);
            long timeout = 10000L;
            long time = System.currentTimeMillis();

            while(System.currentTimeMillis() - time < timeout) {
                try {
                    this.mmClientSocket = device.createRfcommSocketToServiceRecord(uuid);

                    try {
                        this.mmClientSocket.connect();
                        this.os = new DataOutputStream(this.mmClientSocket.getOutputStream());
                        this.is = new DataInputStream(this.mmClientSocket.getInputStream());
                        this.isReadyRW.set(true);
                    } catch (Exception var33) {
                        Log.i("BTPrinting", var33.toString());

                        try {
                            this.mmClientSocket.close();
                        } catch (Exception var31) {
                            Log.i("BTPrinting", var31.toString());
                        } finally {
                            this.mmClientSocket = null;
                            this.os = null;
                            this.is = null;
                        }

                        throw new Exception("Connect Failed");
                    }
                } catch (Exception var34) {
                    Log.i("BTPrinting", var34.toString());
                }

                if(this.isReadyRW.get()) {
                    break;
                }
            }

            if(this.isReadyRW.get()) {
                Log.v("BTPrinting", "Connected to " + BTAddress);
                this.rxBuffer.clear();
                this.RegisterReceiver();
                boolean bCaysnPrinter = false;

                SharedPreferences ex1;
                try {
                    ex1 = this.context.getSharedPreferences("BTPrinting", 0);
                    bCaysnPrinter = ex1.getBoolean(BTAddress, false);
                } catch (Exception var30) {
                    Log.v("BTPrinting", var30.toString());
                }

                if(!bCaysnPrinter) {
                    if(1 == this.PTR_CheckPrinter()) {
                        bCaysnPrinter = true;
                    }

                    if(bCaysnPrinter) {
                        try {
                            ex1 = this.context.getSharedPreferences("BTPrinting", 0);
                            SharedPreferences.Editor editor = ex1.edit();
                            editor.putBoolean(BTAddress, bCaysnPrinter);
                            editor.commit();
                        } catch (Exception var29) {
                            Log.v("BTPrinting", var29.toString());
                        }
                    }
                }
            }

            this.isOpened.set(this.isReadyRW.get());
            if(this.cb != null) {
                if(this.isOpened.get()) {
                    this.cb.OnOpen();
                } else {
                    this.cb.OnOpenFailed();
                }
            }
        } catch (Exception var35) {
            Log.i("BTPrinting", var35.toString());
        } finally {
            this.Unlock();
        }

        return this.isOpened.get();
    }

    public boolean Listen(String BTAddress, int timeout, Context mContext) {
        this.Lock();

        try {
            if(this.isOpened.get()) {
                throw new Exception("Already open");
            }

            if(mContext == null) {
                throw new Exception("Null Pointer mContext");
            }

            this.context = mContext;
            if(BTAddress == null) {
                throw new Exception("Null Pointer BTAddress");
            }

            this.address = BTAddress;
            this.isReadyRW.set(false);
            BluetoothAdapter ex = BluetoothAdapter.getDefaultAdapter();
            if(ex == null) {
                throw new Exception("Null BluetoothAdapter");
            }

            ex.cancelDiscovery();
            this.mmServerSocket = ex.listenUsingRfcommWithServiceRecord("rfcomm", uuid);

            try {
                this.mmClientSocket = this.mmServerSocket.accept(timeout);
            } catch (Exception var46) {
                Log.i("BTPrinting", var46.toString());

                try {
                    this.mmServerSocket.close();
                } catch (Exception var41) {
                    Log.i("BTPrinting", var41.toString());
                } finally {
                    this.mmServerSocket = null;
                }

                throw new Exception("Accept Failed");
            }

            try {
                this.os = new DataOutputStream(this.mmClientSocket.getOutputStream());
                this.is = new DataInputStream(this.mmClientSocket.getInputStream());
                this.isReadyRW.set(true);
            } catch (Exception var45) {
                Log.i("BTPrinting", var45.toString());

                try {
                    this.mmClientSocket.close();
                } catch (Exception var39) {
                    Log.i("BTPrinting", var39.toString());
                } finally {
                    this.mmClientSocket = null;
                    this.os = null;
                    this.is = null;
                }

                throw new Exception("Get Stream Failed");
            }

            if(this.isReadyRW.get()) {
                Log.v("BTPrinting", "Connected to " + BTAddress);
                this.rxBuffer.clear();
                this.RegisterReceiver();
                boolean bCaysnPrinter = false;

                SharedPreferences ex1;
                try {
                    ex1 = this.context.getSharedPreferences("BTPrinting", 0);
                    bCaysnPrinter = ex1.getBoolean(BTAddress, false);
                } catch (Exception var44) {
                    Log.v("BTPrinting", var44.toString());
                }

                if(!bCaysnPrinter) {
                    if(1 == this.PTR_CheckPrinter()) {
                        bCaysnPrinter = true;
                    }

                    if(bCaysnPrinter) {
                        try {
                            ex1 = this.context.getSharedPreferences("BTPrinting", 0);
                            SharedPreferences.Editor editor = ex1.edit();
                            editor.putBoolean(BTAddress, bCaysnPrinter);
                            editor.commit();
                        } catch (Exception var43) {
                            Log.v("BTPrinting", var43.toString());
                        }
                    }
                }
            }

            this.isOpened.set(this.isReadyRW.get());
            if(this.cb != null) {
                if(this.isOpened.get()) {
                    this.cb.OnOpen();
                } else {
                    this.cb.OnOpenFailed();
                }
            }
        } catch (Exception var47) {
            Log.i("BTPrinting", var47.toString());
        } finally {
            this.Unlock();
        }

        return this.isOpened.get();
    }

    public void Close() {
        this.mCloseLocker.lock();

        try {
            try {
                if(this.mmServerSocket != null) {
                    this.mmServerSocket.close();
                }
            } catch (Exception var8) {
                ;
            }

            try {
                if(this.mmClientSocket != null) {
                    this.mmClientSocket.close();
                }
            } catch (Exception var7) {
                ;
            }

            if(!this.isReadyRW.get()) {
                throw new Exception();
            }

            this.mmServerSocket = null;
            this.mmClientSocket = null;
            this.is = null;
            this.os = null;
            this.UnregisterReceiver();
            this.isReadyRW.set(false);
            if(!this.isOpened.get()) {
                throw new Exception();
            }

            this.isOpened.set(false);
            if(this.cb != null) {
                this.cb.OnClose();
            }
        } catch (Exception var9) {
            Log.i("BTPrinting", var9.toString());
        } finally {
            this.mCloseLocker.unlock();
        }

    }

    public int Write(byte[] buffer, int offset, int count) {
        if(!this.isReadyRW.get()) {
            return -1;
        } else {
            this.Lock();
            boolean nBytesWritten = false;

            int nBytesWritten1;
            try {
                this.nIdleTime.set(0L);
                this.os.write(buffer, offset, count);
                this.os.flush();
                nBytesWritten1 = count;
                this.nIdleTime.set(System.currentTimeMillis());
            } catch (Exception var9) {
                Log.e("BTPrinting", var9.toString());
                this.Close();
                nBytesWritten1 = -1;
            } finally {
                this.Unlock();
            }

            return nBytesWritten1;
        }
    }

    public int Read(byte[] buffer, int offset, int count, int timeout) {
        if(!this.isReadyRW.get()) {
            return -1;
        } else {
            this.Lock();
            int nBytesReaded = 0;

            try {
                this.nIdleTime.set(0L);
                long ex = System.currentTimeMillis();

                label107:
                while(true) {
                    while(true) {
                        if(System.currentTimeMillis() - ex >= (long)timeout) {
                            break label107;
                        }

                        if(!this.isReadyRW.get()) {
                            throw new Exception("Not Ready For Read Write");
                        }

                        if(nBytesReaded == count) {
                            break label107;
                        }

                        if(this.rxBuffer.size() > 0) {
                            buffer[offset + nBytesReaded] = ((Byte)this.rxBuffer.get(0)).byteValue();
                            this.rxBuffer.remove(0);
                            ++nBytesReaded;
                        } else {
                            int available = this.is.available();
                            if(available > 0) {
                                byte[] receive = new byte[available];
                                int nReceived = this.is.read(receive);
                                if(nReceived > 0) {
                                    for(int i = 0; i < nReceived; ++i) {
                                        this.rxBuffer.add(Byte.valueOf(receive[i]));
                                    }
                                }
                            } else {
                                Thread.sleep(1L);
                            }
                        }
                    }
                }

                this.nIdleTime.set(System.currentTimeMillis());
            } catch (Exception var15) {
                Log.e("BTPrinting", var15.toString());
                this.Close();
                nBytesReaded = -1;
            } finally {
                this.Unlock();
            }

            return nBytesReaded;
        }
    }

    public void SkipAvailable() {
        this.Lock();

        try {
            this.rxBuffer.clear();
            this.is.skip((long)this.is.available());
        } catch (Exception var5) {
            Log.i("BTPrinting", var5.toString());
        } finally {
            this.Unlock();
        }

    }

    public boolean IsOpened() {
        return this.isOpened.get();
    }

    public void SetCallBack(IOCallBack callBack) {
        this.Lock();

        try {
            this.cb = callBack;
        } catch (Exception var6) {
            Log.i("BTPrinting", var6.toString());
        } finally {
            this.Unlock();
        }

    }

    private int PTR_CheckEncrypt() {
        this.Lock();
        byte result = -1;

        try {
            Random ex = new Random(System.currentTimeMillis());
            byte[] data = new byte[]{31, 40, 99, 8, 0, 27, 64, -46, -45, -44, -43, 27, 64, 0, 0, 0, 0, 29, 114, 1};

            for(int cmd = 0; cmd < 4; ++cmd) {
                data[7 + cmd] = (byte)ex.nextInt(9);
            }

            byte[] var23 = new byte[60 + data.length];
            System.arraycopy(data, 0, var23, 60, data.length);
            this.SkipAvailable();
            if(this.Write(var23, 0, var23.length) == var23.length) {
                byte[] rec = new byte[7];

                while(this.Read(rec, 0, 1, 3000) == 1) {
                    result = 0;
                    if(rec[0] == 99) {
                        if(this.Read(rec, 1, 5, 3000) == 5 && rec[1] == 95) {
                            long v1 = ((long)data[5] & 255L) << 24 | ((long)data[6] & 255L) << 16 | ((long)data[7] & 255L) << 8 | (long)data[8] & 255L;
                            long v2 = ((long)data[9] & 255L) << 24 | ((long)data[10] & 255L) << 16 | ((long)data[11] & 255L) << 8 | (long)data[12] & 255L;
                            long vadd = v1 + v2 & 4294967295L;
                            long vxor = (v1 ^ v2) & 4294967295L;
                            long l1 = v1 & 65535L;
                            long h2 = v2 >> 16 & 65535L;
                            v1 = l1 * l1 - h2 * h2 & 4294967295L;
                            v1 = vadd - vxor - v1 & 4294967295L;
                            v2 = ((long)rec[2] & 255L) << 24 | ((long)rec[3] & 255L) << 16 | ((long)rec[4] & 255L) << 8 | (long)rec[5] & 255L;
                            if(v1 == v2) {
                                result = 1;
                            }
                        }
                        break;
                    }

                    if((rec[0] & 144) == 0) {
                        break;
                    }
                }
            }
        } catch (Exception var21) {
            Log.i("BTPrinting", var21.toString());
        } finally {
            this.Unlock();
        }

        return result;
    }

    private boolean PTR_CheckKey() {
        this.Lock();
        boolean result = false;

        try {
            byte[] ex = "XSH-KCEC".getBytes();
            byte[] random = new byte[8];
            Random rmByte = new Random(System.currentTimeMillis());

            for(int HeaderSize = 0; HeaderSize < 8; ++HeaderSize) {
                random[HeaderSize] = (byte)rmByte.nextInt(9);
            }

            boolean var20 = true;
            byte[] recHeader = new byte[5];
            Object recData = null;
            boolean rec = false;
            boolean recDataLen = false;
            byte[] randomlen = new byte[]{(byte)(random.length & 255), (byte)(random.length >> 8 & 255)};
            byte[] data = ByteUtils.byteArraysToBytes(new byte[][]{{31, 31, 2}, randomlen, random, {27, 64}});
            this.SkipAvailable();
            this.Write(data, 0, data.length);
            int var22 = this.Read(recHeader, 0, 5, 1000);
            if(var22 == 5) {
                int var23 = (recHeader[3] & 255) + (recHeader[4] << 8 & 255);
                byte[] var21 = new byte[var23];
                var22 = this.Read(var21, 0, var23, 1000);
                if(var22 == var23) {
                    byte[] decrypted = new byte[var21.length + 1];
                    DES2 des2 = new DES2();
                    des2.yxyDES2_InitializeKey(ex);
                    des2.yxyDES2_DecryptAnyLength(var21, decrypted, var21.length);
                    result = ByteUtils.bytesEquals(random, 0, decrypted, 0, random.length);
                }
            }
        } catch (Exception var18) {
            Log.i("BTPrinting", var18.toString());
        } finally {
            this.Unlock();
        }

        return result;
    }

    private int PTR_CheckPrinter() {
        this.Lock();
        int check = -1;

        try {
            for(int ex = 0; ex < 3; ++ex) {
                check = this.PTR_CheckEncrypt();
                if(check != -1) {
                    break;
                }
            }

            if(check == 0 && this.PTR_CheckKey()) {
                check = 1;
            }

            if(check == 1) {
                nCheckFaildTimes = 0;
            } else if(check == 0) {
                ++nCheckFaildTimes;
            }

            if(nCheckFaildTimes >= nMaxCheckFailedTimes) {
                byte[] var11 = new byte[]{13, 10, 27, 64, 28, 38, 27, 57, 1};
                byte[] txt = "----Unknow printer----\r\n".getBytes();
                byte[] cmd = new byte[var11.length + txt.length];
                byte offset = 0;
                System.arraycopy(var11, 0, cmd, offset, var11.length);
                int var12 = offset + var11.length;
                System.arraycopy(txt, 0, cmd, var12, txt.length);
                int var10000 = var12 + txt.length;
                this.Write(cmd, 0, cmd.length);
            }
        } catch (Exception var9) {
            Log.i("BTPrinting", var9.toString());
        } finally {
            this.Unlock();
        }

        return check;
    }
}
