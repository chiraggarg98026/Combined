package com.gui.guiprogramming.movie;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.movie.movie_adapter.MovieDBManager;

public class MovieStatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_statistics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        TextView tvNotFound = findViewById(R.id.tvNotFound);
        TextView tvMovieCount = findViewById(R.id.tvMovieCount);
        TextView tvShortRuntime = findViewById(R.id.tvShortRuntime);
        TextView tvAvgRuntime = findViewById(R.id.tvAvgRuntime);
        TextView tvLongRuntime = findViewById(R.id.tvLongRuntime);
        //Establishing database connection
        MovieDBManager db = new MovieDBManager(MovieStatisticsActivity.this);
        db.open();
        //getting no. of records from database
        int count = db.getCount();
        //if more than 0 records are stored then view stats
        if (count > 0) {
            tvNotFound.setVisibility(View.GONE);
            String c = String.valueOf(count);
            tvMovieCount.setText(c);
            String unit = getResources().getString(R.string.movie_min);
            String shortestRunTime = db.getShortestRunTimeMovie() + " " + unit;
            String avgRunTime = db.getAvgRunTimeMovie() + " " + unit;
            String longestRunTime = db.getLongestRunTimeMovie() + " " + unit;
            tvShortRuntime.setText(shortestRunTime);
            tvAvgRuntime.setText(avgRunTime);
            tvLongRuntime.setText(longestRunTime);
        } else {
            //show message otherwise
            tvNotFound.setVisibility(View.VISIBLE);
        }
        db.close();
    }
}
