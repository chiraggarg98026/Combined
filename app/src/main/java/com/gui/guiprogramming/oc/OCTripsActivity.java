package com.gui.guiprogramming.oc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_adapter.OCTripListAdapter;
import com.gui.guiprogramming.oc.oc_model.RouteDirection;
import com.gui.guiprogramming.oc.oc_model.Trip;

import java.util.ArrayList;

/**
 * OCTripsActivity displays available route direction trips for a stop
 * */
public class OCTripsActivity extends Activity {

    ListView listTrips;
    OCTripListAdapter adapter;
    ArrayList<Trip> mRowData;
    TextView tvError;

    String stopNo, stopName;
    int RouteNo;
    String RouteLabel;
    String Direction;
    String RequestProcessingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_activity_trips);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Intent intent = getIntent();
        listTrips = findViewById(R.id.listTrips);
        tvError = findViewById(R.id.tvError);
        TextView tvResultsFor = findViewById(R.id.tvResultsFor);
        TextView tvRouteNo = findViewById(R.id.tvRouteNo);
        //Checking for data in intent
        if (intent.hasExtra("DATA")) {
            //retrieving data from intent
            RouteDirection routeDirection = (RouteDirection) intent.getSerializableExtra("DATA");
            RouteNo = routeDirection.getRouteNo();
            RouteLabel = routeDirection.getRouteLabel();
            Direction = routeDirection.getDirection();
            RequestProcessingTime = routeDirection.getRequestProcessingTime();
            mRowData = routeDirection.getTrips();
        } else {
            mRowData = new ArrayList<>();
        }
        //retrieving data from intent
        stopNo = intent.getStringExtra("stopNo");
        stopName = intent.getStringExtra("stopName");
        stopNo = intent.getStringExtra("stopNo");
        stopName = intent.getStringExtra("stopName");
        String textToSet = stopName + " - " + stopNo;
        tvResultsFor.setText(textToSet);
        String stopText = String.valueOf(RouteNo);
        tvRouteNo.setText(stopText);
        adapter = new OCTripListAdapter(OCTripsActivity.this, mRowData);
        listTrips.setAdapter(adapter);

        if(mRowData.isEmpty())
            tvError.setVisibility(View.VISIBLE);

        listTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(OCTripsActivity.this, OCDetailsActivity.class)
                        .putExtra("DATA", mRowData.get(position))
                        .putExtra("stopNo", stopNo)
                        .putExtra("stopName", stopName)
                        .putExtra("RouteNo", RouteNo)
                        .putExtra("RouteLabel", RouteLabel)
                        .putExtra("Direction", Direction)
                        .putExtra("RequestProcessingTime",RequestProcessingTime)
                );
            }
        });
    }

    /**
     * Binding menu to activity
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handling click event of menu items
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuStat:
                startActivity(new Intent(OCTripsActivity.this, OCStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(OCTripsActivity.this, OCFavoritesActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
        }
        return true;
    }

    /**
     * Showing a help dialog to user
     * */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(OCTripsActivity.this);
        //We do not need title for custom dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting layout to dialog
        dialog.setContentView(R.layout.oc_layout_help_dialog);
//        initializing widgets of custom dialog layout
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

//        showing dialog with MATCH_PARENT width.
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(true);
        dialog.show();
    }
}
