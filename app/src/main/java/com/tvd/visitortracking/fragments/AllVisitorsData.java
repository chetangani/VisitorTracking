package com.tvd.visitortracking.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tvd.visitortracking.MainActivity;
import com.tvd.visitortracking.R;
import com.tvd.visitortracking.adapter.VisitorAdapter;
import com.tvd.visitortracking.posting.SendingData;
import com.tvd.visitortracking.posting.SendingData.AllVisitors_Data;
import com.tvd.visitortracking.values.ConstantValues;
import com.tvd.visitortracking.values.GetSetValues;

import java.util.ArrayList;

public class AllVisitorsData extends Fragment {
    View view;
    GetSetValues getSetValues;
    ProgressDialog progressDialog;
    RecyclerView visitorsview;
    ArrayList<GetSetValues> visitorsList;
    VisitorAdapter visitorAdapter;

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ConstantValues.VISITOR_SUCCESS:
                        progressDialog.dismiss();
                        break;

                    case ConstantValues.VISITOR_FAILURE:
                        break;
                }
            }
        };
    }

    public AllVisitorsData() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_visitors_data, container, false);

        getSetValues = ((MainActivity) getActivity()).getSetValues();

        visitorsview = (RecyclerView) view.findViewById(R.id.visitors_view_data);
        visitorsList = new ArrayList<>();
        visitorAdapter = new VisitorAdapter(visitorsList, getSetValues, getActivity());
        visitorsview.setHasFixedSize(true);
        visitorsview.setLayoutManager(new LinearLayoutManager(getActivity()));
        visitorsview.setAdapter(visitorAdapter);

        progressDialog = ProgressDialog.show(getActivity(), "Result", "Fetching result please wait..", true);
        SendingData sendingData = new SendingData();
        AllVisitors_Data visitorsData = sendingData.new AllVisitors_Data(mHandler, getSetValues, visitorsList, visitorAdapter);
        visitorsData.execute();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }
}
