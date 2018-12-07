package com.gui.guiprogramming.oc;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.gui.guiprogramming.oc.oc_adapter.OCRouteDirectListAdapter;
import com.gui.guiprogramming.oc.oc_helper.OCAppHelper;
import com.gui.guiprogramming.oc.oc_model.Route;
import com.gui.guiprogramming.oc.oc_model.RouteDirection;
import com.gui.guiprogramming.oc.oc_model.Trip;
import com.gui.guiprogramming.oc.oc_util.WebUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
* This activity displays all the trips available for a particular route and stop#.
* */
public class OCNextTripActivity extends Activity {

    Intent intent;
    Route route;
    String stopNo, stopName;
    int routeNo;

    ListView listRouteDirects;
    OCRouteDirectListAdapter adapter;
    ArrayList<RouteDirection> mRowData;
    TextView tvResultsFor, tvRouteNo, tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_activity_next_trip);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        intent = getIntent();
        listRouteDirects = findViewById(R.id.listRouteDirects);
        tvResultsFor = findViewById(R.id.tvResultsFor);
        tvRouteNo = findViewById(R.id.tvRouteNo);
        tvError = findViewById(R.id.tvError);
        if (intent.hasExtra("DATA")) {
            route = (Route) intent.getSerializableExtra("DATA");
            routeNo = route.getRouteNo();
        } else {
            route = new Route();
            routeNo = 0;
        }
        stopNo = intent.getStringExtra("stopNo");
        stopName = intent.getStringExtra("stopName");
        String textToSet = stopName + " - " + stopNo;
        tvResultsFor.setText(textToSet);
        String stopText = String.valueOf(routeNo);
        tvRouteNo.setText(stopText);

        mRowData = new ArrayList<>();
        adapter = new OCRouteDirectListAdapter(OCNextTripActivity.this, mRowData);
        listRouteDirects.setAdapter(adapter);

        new Async(stopNo, String.valueOf(routeNo)).execute();

        listRouteDirects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(OCNextTripActivity.this, OCTripsActivity.class)
                        .putExtra("DATA", mRowData.get(position))
                        .putExtra("stopNo", stopNo)
                        .putExtra("stopName",stopName)
                );
            }
        });
    }

    /**
     * Calling API
     * */
    public class Async extends AsyncTask<Void, Void, String> {
        //https://api.octranspo1.com/v1.2/GetNextTripsForStop
        // ?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=3017&routeNo=95
        ProgressDialog dialog = new ProgressDialog(OCNextTripActivity.this);
        String mURL;
        String stopNo;
        String routeNo;

        /**
         * @param stopNo bus stop number
         * @param routeNo route# to query next trips
         * */
        public Async(String stopNo, String routeNo) {
            this.stopNo = stopNo;
            this.routeNo = routeNo;
            this.mURL = WebUtil.GET_NEXT_TRPIS + "&stopNo=" + stopNo + "&routeNo=" + routeNo;
        }

        //Showing a progress dialog to prevent user to interact with UI.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRowData.clear();
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        //Executing a GET request and parsing the XML response
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(mURL);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");

                //Flags used to make sure we are parsing the correct data inside various nested tags
                boolean insideRoute = false;
                boolean insideRouteDirect = false;
                boolean insideTrips = false;
                boolean insideTrip = false;

                RouteDirection routeDirection = null;
                Trip trip = null;
//                ArrayList<RouteDirection> directions = null;
                ArrayList<Trip> trips = null;
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
//                        Log.i("Route scanning tag", xpp.getName());
                        switch (xpp.getName()) {
                            case "Error":
                                String e = xpp.nextText();
                                if (e.trim().length() != 0) {
                                    String errorMessage =
                                            OCAppHelper.resolveErrorCode(Integer.parseInt(e));
                                    Log.i("Route Error==>", errorMessage);
                                    return errorMessage;
                                }
                                break;
                            case "Route":
                                insideRoute = true;
                                break;
                            case "RouteDirection":
                                if (insideRoute) {
                                    insideRouteDirect = true;
                                    routeDirection = new RouteDirection();
                                }
                                break;
                            case "RouteNo":
                                if (insideRouteDirect) {
                                    routeDirection.setRouteNo(Integer.parseInt(xpp.nextText()));
                                }
                                break;
                            case "RouteLabel":
                                if (insideRouteDirect) {
                                    routeDirection.setRouteLabel(xpp.nextText());
                                }
                                break;
                            case "Direction":
                                if (insideRouteDirect) {
                                    routeDirection.setDirection(xpp.nextText());
                                }
                                break;
                            case "RequestProcessingTime":
                                if (insideRouteDirect) {
                                    routeDirection.setRequestProcessingTime(xpp.nextText());
                                }
                                break;
                            case "Trips":
                                if (insideRouteDirect) {
                                    insideTrips = true;
                                    trips = new ArrayList<>();
                                }
                                break;
                            case "Trip":
                                if (insideTrips) {
                                    insideTrip = true;
                                    trip = new Trip();
                                }
                                break;
                            case "TripDestination":
                                if (insideTrip) {
                                    String s = xpp.nextText();
                                    if(s == null || s.isEmpty()){
                                        s = "-";
                                    }
                                    trip.setTripDestination(s);
                                }
                                break;
                            case "TripStartTime":
                                if (insideTrip) {
                                    String s = xpp.nextText();
                                    if(s == null || s.isEmpty()){
                                        s = "-";
                                    }
                                    trip.setTripStartTime(s);
                                }
                                break;
                            case "AdjustedScheduleTime":
                                if (insideTrip) {
                                    String s = xpp.nextText();
                                    if(s == null || s.isEmpty()){
                                        s = "-";
                                    }
                                    trip.setAdjustedScheduleTime(s);
                                }
                                break;
                            case "AdjustmentAge":
                                if (insideTrip) {
                                    String data = xpp.nextText();
                                    if (data.trim().length() != 0) {
                                        trip.setAdjustmentAge(Float.parseFloat(data));
                                    }
                                }
                                break;
                            case "LastTripOfSchedule":
                                if (insideTrip) {
                                    String data = xpp.nextText();
                                    if (data.trim().length() != 0)
                                        trip.setLastTripOfSchedule(Boolean.parseBoolean(data));
                                }
                                break;
                            case "Latitude":
                                if (insideTrip) {
                                    String data = xpp.nextText();
                                    if (data.trim().length() != 0)
                                        trip.setLatitude(Double.parseDouble(data));
                                }
                                break;
                            case "Longitude":
                                if (insideTrip) {
                                    String data = xpp.nextText();
                                    if (data.trim().length() != 0)
                                        trip.setLongitude(Double.parseDouble(data));
                                }
                                break;
                            case "GPSSpeed":
                                if (insideTrip) {
                                    String data = xpp.nextText();
                                    if (data.trim().length() != 0)
                                        trip.setGPSSpeed(Float.parseFloat(data));
                                }
                                break;

                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        switch (xpp.getName()) {
                            case "Route":
                                route.setDirections(mRowData);
                                insideRoute = false;
                                break;
                            case "RouteDirection":
                                insideRouteDirect = false;
                                mRowData.add(routeDirection);
                                break;
                            case "Trip":
                                insideTrip = false;
                                if (trips != null)
                                    trips.add(trip);
                                break;
                            case "Trips":
                                insideTrips = false;
                                if (routeDirection != null)
                                    routeDirection.setTrips(trips);
                                break;
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            //If doInBackground returns 'success' that means parsing done successfully
            if (!s.equals("success")) {
                OCAppHelper.showToast(OCNextTripActivity.this, s);
            } else {
                adapter.notifyDataSetChanged();
                //parsing is successful but we get no data from server
                //showing error in a TextView
                if(mRowData.isEmpty())
                    tvError.setVisibility(View.VISIBLE);
            }
        }

        public InputStream getInputStream(URL url) {
            try {
                return url.openConnection().getInputStream();
            } catch (IOException e) {
                return null;
            }
        }
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
                startActivity(new Intent(OCNextTripActivity.this, OCStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(OCNextTripActivity.this, OCFavoritesActivity.class));
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
        final Dialog dialog = new Dialog(OCNextTripActivity.this);
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
