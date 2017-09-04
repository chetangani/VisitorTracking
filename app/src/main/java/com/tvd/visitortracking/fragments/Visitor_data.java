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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tvd.visitortracking.MainActivity;
import com.tvd.visitortracking.R;
import com.tvd.visitortracking.posting.DataAPI;
import com.tvd.visitortracking.posting.SendingData;
import com.tvd.visitortracking.posting.SendingData.Check_Out_Visitor;
import com.tvd.visitortracking.values.ConstantValues;
import com.tvd.visitortracking.values.GetSetValues;

public class Visitor_data extends Fragment {
    View view;
    ImageView visitor_image;
    GetSetValues getSetValues;
    TextView tv_visitor_name, tv_visitor_number, tv_visitor_tomeet, tv_visitor_from, tv_visitor_checkin, tv_visitor_checkout;
    LinearLayout checkout_layout;
    Button checkout_btn;
    ProgressDialog progressDialog;

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ConstantValues.VISITOR_CHECKOUT_SUCCESS:
                        progressDialog.dismiss();
                        AlertDialog.Builder resultDialog = new AlertDialog.Builder(getActivity());
                        resultDialog.setTitle("Product Result");
                        resultDialog.setCancelable(false);
                        resultDialog.setMessage(getSetValues.getVisitor_Checkout_Result());
                        resultDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkout_btn.setVisibility(View.GONE);
                            }
                        });
                        AlertDialog dialog = resultDialog.create();
                        dialog.show();
                        break;

                    case ConstantValues.VISITOR_CHECKOUT_FAILURE:
                        break;
                }
            }
        };
    }

    public Visitor_data() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_visitor_data, container, false);

        getSetValues = ((MainActivity) getActivity()).getSetValues();

        visitor_image = (ImageView) view.findViewById(R.id.visitor_data_image);
        tv_visitor_name = (TextView) view.findViewById(R.id.visitor_data_name);
        tv_visitor_number = (TextView) view.findViewById(R.id.visitor_data_number);
        tv_visitor_tomeet = (TextView) view.findViewById(R.id.visitor_data_tomeet);
        tv_visitor_from = (TextView) view.findViewById(R.id.visitor_data_from);
        tv_visitor_checkin = (TextView) view.findViewById(R.id.visitor_data_checkin);
        tv_visitor_checkout = (TextView) view.findViewById(R.id.visitor_data_checkout);
        checkout_btn = (Button) view.findViewById(R.id.checkout_btn);
        checkout_layout = (LinearLayout) view.findViewById(R.id.checkout_layout);

        Picasso.with(getActivity()).load(DataAPI.IMAGE_URL+getSetValues.getVisitor_data_image()).into(visitor_image);
        tv_visitor_name.setText(getSetValues.getVisitor_data_name());
        tv_visitor_number.setText(getSetValues.getVisitor_data_number());
        tv_visitor_tomeet.setText(getSetValues.getVisitor_data_tomeet());
        tv_visitor_from.setText(getSetValues.getVisitor_data_from());
        tv_visitor_checkin.setText(getSetValues.getVisitor_data_checkin());
        try {
            if (!getSetValues.getVisitor_data_checkout().equals("")) {
                checkout_layout.setVisibility(View.VISIBLE);
                tv_visitor_checkout.setText(getSetValues.getVisitor_data_checkout());
                checkout_btn.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(getActivity(), "Result", "Logging Out please wait..", true);
                SendingData sendingData = new SendingData();
                Check_Out_Visitor checkOutVisitor = sendingData.new Check_Out_Visitor(mHandler, getSetValues);
                checkOutVisitor.execute(getSetValues.getVisitor_data_ID());
            }
        });

        return view;
    }

}
