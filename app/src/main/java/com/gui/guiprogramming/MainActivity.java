package com.gui.guiprogramming;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.gui.guiprogramming.food.FoodMainActivity;
import com.gui.guiprogramming.movie.MovieSplashActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        Button btnCBCApp = findViewById(R.id.btnCBCApp);
        Button btnFoodNutrientsApp = findViewById(R.id.btnFoodNutrientsApp);
        Button btnMovieInfoApp = findViewById(R.id.btnMovieInfoApp);
        Button btnOCTranspoApp = findViewById(R.id.btnOCTranspoApp);
        btnCBCApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO update
            }
        });
        btnFoodNutrientsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FoodMainActivity.class));
            }
        });
        btnMovieInfoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MovieSplashActivity.class));
            }
        });
        btnOCTranspoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO update
            }
        });
    }
}
