package com.gui.guiprogramming.food;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.food.food_helper.FoodDBManager;

public class FoodStatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity_statistics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        TextView tvNotFound = findViewById(R.id.tvNotFound);
        TextView tvTotalItems = findViewById(R.id.tvTotalItems);
        TextView tvSumCalories = findViewById(R.id.tvSumCalories);
        TextView tvMinCalories = findViewById(R.id.tvMinCalories);
        TextView tvAvgCalories = findViewById(R.id.tvAvgCalories);
        TextView tvMaxCalories = findViewById(R.id.tvMaxCalories);
        //Establishing database connection
        FoodDBManager db = new FoodDBManager(FoodStatisticsActivity.this);
        db.open();
        //getting no. of records from database
        int count = db.getCount();
        //if more than 0 records are stored then view stats
        if (count > 0) {
            tvNotFound.setVisibility(View.GONE);
            String c = String.valueOf(count);
            tvTotalItems.setText(c);
            String sum = String.valueOf(db.getTotalCalories());
            String min = String.valueOf(db.getMinCalories());
            String avg = String.valueOf(db.getAvgCalories());
            String max = String.valueOf(db.getMaxCalories());
            tvSumCalories.setText(sum);
            tvMinCalories.setText(min);
            tvAvgCalories.setText(avg);
            tvMaxCalories.setText(max);
        } else {
            //show message otherwise
            tvNotFound.setVisibility(View.VISIBLE);
        }
        db.close();
    }
}
