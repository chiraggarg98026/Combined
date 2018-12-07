package com.gui.guiprogramming.movie.movie_async;

import android.app.Activity;
import android.util.Log;

import com.gui.guiprogramming.movie.movie_util.MovieWebUtil;
import com.gui.guiprogramming.movie.movie_helper.MovieAppHelper;

/**
 * This is the only class used to call APIs in this module
 * Each API is called using this class
 * It is a sub-class of AsyncTask
 * */
public class MovieCallWSMovie extends MovieWebApiCall {

    //interface used to retrieve API response,
    // and to use response in requesting activities.
    private MovieDataFetchListener movieDataFetchListener;


    /**
     * Request method will be set to "GET".
     * @param activity The activity instance from where you are calling an API
     * @param url URL of an API
     * @param movieDataFetchListener listener to receive result of an API
     * @see MovieDataFetchListener
     * */
    public MovieCallWSMovie(Activity activity, String url, MovieDataFetchListener movieDataFetchListener){
        super(activity,url, true);
        this.movieDataFetchListener = movieDataFetchListener;
        Log.i("Requesting_URL ==>",url);
    }

    /**
     * Request method will be set to "POST".
     * @param activity The activity instance from where you are calling an API
     * @param url URL of an API
     * @param params parameters to set in a request
     * @param movieDataFetchListener listener to receive result of an API
     * @see MovieDataFetchListener
     * */
    public MovieCallWSMovie(Activity activity, String url, String params, MovieDataFetchListener movieDataFetchListener){
        super(activity, url, true, MovieWebUtil.RequestMethod.POST, params);
        this.movieDataFetchListener = movieDataFetchListener;
        Log.i("Requesting_URL ==>",url + " with parameters ==>" + params);
    }

    /**
     * Request method will be set to "POST".
     * @param activity The activity instance from where you are calling an API
     * @param url URL of an API
     * @param reqMethod request method to set
     * @param params parameters to set in a request
     * @param movieDataFetchListener listener to receive result of an API
     * @see MovieDataFetchListener
     * */
    public MovieCallWSMovie(Activity activity, String url, String reqMethod, String params, MovieDataFetchListener movieDataFetchListener){
        super(activity, url, true, reqMethod, params);
        this.movieDataFetchListener = movieDataFetchListener;
//        Log.i("Requesting_URL ==>",url + " with parameters ==>" + params);
    }

    @Override
    void onProgressUpdate(int progress) {
//        System.out.print("Receiving data...Progress ===> " + progress);
    }

    /**
     * onFinishSuccess will receive 'response' when HTTP response is received from called API
     * */
    @Override
    void onFinishSuccess(String response) {
        Log.i("Response_URL ==>",mURL + " = " + response);
        if (response.startsWith("failed to connect to") || response.startsWith("Unable to resolve")){
            MovieAppHelper.showToast(mActivity,"Please check your internet connection & restart app!");
        }
        movieDataFetchListener.onSuccess(response);
    }

    /**
     * onFinishFail will receive 'errorMessage' when no HTTP response is received from called API
     * */
    @Override
    void onFinishFail(String errorMessage) {
        Log.i("ERROR_URL ==>",mURL + " = " + errorMessage);
        movieDataFetchListener.onFail(errorMessage);
    }
}
