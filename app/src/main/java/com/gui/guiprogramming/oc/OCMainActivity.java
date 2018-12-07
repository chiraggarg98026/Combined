package com.gui.guiprogramming.oc;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_adapter.OCRouteListAdapter;
import com.gui.guiprogramming.oc.oc_helper.OCAppHelper;
import com.gui.guiprogramming.oc.oc_model.Route;
import com.gui.guiprogramming.oc.oc_util.WebUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OCMainActivity extends Activity {

    SearchView svMovieInput;
    ListView lvMovies;
    TextView tvStopName;
    LinearLayout llShort;
    OCRouteListAdapter listAdapter;
    List<Route> mRowData;
    String StopNo, stopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        svMovieInput = findViewById(R.id.svMovieInput);
        lvMovies = findViewById(R.id.lvMovies);
        tvStopName = findViewById(R.id.tvStopName);
        llShort = findViewById(R.id.llShort);

        mRowData = new ArrayList<>();
        listAdapter = new OCRouteListAdapter(OCMainActivity.this, mRowData);
        lvMovies.setAdapter(listAdapter);

        svMovieInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Called when user hits Search Icon on keyboard
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("QUERY", query);
//                    OCAppHelper.showToast(OCMainActivity.this, "Searching for..." + query);
                //replacing white spaces with %20 -> URL encoding
                query = query.replaceAll(" ", "%20");
                getSearchResult(query); //get query result and set result to ListView
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(OCMainActivity.this, OCNextTripActivity.class)
                        .putExtra("stopNo", StopNo)
                        .putExtra("stopName",stopName)
                        .putExtra("DATA", mRowData.get(i)));
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
                startActivity(new Intent(OCMainActivity.this, OCStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(OCMainActivity.this, OCFavoritesActivity.class));
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
        final Dialog dialog = new Dialog(OCMainActivity.this);
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

    /**
     * Calling API
     *
     * @param input entered SearchView text
     */
    void getSearchResult(String input) {
        new Async(input).execute();
    }

    public class Async extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog = new ProgressDialog(OCMainActivity.this);
        String mURL;
        String input;

        public Async(String input) {
            this.input = input;
            this.mURL = WebUtil.GET_ROUTE_SUMMARY + "&stopNo=" + input;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRowData.clear();
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(mURL);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(getInputStream(url), "UTF_8");
                boolean insideRoute = false;
                int eventType = xpp.getEventType();
                Route route = null;
                String routeNo, directID, direct, heading;
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
                            case "StopNo":
                                StopNo = xpp.nextText();
                                break;
                            case "StopDescription":
                                stopName = xpp.nextText();
                                break;
                            case "Route":
                                insideRoute = true;
                                route = new Route();
                                break;
                            case "RouteNo":
                                if (insideRoute) {
                                    routeNo = xpp.nextText();
                                    Log.i("Route no==>", routeNo);
                                    route.setRouteNo(Integer.parseInt(routeNo));
                                }
                                break;
                            case "DirectionID":
                                if (insideRoute) {
                                    directID = xpp.nextText();
                                    Log.i("Route DirectionID==>", directID);
                                    route.setDirectionID(Integer.parseInt(directID));
                                }
                                break;
                            case "Direction":
                                if (insideRoute) {
                                    direct = xpp.nextText();
                                    Log.i("Route Direction==>", direct);
                                    route.setDirection(direct);
                                }
                                break;
                            case "RouteHeading":
                                if (insideRoute) {
                                    heading = xpp.nextText();
                                    Log.i("Route RouteHeading==>", heading);
                                    route.setRouteHeading(heading);
                                }
                                break;
                        }
                    } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("Route")) {
                        mRowData.add(route);
                        insideRoute = false;
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
            if (!s.equals("success")) {
                llShort.setVisibility(View.GONE);
                OCAppHelper.showToast(OCMainActivity.this, s);
            } else {
                llShort.setVisibility(View.VISIBLE);
                tvStopName.setText(stopName);
            }
            listAdapter.notifyDataSetChanged();
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
