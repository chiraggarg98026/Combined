package com.gui.guiprogramming.movie;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.movie.movie_helper.MovieAppHelper;

/**
 * This is a launcher activity
 * */
public class MovieSplashActivity extends Activity {

    int SPLASH_TIME_OUT = 3000; //Wait for 3 seconds on splash UI
    Thread splashThread; // Thread to wait 3 seconds and request permissions
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_splash);
        //AlertDialog instance to show it later
        builder = new AlertDialog.Builder(MovieSplashActivity.this);
        builder.setTitle("Permission request");

        splashThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SPLASH_TIME_OUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //Requesting permission to store movie poster to storage on post thread
                    //as per https://developer.android.com/training/permissions/requesting
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ContextCompat.checkSelfPermission(MovieSplashActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                // Permission is not granted
                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(MovieSplashActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    // Show an explanation to the user *asynchronously* -- don't block
                                    // this thread waiting for the user's response! After the user
                                    // sees the explanation, try again to request the permission.
                                    showPermissionAlertDialog();
                                } else {
                                    // No explanation needed; request the permission
                                    reqPermission();
                                }
                            } else {
                                // Permission has already been granted
                                goToNextActivity();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Starting thread to wait 3 seconds
        splashThread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToNextActivity();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MovieSplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    showPermissionAlertDialog();
                } else {
                    MovieAppHelper.showToast(this, "Won't be able to continue without permission, "
                            + "Go to Settings -> Applications -> Movie information -> Permission and allow permission to continue !");
                    finish();
                }
            }
        }
    }

    /**
     * This methods starts MovieMainActivity and finishes current one.
     * */
    void goToNextActivity() {
        startActivity(new Intent(MovieSplashActivity.this, MovieMainActivity.class));
        finish();
    }

    /**
     * Request permission dialog, system provided
     * */
    public void reqPermission() {
        ActivityCompat.requestPermissions(MovieSplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
    }

    /**
     * Showing "why do we need this permission" dialog to user
     * */
    void showPermissionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieSplashActivity.this);
        builder.setTitle("Permission request");
        builder.setMessage("This app needs WRITE_EXTERNAL_STORAGE permission to store your " +
                "favorite movies' details offline. Allow to continue!");
        builder.setCancelable(false);
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                reqPermission();
            }
        });
        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }

}
