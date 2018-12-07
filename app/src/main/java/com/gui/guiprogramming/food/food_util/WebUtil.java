package com.gui.guiprogramming.food.food_util;

/**
 * WebUtil class contains URL constants, can be used to anywhere in module
 */
public class WebUtil {

    //edamam API KEY = app_id=42152d95&app_key=782cb9a529aaaba63596a582b5ba67ce
    //SAMPLE URL = https://api.edamam.com/api/food-database/parser
    // ?app_id=42152d95&app_key=782cb9a529aaaba63596a582b5ba67ce&ingr=red%20apple

    //HOST URL
    private static final String DOMAIN_FOOD = "https://api.edamam.com/api/food-database/parser" +
            "?app_id=42152d95&app_key=782cb9a529aaaba63596a582b5ba67ce";

    //URL for searching ingredient
    public static final String INGR_SEARCH = DOMAIN_FOOD + "&ingr=";

    /**
     * RequestMethod contains request method types,
     * Used in FoodWebApiCall.java
     * Used to set request method of HTTP request
     */
    public static class RequestMethod {
        public static final String GET = "Get";
        public static final String POST = "Post";
    }

}
