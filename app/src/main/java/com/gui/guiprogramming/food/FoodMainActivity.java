package com.gui.guiprogramming.food;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.food.food_adapter.FoodSearchResultListAdapter;
import com.gui.guiprogramming.food.food_async.FoodCallWS;
import com.gui.guiprogramming.food.food_async.FoodDataFetchListener;
import com.gui.guiprogramming.food.food_helper.FoodAppHelper;
import com.gui.guiprogramming.food.food_model.Food;
import com.gui.guiprogramming.food.food_model.Nutrients;
import com.gui.guiprogramming.food.food_util.WebUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodMainActivity extends Activity {

    SearchView svMovieInput;
    FoodSearchResultListAdapter adapter;
    ArrayList<Food> mRowData;
    ListView lvResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        svMovieInput = findViewById(R.id.svMovieInput);
        lvResults = findViewById(R.id.lvResults);
        mRowData = new ArrayList<>();
        adapter = new FoodSearchResultListAdapter(FoodMainActivity.this, mRowData);
        lvResults.setAdapter(adapter);
        svMovieInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Called when user hits Search Icon on keyboard
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("QUERY", query);
//                    FoodAppHelper.showToast(getActivity(), "Searching for..." + query);
                //replacing white spaces with %20 -> URL encoding
                query = query.replaceAll(" ", "%20");
                getIngredientDetails(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(FoodMainActivity.this, FoodDetailsActivity.class)
                        //passing Food class object as Serializable
                        .putExtra("DATA", mRowData.get(position)));
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
                startActivity(new Intent(FoodMainActivity.this, FoodStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(FoodMainActivity.this, FoodFavoritesActivity.class));
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
        final Dialog dialog = new Dialog(FoodMainActivity.this);
        //We do not need title for custom dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting layout to dialog
        dialog.setContentView(R.layout.food_layout_help_dialog);
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

    void getIngredientDetails(String query) {
        new FoodCallWS(FoodMainActivity.this, WebUtil.INGR_SEARCH + query, new FoodDataFetchListener() {
            @Override
            public void onSuccess(String response) {
                Log.i("res", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    mRowData.clear();
                    JSONArray parsed = jsonObject.getJSONArray("parsed");
                    if (parsed.length() > 0) {
                        for (int i = 0; i < parsed.length(); i++) {
                            JSONObject food = parsed.getJSONObject(i).getJSONObject("food");
                            String foodId = food.getString("foodId");
                            String label = food.getString("label");
                            String category = food.getString("category");
                            String categoryLabel = food.getString("categoryLabel");
                            JSONObject nutrients = food.getJSONObject("nutrients");
                            double energy = 0, protein = 0, fat = 0, carbs = 0;
                            //safe checking for nutrients
                            if (nutrients.has("ENERC_KCAL"))
                                energy = nutrients.getDouble("ENERC_KCAL");
                            if (nutrients.has("PROCNT"))
                                protein = nutrients.getDouble("PROCNT");
                            if (nutrients.has("FAT"))
                                fat = nutrients.getDouble("FAT");
                            if (nutrients.has("CHOCDF"))
                                carbs = nutrients.getDouble("CHOCDF");
                            Nutrients nut = new Nutrients();
                            nut.setEnergy(energy);
                            nut.setFat(fat);
                            nut.setProtein(protein);
                            nut.setCarbs(carbs);
                            Food data = new Food();
                            data.setFoodId(foodId);
                            data.setLabel(label);
                            data.setCategory(category);
                            data.setCategoryLabel(categoryLabel);
                            data.setNutrients(nut);
                            mRowData.add(data);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        FoodAppHelper.showToast(FoodMainActivity.this, "Failed to get results!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String errorMessage) {

            }
        }).execute();
    }


}

