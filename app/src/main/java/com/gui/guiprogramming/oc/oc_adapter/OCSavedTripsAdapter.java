package com.gui.guiprogramming.oc.oc_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_model.OCDBMap;

import java.util.ArrayList;

/*
* This is a custom list adapter, used to display all locally saved trips
* ViewHolder pattern is used
* */
public class OCSavedTripsAdapter extends BaseAdapter {

    ArrayList<OCDBMap> mRowData;
    static LayoutInflater inflater; //inflater to bind layout file to this adapter for each row

    /**
     * @param activity current instance of an activity,
     *                 if this is called inside fragment, pass getActivity()
     * @param mRowData OCDBMap ArrayList, which contains the data to be displayed
     * */
    public OCSavedTripsAdapter(Activity activity, ArrayList<OCDBMap> mRowData) {
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
            view = inflater.inflate(R.layout.oc_row_saved_trip, null);
            holder = new ViewHolder();
            holder.tvStopName = view.findViewById(R.id.tvStopName);
            holder.tvRoute = view.findViewById(R.id.tvRoute);
            holder.tvDestination = view.findViewById(R.id.tvDestination);
            holder.tvStartTime = view.findViewById(R.id.tvStartTime);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        OCDBMap map = mRowData.get(position);
        String stop = map.getStopName() + " - " + map.getStopNo();
        String route = map.getRouteLabel() + " - " + map.getRouteNo();
        String dest = map.getTrip().getTripDestination();
        String startTime = map.getTrip().getTripStartTime();
        holder.tvStopName.setText(stop);
        holder.tvRoute.setText(route);
        holder.tvDestination.setText(dest);
        holder.tvStartTime.setText(startTime);
        return view;
    }

    static class ViewHolder {
        TextView tvStopName, tvRoute, tvDestination, tvStartTime;
    }
}
