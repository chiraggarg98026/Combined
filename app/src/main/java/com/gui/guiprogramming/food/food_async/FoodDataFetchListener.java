package com.gui.guiprogramming.food.food_async;

/**
 * FoodDataFetchListener works as communicator between FoodCallWS to originated Activity
 * */
public interface FoodDataFetchListener {
    public void onSuccess(String response);
    public void onFail(String errorMessage);
}
