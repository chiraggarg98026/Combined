package com.gui.guiprogramming.oc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.oc.oc_adapter.OCSavedTripsAdapter;
import com.gui.guiprogramming.oc.oc_helper.OCAppHelper;
import com.gui.guiprogramming.oc.oc_helper.OCDBManager;
import com.gui.guiprogramming.oc.oc_model.OCDBMap;

import java.util.ArrayList;

public class OCFavoritesActivity extends Activity {

    ListView lvFavMovies;
    OCSavedTripsAdapter adapter;
    ArrayList<OCDBMap> mRowData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oc_activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        lvFavMovies = findViewById(R.id.lvFavMovies);
        getMoviesFromDB();
//        getMoviesFromDB(); // this function retrieves favorite movies from database
        lvFavMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /* startActivityForResult to detect whether
                 * user has removed movie from favorites or not
                 * result will be handled in onActivityResult
                 * */
                OCDBMap map = mRowData.get(i);
                startActivityForResult(
                        new Intent(OCFavoritesActivity.this, OCDetailsActivity.class)
                                .putExtra("stopNo", String.valueOf(map.getStopNo()))
                                .putExtra("stopName", map.getStopName())
                                .putExtra("RouteLabel", map.getRouteLabel())
                                .putExtra("Direction", map.getDirection())
                                .putExtra("RequestProcessingTime", map.getReqProcessingTime())
                                .putExtra("RouteNo", map.getRouteNo())
                                .putExtra("DATA", map.getTrip()),

                        OCAppHelper.RESULT_DELETED);
            }
        });
    }

    /**
     * Getting list of movies and set to ListView
     */
    private void getMoviesFromDB() {
        OCDBManager db = new OCDBManager(OCFavoritesActivity.this);
        db.open();
        mRowData = db.getTrips();
        adapter = new OCSavedTripsAdapter(OCFavoritesActivity.this, mRowData);
        lvFavMovies.setAdapter(adapter);
        db.close();
    }

    /**
     * Handling startActivityForResult() method's result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If this condition is true, that means one row is deleted
        // from database and we need to update our ListView
        if (resultCode == OCAppHelper.RESULT_DELETED) {
            //Calling again to refresh ListView
            getMoviesFromDB();
        }
    }
}
