package com.gui.guiprogramming.movie.movie_async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.movie.movie_util.MovieWebUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MovieWebApiCall helps call APIs in this module
 * Each API is called through this class
 */
public abstract class MovieWebApiCall extends AsyncTask<Void, Integer, String> {

    //Abstract method declaration for communicating with it's chlid class MovieCallWSMovie
    abstract void onProgressUpdate(int progress);
    abstract void onFinishSuccess(String response);
    abstract void onFinishFail(String errorMessage);
    //Abstract method declaration ends here

    Activity mActivity;
    String mURL;
    String requestMethod = "";
    String parameters;
    boolean isProgressVisible;

    ProgressDialog progressDialog;

    /**
     * Request method will be set to "GET".
     * @param mActivity The activity instance from where you are calling an API
     * @param mURL URL of an API
     * @param isProgressVisible set it to true if you want to display progress dialog
     * */
    public MovieWebApiCall(Activity mActivity, String mURL, boolean isProgressVisible) {
        this.mActivity = mActivity;
        this.mURL = mURL;
        this.isProgressVisible = isProgressVisible;

        progressDialog = new ProgressDialog(mActivity);

        if (isProgressVisible) {
            //showing progress dialog and preventing user to interact with UI
            progressDialog.setMessage(mActivity.getResources().getString(R.string.movie_please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

    }


    /**
     * @param mActivity The activity instance from where you are calling an API
     *      * @param mURL URL of an API
     * @param parameters parameters to set in a request
     * @param requestMethod request method to set
     * */
    public MovieWebApiCall(Activity mActivity, String mURL, boolean isProgressVisible, String requestMethod, String parameters) {
        this.mActivity = mActivity;
        this.mURL = mURL;
        this.isProgressVisible = isProgressVisible;
        this.requestMethod = requestMethod;
        this.parameters = parameters;

        progressDialog = new ProgressDialog(mActivity);

        if (isProgressVisible) {
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    /**
     * To do stuff before executing doInBackground
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * To do stuff during background execution
     * */
    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection httpURLConnection = null;
        URL url = null;
        String response = "";
        try {
            url = new URL(mURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            if (requestMethod.equals(MovieWebUtil.RequestMethod.POST)) {
                response = executePostRequest(parameters, httpURLConnection);
            } else {
                response = executeGetRequest(httpURLConnection);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        onProgressUpdate(values[0]);
    }

    /**
     * To do stuff after executing doInBackground
     * */
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        //Dismissing progress dialog if it was shown
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        //Passing received result to MovieCallWSMovie class by abstract methods
        //If response get successfully,
        // pass it to onFinishSuccess, otherwise onFinishFail
        if (!response.equals("")) {
            onFinishSuccess(response);
        } else {
            onFinishFail("No Response!");
        }
    }

    /**
     * To execute API using POST request method
     * */
    private String executePostRequest(String params, HttpURLConnection httpURLConnection) {
        String response = "";
        DataOutputStream dataOutputStream = null;
        InputStream inputStream = null;
        try {
            //setting request method to post
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            //dataOutputStream used to attach parameters to POST request in BODY
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(params);
            dataOutputStream.flush();
            dataOutputStream.close();
            inputStream = httpURLConnection.getInputStream();
            response = getString(inputStream);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * To execute API using GET request method
     * */
    private String executeGetRequest(HttpURLConnection httpURLConnection) {
        InputStream inputStream = null;
        try {
            httpURLConnection.setRequestMethod("GET");
            inputStream = (InputStream) httpURLConnection.getContent();
            return getString(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * Fetching string from inputStream
     * @param inputStream HTTP response contents
     * @return response in string form
     * */
    private static String getString(InputStream inputStream) throws IOException {
        //Using buffered reader to read InputStream
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = ""; //temporary string to read a single line form inputStream
        //using StringBuilder to concatenate all the lines to a single result
        StringBuilder result = new StringBuilder();

        //reading input stream line by line
        while ((line = bufferedReader.readLine()) != null)
            //appending each line to result
            result.append(line);

        inputStream.close();
        //returning the result
        return result.toString();
    }
}
