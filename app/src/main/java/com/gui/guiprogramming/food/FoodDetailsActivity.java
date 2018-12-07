package com.gui.guiprogramming.food;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.food.food_helper.FoodAppHelper;
import com.gui.guiprogramming.food.food_helper.FoodDBManager;
import com.gui.guiprogramming.food.food_model.Food;
import com.gui.guiprogramming.food.food_model.Nutrients;

public class FoodDetailsActivity extends Activity {

    LinearLayout linearDetails;
    Button btnAddToFav;
    String foodID, title, categoryLabel, category, tag;
    double energy, protein, fat, carbs;
    boolean isExists;
    String message = "";

    EditText edtTag;
    Dialog dialog;
    FoodDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Intent intent = getIntent();
        //reading serializable instance from intent
        Food food = (Food) intent.getSerializableExtra("DATA");
        Nutrients nutrients = food.getNutrients();
        Log.i("food", food.getLabel());
        linearDetails = findViewById(R.id.linearDetails);
        TextView tvRowFoodTitle = findViewById(R.id.tvRowFoodTitle);
        TextView tvRowFoodCategory = findViewById(R.id.tvRowFoodCategory);
        TextView tvFoodEnergy = findViewById(R.id.tvFoodEnergy);
        TextView tvFoodProtein = findViewById(R.id.tvFoodProtein);
        TextView tvFoodFat = findViewById(R.id.tvFoodFat);
        TextView tvFoodCarbs = findViewById(R.id.tvFoodCarbs);
        TextView tvFoodTag = findViewById(R.id.tvFoodTag);
        btnAddToFav = findViewById(R.id.btnAddToFav);
        foodID = food.getFoodId();
        title = food.getLabel();
        categoryLabel = food.getCategoryLabel();
        category = food.getCategory();
        String categoryText = food.getCategory() + " (" + categoryLabel + ")";
        energy = nutrients.getEnergy();
        protein = nutrients.getProtein();
        fat = nutrients.getFat();
        carbs = nutrients.getCarbs();
        tag = food.getTag();
        //units are set according to
        // https://developer.edamam.com/food-database-api-docs
        String energyString = energy + "kcal";
        String proteinString = protein + "g";
        String fatString = fat + "g";
        String carbsString = carbs + "g";
        tvRowFoodTitle.setText(title);
        tvRowFoodCategory.setText(categoryText);
        tvFoodEnergy.setText(energyString);
        tvFoodProtein.setText(proteinString);
        tvFoodFat.setText(fatString);
        tvFoodCarbs.setText(carbsString);
        if(tag != null && tag.trim().length() != 0){
            String tagToSet = "TAG: " + tag;
            tvFoodTag.setText(tagToSet);
        }else{
            tvFoodTag.setVisibility(View.GONE);
        }

        dbManager = new FoodDBManager(FoodDetailsActivity.this);
        dbManager.open();
        updateButtonText(dbManager);
        dbManager.close();

        btnAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.open();
                if (isExists) {
                    //Food exists, remove it
                    if (dbManager.removeFavorite(foodID)) {
                        message = getResources().getString(R.string.food_snackbar_text_item_removed);
                        setResult(FoodAppHelper.RESULT_ITEM_DELETED);
                    } else {
                        message = getResources().getString(R.string.food_snackbar_text_item_unable_remove);
                    }
                    showSnackBar(message);
                    updateButtonText(dbManager);
                    dbManager.close();
                } else {
                    //Food not exists, insert it
                    dialog = new Dialog(FoodDetailsActivity.this);
                    //We do not need title for custom dialog
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.food_dialog_add_tag);
                    edtTag = dialog.findViewById(R.id.edtTag);
                    edtTag.setText(title);
                    Button btnCancel = dialog.findViewById(R.id.btnCancel);
                    Button btnSet = dialog.findViewById(R.id.btnSet);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    btnSet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text = edtTag.getText().toString().trim();
                            if (text.length() != 0) {
                                if (dbManager.addToFav(foodID, title, energy, protein, fat, carbs,
                                        category, categoryLabel, text)
                                        != -1) {
                                    message = getResources().getString(R.string.food_snackbar_text_item_added);
                                } else {
                                    message = getResources().getString(R.string.food_snackbar_text_item_unable_add);
                                }
                            } else {
                                FoodAppHelper.showToast(FoodDetailsActivity.this, "Enter TAG to set!");
                            }
                            dialog.cancel();
                            showSnackBar(message);
                            updateButtonText(dbManager);
                            dbManager.close();
                        }
                    });
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
        });
    }

    void updateButtonText(FoodDBManager dbManager) {
        isExists = dbManager.isExists(foodID);
        if (isExists) {
            btnAddToFav.setText(getResources().getString(R.string.food_remove_from_favorites));
        } else {
            btnAddToFav.setText(getResources().getString(R.string.food_add_to_string_favorites));
        }
    }

    void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(linearDetails, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

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
                startActivity(new Intent(FoodDetailsActivity.this, FoodStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(FoodDetailsActivity.this, FoodFavoritesActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
                //handling onclick of actionBar back button
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    /**
     * Showing a help dialog to user
     * */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(FoodDetailsActivity.this);
        //We do not need title for custom dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting layout to dialog
        dialog.setContentView(R.layout.food_layout_help_dialog);
        //initializing widgets of custom dialog layout
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //showing dialog with MATCH_PARENT width.
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
