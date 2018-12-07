package com.gui.guiprogramming.oc.oc_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_model.Trip;

import java.util.ArrayList;

/**
 *
 */
public class OCTripListAdapter extends BaseAdapter {

    ArrayList<Trip> mRowData;
    static LayoutInflater inflater;

    public OCTripListAdapter(Activity activity, ArrayList<Trip> mRowData) {
        this.mRowData = mRowData;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mRowData.size();
    }

    @Override
    public Object getItem(int position) {
        return mRowData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.oc_row_trip_list, null);
            holder = new ViewHolder();
            holder.tvTripDestination = view.findViewById(R.id.tvTripDestination);
            holder.tvTripStartTime = view.findViewById(R.id.tvTripStartTime);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Trip trip = mRowData.get(position);
        String tripDest = trip.getTripDestination();
        String tripStartTime = trip.getTripStartTime();
        if(tripStartTime.isEmpty()){
            tripStartTime = "";
        }
        if(tripDest.isEmpty()){
            tripDest = "";
        }
        holder.tvTripDestination.setText(tripDest);
        holder.tvTripStartTime.setText(tripStartTime);
        return view;
    }

    static class ViewHolder {
        TextView tvTripDestination, tvTripStartTime;
    }
}
