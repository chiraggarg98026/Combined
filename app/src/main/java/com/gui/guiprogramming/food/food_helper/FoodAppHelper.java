package com.gui.guiprogramming.food.food_helper;

import android.app.Activity;
import android.widget.Toast;

/**
 * FoodAppHelper contains constants and helper methods which can be useful anywhere in module
 */
public class FoodAppHelper {

    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public static final int RESULT_ITEM_DELETED = 3;

}
