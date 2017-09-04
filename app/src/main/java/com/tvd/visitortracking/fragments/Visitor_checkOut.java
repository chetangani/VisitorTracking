package com.tvd.visitortracking.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;
import com.tvd.visitortracking.MainActivity;
import com.tvd.visitortracking.posting.SendingData;
import com.tvd.visitortracking.values.ConstantValues;
import com.tvd.visitortracking.values.FunctionsCall;
import com.tvd.visitortracking.values.GetSetValues;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Visitor_checkOut extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    ProgressDialog progressDialog;
    GetSetValues getSetValues;
    FunctionsCall functionsCall = new FunctionsCall();
    String Visitor_ID="";

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ConstantValues.VISITOR_DATA_SUCCESS:
                        progressDialog.dismiss();
                        getSetValues.setVisitor_data_ID(Visitor_ID);
                        ((MainActivity) getActivity()).addOnstartup(new Visitor_data());
                        break;

                    case ConstantValues.VISITOR_DATA_FAILURE:
                        break;
                }
            }
        };
    }

    public Visitor_checkOut() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mScannerView = new ZXingScannerView(getActivity());
        getSetValues = ((MainActivity) getActivity()).getSetValues();
        return mScannerView;
    }

    @Override
    public void handleResult(Result result) {
        if (result.getText().length() > 7) {
            Visitor_ID = result.getText().substring(7);
            progressDialog = ProgressDialog.show(getActivity(), "Result", "Fetching result please wait..", true);
            SendingData sendingData = new SendingData();
            SendingData.Visitor_Data visitorData = sendingData.new Visitor_Data(mHandler, getSetValues);
            visitorData.execute(Visitor_ID);
        } else rescan();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();   // Stop camera on pause
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);   // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

    public GetSetValues getset() {
        return this.getSetValues;
    }

    private void rescan() {
        AlertDialog.Builder rescan = new AlertDialog.Builder(getActivity());
        rescan.setTitle("ReScan");
        rescan.setMessage("Please Rescan the result...");
        rescan.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.resumeCameraPreview(Visitor_checkOut.this);
            }
        });
    }
}
