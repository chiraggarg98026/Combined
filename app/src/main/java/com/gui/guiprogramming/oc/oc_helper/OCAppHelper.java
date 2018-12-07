package com.gui.guiprogramming.oc.oc_helper;

import android.app.Activity;
import android.widget.Toast;

/**
 * OCAppHelper contains constants and helper methods which can be useful anywhere in module
 */
public class OCAppHelper {

    //used to detect whether user removes an existing TRIP from the database, and update the list
    public static final int RESULT_DELETED = 3;

    public static void showToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    /**
     * @param errorCode Error code received from the response
     *                  The error messages are given in the documentation
     *                  http://www.octranspo.com/developers/documentation
     *                  ERROR CODE AND IT'S MESSAGE
     *                  <p>
     *                  Error	    Note
     *                  1	    Invalid API key
     *                  2	    Unable to query data source
     *                  10	    Invalid stop number
     *                  11      Invalid route number
     *                  12      Stop does not service route </p>
     */
    public static String resolveErrorCode(int errorCode) {
        String errorMessage = "";
        switch (errorCode) {
            case 1:
                errorMessage = "Invalid API key";
                break;
            case 2:
                errorMessage = "Unable to query data source";
                break;
            case 10:
                errorMessage = "Invalid stop number";
                break;
            case 11:
                errorMessage = "Invalid route number";
                break;
            case 12:
                errorMessage = "Stop does not service route";
                break;
        }
        return errorMessage;
    }

}
