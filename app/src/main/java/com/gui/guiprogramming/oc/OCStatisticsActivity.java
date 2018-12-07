package com.gui.guiprogramming.oc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_helper.OCDBManager;

/**
 * OCStatisticsActivity displays the statistics for a saved data in the database.
 * */
public class OCStatisticsActivity extends Activity {

    TextView tvNotFound, tvTotalItems, tvMinDelay, tvAvgDelay, tvMaxDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        setContentView(R.layout.oc_activity_statistics);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvMinDelay = findViewById(R.id.tvMinDelay);
        tvAvgDelay = findViewById(R.id.tvAvgDelay);
        tvMaxDelay = findViewById(R.id.tvMaxDelay);
        tvNotFound = findViewById(R.id.tvNotFound);
        //Establishing database connection
        OCDBManager db = new OCDBManager(OCStatisticsActivity.this);
        db.open();
        //getting no. of records from database
        int count = db.getCount();
        //if more than 0 records are stored then view stats
        if (count > 0) {
            //set visibility to gone for an error message
            tvNotFound.setVisibility(View.GONE);
            String c = String.valueOf(count);
            tvTotalItems.setText(c);
            String min = String.valueOf(db.getMinDelay()) + " min";
            String avg = String.valueOf(db.getAvgDelay()) + " min";
            String max = String.valueOf(db.getMaxDelay()) + " min";
            tvMinDelay.setText(min);
            tvAvgDelay.setText(avg);
            tvMaxDelay.setText(max);
        } else {
            //show message otherwise
            tvNotFound.setVisibility(View.VISIBLE);
        }
        db.close();
    }
}
