package com.gui.guiprogramming.oc.oc_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_model.RouteDirection;

import java.util.ArrayList;
import java.util.List;

/*
* This is a custom list adapter, used to bind route-directions list, used inside OCNextTripActivity.
* ViewHolder pattern is used
* */

public class OCRouteDirectListAdapter extends BaseAdapter {

    private List<RouteDirection> mRowData;
    private static LayoutInflater inflater; //inflater to bind layout file to this adapter for each row

    /**
     * @param activity current instance of an activity,
     *                 if this is called inside fragment, pass getActivity()
     * @param mRowData RouteDirection ArrayList, which contains the data to be displayed
     * */
    public OCRouteDirectListAdapter(Activity activity, ArrayList<RouteDirection> mRowData) {
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
            view = inflater.inflate(R.layout.oc_row_layout_list_route_direct, null);
            holder = new ViewHolder();
            holder.tvBusRouteLabel = view.findViewById(R.id.tvBusRouteLabel);
            holder.tvBusRouteDirect = view.findViewById(R.id.tvBusRouteDirect);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RouteDirection direction = mRowData.get(position);
        String label = direction.getRouteLabel();
        String direct = direction.getDirection();
        holder.tvBusRouteLabel.setText(label);
        holder.tvBusRouteDirect.setText(direct);
        return view;
    }

    static class ViewHolder {
        TextView tvBusRouteLabel, tvBusRouteDirect;
    }
}
