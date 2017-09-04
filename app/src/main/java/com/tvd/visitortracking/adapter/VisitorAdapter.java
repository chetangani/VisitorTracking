package com.tvd.visitortracking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tvd.visitortracking.MainActivity;
import com.tvd.visitortracking.R;
import com.tvd.visitortracking.fragments.Visitor_data;
import com.tvd.visitortracking.posting.DataAPI;
import com.tvd.visitortracking.values.GetSetValues;

import java.util.ArrayList;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.VisitorHolder> {
    ArrayList<GetSetValues> arrayList = new ArrayList<>();
    GetSetValues getSetValues;
    Context context;

    public VisitorAdapter(ArrayList<GetSetValues> arrayList, GetSetValues getSetValues, Context context) {
        this.arrayList = arrayList;
        this.getSetValues = getSetValues;
        this.context = context;
    }

    @Override
    public VisitorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visitors_card, parent, false);
        VisitorHolder visitorHolder = new VisitorHolder(view);
        return visitorHolder;
    }

    @Override
    public void onBindViewHolder(VisitorHolder holder, int position) {
        GetSetValues details = arrayList.get(position);
        holder.tv_name.setText(details.getVisitor_view_name());
        holder.tv_number.setText(details.getVisitor_view_number());
        holder.tv_tomeet.setText(details.getVisitor_view_tomeet());
        holder.tv_checkin.setText(details.getVisitor_view_checkin());
        Picasso.with(context).load(DataAPI.IMAGE_URL+details.getVisitor_view_image()).into(holder.visitor_image);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class VisitorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView visitor_image;
        TextView tv_name, tv_number, tv_tomeet, tv_checkin;
        public VisitorHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            visitor_image = (ImageView) itemView.findViewById(R.id.visitors_card_image);
            tv_name = (TextView) itemView.findViewById(R.id.visitor_card_name);
            tv_number = (TextView) itemView.findViewById(R.id.visitor_card_number);
            tv_tomeet = (TextView) itemView.findViewById(R.id.visitor_card_tomeet);
            tv_checkin = (TextView) itemView.findViewById(R.id.visitor_card_check_in_time);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            GetSetValues details = arrayList.get(pos);
            getSetValues.setVisitor_data_ID(details.getVisitor_view_ID());
            getSetValues.setVisitor_data_name(details.getVisitor_view_name());
            getSetValues.setVisitor_data_number(details.getVisitor_view_number());
            getSetValues.setVisitor_data_tomeet(details.getVisitor_view_tomeet());
            getSetValues.setVisitor_data_from(details.getVisitor_view_from());
            getSetValues.setVisitor_data_image(details.getVisitor_view_image());
            getSetValues.setVisitor_data_checkin(details.getVisitor_view_checkin());
            getSetValues.setVisitor_data_checkout(details.getVisitor_view_checkout());
            ((MainActivity) context).addOnstartup(new Visitor_data());
        }
    }
}
