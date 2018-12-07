package com.gui.guiprogramming.cbc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;

/**
 * CBCStatisticsActivity shows the statistics for news article description
 * */
public class CBCStatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        setContentView(R.layout.cbc_activity_statistics);
        TextView tvTotalNewsCount = findViewById(R.id.tvTotalNewsCount);
        TextView tvMaxWordCount = findViewById(R.id.tvMaxWordCount);
        TextView tvAvgWordCount = findViewById(R.id.tvAvgWordCount);
        TextView tvMinWordCount = findViewById(R.id.tvMinWordCount);
        TextView tvNotFound = findViewById(R.id.tvNotFound);
        //Establishing database connection
        CBCDBManager db = new CBCDBManager(CBCStatisticsActivity.this);
        db.open();
        //getting no. of records from database
        int count = db.getCount();
        //if more than 0 records are stored then view stats
        if (count > 0) {
            //set visibility to gone for an error message
            tvNotFound.setVisibility(View.GONE);
            String c = String.valueOf(count);
            tvTotalNewsCount.setText(c);
            String min = db.getMinWords() + " words";
            String avg = db.getAvgWords() + " words";
            String max = db.getMaxWords() + " words";
            tvMinWordCount.setText(min);
            tvAvgWordCount.setText(avg);
            tvMaxWordCount.setText(max);
        } else {
            //show message otherwise
            tvNotFound.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}
