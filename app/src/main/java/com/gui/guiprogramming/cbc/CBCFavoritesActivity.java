package com.gui.guiprogramming.cbc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.cbc.cbc_adapter.CBCNewsListAdapter;
import com.gui.guiprogramming.cbc.cbc_model.CBCNewsStory;

import java.util.ArrayList;

public class CBCFavoritesActivity extends Activity {

    ListView lvFavArticles;
    //Custom list adapter
    CBCNewsListAdapter listAdapter;
    //Custom data set - model class
    ArrayList<CBCNewsStory> mRowData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbc_activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        lvFavArticles = findViewById(R.id.lvFavArticles);
        getArticlesFromDB(); // this function retrieves favorite movies from database
        lvFavArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /* startActivityForResult to detect whether
                 * user has removed article from offline or not
                 * result will be handled in onActivityResult
                 * */
                startActivityForResult(
                        new Intent(CBCFavoritesActivity.this, CBCDetailActivity.class)
                                .putExtra("DATA", mRowData.get(i)),
                        1);
            }
        });
    }

    /**
     * Getting all the articles saved from the database
     * */
    private void getArticlesFromDB() {
        CBCDBManager db = new CBCDBManager(CBCFavoritesActivity.this);
        db.open();
        mRowData = db.getAllArticles();
        listAdapter = new CBCNewsListAdapter(CBCFavoritesActivity.this, mRowData);
        lvFavArticles.setAdapter(listAdapter);
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
        if (resultCode == 1) {
            //Calling again to refresh ListView
            getArticlesFromDB();
        }
    }
}
