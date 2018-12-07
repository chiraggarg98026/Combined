package com.gui.guiprogramming.cbc.cbc_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.cbc.cbc_model.CBCNewsStory;

import java.util.ArrayList;

/**
 * CBCNewsListAdapter is a custom list adapter.
 * ViewHolder pattern is used.
 */
public class CBCNewsListAdapter extends BaseAdapter {


    ArrayList<CBCNewsStory> mRowData;
    static LayoutInflater inflater;

    public CBCNewsListAdapter(Activity activity, ArrayList<CBCNewsStory> mRowData) {
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
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.cbc_row_news_story, null);
            holder.tvNewsTitle = view.findViewById(R.id.tvNewsTitle);
            holder.tvNewsDate = view.findViewById(R.id.tvNewsDate);
            holder.tvNewsType = view.findViewById(R.id.tvNewsType);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //getting data for each row
        CBCNewsStory model = mRowData.get(position);
        String title = model.getTitle();
        String date = model.getPubDate();
        String type = model.getCategory();
        //setting data to UI
        holder.tvNewsTitle.setText(title);
        holder.tvNewsDate.setText(date);
        holder.tvNewsType.setText(type);
        return view;
    }

    static class ViewHolder {
        TextView tvNewsTitle, tvNewsDate, tvNewsType;
    }
}
