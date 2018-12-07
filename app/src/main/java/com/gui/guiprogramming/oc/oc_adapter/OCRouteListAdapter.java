package com.gui.guiprogramming.oc.oc_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_model.Route;

import java.util.List;

/*
* This is a custom list adapter, used to display all the routes when
* user searches for the routes by entering Stop no on SearchRoutes fragment.
* ViewHolder pattern is used
 */
public class OCRouteListAdapter extends BaseAdapter {

    Activity activity;
    List<Route> mRowData;
    static LayoutInflater inflater; //inflater to bind layout file to this adapter for each row

    /**
     * @param activity current instance of an activity,
     *                 if this is called inside fragment, pass getActivity()
     * @param mRowData Route ArrayList, which contains the data to be displayed
     * */
    public OCRouteListAdapter(Activity activity, List<Route> mRowData) {
        this.activity = activity;
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
            view = inflater.inflate(R.layout.oc_row_layout_list_routes, null);
            holder = new ViewHolder();
            holder.tvBusRouteNo = view.findViewById(R.id.tvBusRouteNo);
            holder.tvBusRouteDirectID = view.findViewById(R.id.tvBusRouteDirectID);
            holder.tvBusRouteDirect = view.findViewById(R.id.tvBusRouteDirect);
            holder.tvBusRouteHeading = view.findViewById(R.id.tvBusRouteHeading);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Route route = mRowData.get(position);
        String routeNo = String.valueOf(route.getRouteNo());
        String directID = String.valueOf(route.getDirectionID());
        String direct = route.getDirection();
        String heading = route.getRouteHeading();
        holder.tvBusRouteNo.setText(routeNo);
        holder.tvBusRouteDirectID.setText(directID);
        holder.tvBusRouteDirect.setText(direct);
        holder.tvBusRouteHeading.setText(heading);
        return view;
    }

    static class ViewHolder {
        TextView tvBusRouteNo, tvBusRouteDirectID, tvBusRouteDirect,
                tvBusRouteHeading;
    }
}
