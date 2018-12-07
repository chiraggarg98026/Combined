package com.gui.guiprogramming.oc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_helper.OCAppHelper;
import com.gui.guiprogramming.oc.oc_helper.OCDBManager;
import com.gui.guiprogramming.oc.oc_model.Trip;

/**
 * <p>OCDetailsActivity shows all the information about a particular trip.
 * Either from database or from a live server</p>
 * */
public class OCDetailsActivity extends Activity {

    Trip trip;
    String stopNo, stopName, sTime, dest, delay;
    int RouteNo;
    String RouteLabel, Direction, RequestProcessingTime;
    boolean intentReceived = false;
    Button btnSave;
    LinearLayout linearParent;
    OCDBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Intent intent = getIntent();
        TextView tvBusStop = findViewById(R.id.tvBusStop);
        TextView tvRouteNo = findViewById(R.id.tvRouteNo);
        TextView tvDirection = findViewById(R.id.tvDirection);
        TextView tvRequestTime = findViewById(R.id.tvRequestTime);
        TextView tvDestination = findViewById(R.id.tvDestination);
        TextView tvStartTime = findViewById(R.id.tvStartTime);
        TextView tvDelay = findViewById(R.id.tvDelay);
        TextView tvSpeed = findViewById(R.id.tvSpeed);
        TextView tvLatitude = findViewById(R.id.tvLatitude);
        TextView tvLongitude = findViewById(R.id.tvLongitude);
        linearParent = findViewById(R.id.linearParent);
        btnSave = findViewById(R.id.btnSave);
        //safe checking, if the intent contains all the necessary data or not
        //and preventing app to crash for NullPointerException
        if (intent.hasExtra("DATA") && intent.hasExtra("stopNo")
                && intent.hasExtra("stopName") && intent.hasExtra("Direction")
                && intent.hasExtra("RouteLabel") && intent.hasExtra("RouteNo")
                && intent.hasExtra("RequestProcessingTime")) {

            intentReceived = true;
            //reading all the data received from intent, to show on UI.
            trip = (Trip) intent.getSerializableExtra("DATA");
            stopNo = intent.getStringExtra("stopNo");
            stopName = intent.getStringExtra("stopName");
            RouteLabel = intent.getStringExtra("RouteLabel");
            Direction = intent.getStringExtra("Direction");
            RequestProcessingTime = intent.getStringExtra("RequestProcessingTime");
            RouteNo = intent.getIntExtra("RouteNo", 0);
            String stop = stopName + " - " + stopNo;
            String route = RouteLabel + " - " + RouteNo;
            dest = trip.getTripDestination();
            sTime = trip.getTripStartTime();

            //delay unit set as
            // 'minute' -> as per http://octranspo.com/developers/documentation
            delay = trip.getAdjustedScheduleTime();
            String delayToSet = delay + " min";

            //Formatting the request processing time - default is YYYYMMDDHHMISS in API
            // to YYYY-MM-DD HH:MI:SS
            String formattedDateTime = RequestProcessingTime.substring(0, 4)
                    + "-" + RequestProcessingTime.substring(4, 6)
                    + "-" + RequestProcessingTime.substring(6, 8)
                    + " " + RequestProcessingTime.substring(8, 10)
                    + ":" + RequestProcessingTime.substring(10, 12)
                    + ":" + RequestProcessingTime.substring(12, 14);

            String speed = String.valueOf(trip.getGPSSpeed());
            String lat = String.valueOf(trip.getLatitude());
            String lon = String.valueOf(trip.getLongitude());
            tvBusStop.setText(stop);
            tvRouteNo.setText(route);
            tvDirection.setText(Direction);
            tvRequestTime.setText(formattedDateTime);
            tvDestination.setText(dest);
            tvStartTime.setText(sTime);
            tvDelay.setText(delayToSet);
            tvSpeed.setText(speed);
            tvLatitude.setText(lat);
            tvLongitude.setText(lon);
            db = new OCDBManager(OCDetailsActivity.this);
            db.open();
            isExists(db); //checking into database, the data already exists or not
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentReceived) {
                    String message = "";
                    db.open();
                    //if data already exists, then delete otherwise insert data
                    if (isExists(db)) {
                        //DELETE
                        if (db.removeTrip(Integer.parseInt(stopNo), Direction, RouteNo, dest, sTime)) {
                            setResult(OCAppHelper.RESULT_DELETED);
                            message = getResources().getString(R.string.oc_snack_hint_remove);
                        } else {
                            message = getResources().getString(R.string.oc_snack_hint_remove_fail);
                        }
                    } else {
                        //INSERT
                        if (db.saveTrip(Integer.parseInt(stopNo), stopName, Direction, RouteLabel,
                                RouteNo, RequestProcessingTime, trip) != -1) {
                            message = getResources().getString(R.string.oc_snack_hint_saved);
                        } else {
                            message = getResources().getString(R.string.oc_snack_hint_save_fail);
                        }
                    }
                    //cheking again for existence to update a button text.
                    isExists(db);
                    //showing Snackbar
                    Snackbar snackbar = Snackbar.make(linearParent, message, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }

    /**
     * <p>Check for data into database,
     *  and update the button text.</p>
     * @return true if data exists otherwise false.
     * @see OCDBManager#isExists(int, String, int, String, String)
     * */
    boolean isExists(OCDBManager manager) {
        boolean isExists = manager.isExists(Integer.parseInt(stopNo), Direction, RouteNo, dest, sTime);
        if (isExists) {
            String removeText = getResources().getString(R.string.oc_remove_offline);
            btnSave.setText(removeText);
        } else {
            String saveText = getResources().getString(R.string.oc_save_offline);
            btnSave.setText(saveText);
        }
        return isExists;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //closing database connection
        db.close();
    }

    /**
     * Binding menu to activity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Handling click event of menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuStat:
                startActivity(new Intent(OCDetailsActivity.this, OCStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(OCDetailsActivity.this, OCFavoritesActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
        }
        return true;
    }

    /**
     * Showing a help dialog to user
     */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(OCDetailsActivity.this);
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
