package com.gui.guiprogramming.food;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.food.food_adapter.FoodFavoriteListAdapter;
import com.gui.guiprogramming.food.food_helper.FoodAppHelper;
import com.gui.guiprogramming.food.food_helper.FoodDBManager;
import com.gui.guiprogramming.food.food_model.Food;

import java.util.ArrayList;


public class FoodFavoritesActivity extends Activity {

    ListView lvFavIngr;
    //Custom list adapter
    FoodFavoriteListAdapter adapter;
    //Custom data set - model class
    ArrayList<Food> mRowData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity_favorites);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        lvFavIngr = findViewById(R.id.lvFavIngr);
        getFavorites(); // this function retrieves favorite food items from database
        lvFavIngr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /* startActivityForResult to detect whether
                 * user has removed food item from favorites or not
                 * result will be handled in onActivityResult
                 * */
                startActivityForResult(new Intent(FoodFavoritesActivity.this, FoodDetailsActivity.class)
                                .putExtra("DATA", mRowData.get(i)),
                        FoodAppHelper.RESULT_ITEM_DELETED);
            }
        });
    }

    void getFavorites() {
        FoodDBManager dbManager = new FoodDBManager(FoodFavoritesActivity.this);
        dbManager.open(); //require to open a database connection
        mRowData = dbManager.getFavoritesList(); //Getting favorite food items
        adapter = new FoodFavoriteListAdapter(FoodFavoritesActivity.this, mRowData);
        lvFavIngr.setAdapter(adapter);
        //closing connection after usage
        dbManager.close();
    }

    /**
     * Handling startActivityForResult() method's result
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If this condition is true, that means one row is deleted
        // from database and we need to update our ListView
        if (resultCode == FoodAppHelper.RESULT_ITEM_DELETED) {
            //Calling again to refresh ListView
            getFavorites();
        }
    }
}
