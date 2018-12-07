package com.gui.guiprogramming.food.food_adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.food.food_model.Food;

import java.util.ArrayList;

/**
 * FoodSearchResultListAdapter is a custom ListAdapter, used to set custom layout to ListView
 * ViewHolder pattern is used
 */
public class FoodSearchResultListAdapter extends BaseAdapter {

    Activity activity;
    ArrayList<Food> mRowData;
    static LayoutInflater inflater;

    public FoodSearchResultListAdapter(Activity activity, ArrayList<Food> mRowData) {
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
            view = inflater.inflate(R.layout.food_row_result_list, null);
            holder = new ViewHolder();
            holder.tvRowFoodTitle = view.findViewById(R.id.tvRowFoodTitle);
            holder.tvRowFoodCategory = view.findViewById(R.id.tvRowFoodCategory);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Food model = mRowData.get(position);
        String label = model.getLabel();
        String category = model.getCategory();
        holder.tvRowFoodTitle.setText(label);
        holder.tvRowFoodCategory.setText(category);
        return view;
    }

    static class ViewHolder {
        TextView tvRowFoodTitle, tvRowFoodCategory;
    }
}
