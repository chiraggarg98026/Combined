package com.gui.guiprogramming.movie;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gui.guiprogramming.movie.movie_util.MovieWebUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.movie.movie_adapter.MovieDBManager;
import com.gui.guiprogramming.movie.movie_async.MovieCallWSMovie;
import com.gui.guiprogramming.movie.movie_async.MovieDataFetchListener;
import com.gui.guiprogramming.movie.movie_helper.MovieAppHelper;
import com.gui.guiprogramming.movie.movie_model.Movie;

/**
 * MovieDetailActivity shows all the detailed information about particular movie
 * */
public class MovieDetailActivity extends Activity {

    ImageView imgMoviePoster;
    TextView tvMovieTitle, tvShowType, tvMovieYear, tvMovieGenre, tvMovieIMDBRating,
            tvMovieLanguage, tvMoviePlot, tvMovieRuntime, tvMovieCountry;
    LinearLayout relPosterBG;
    Button btnAddToFav;

    String Title, Year, Poster, Type, imdbID, Runtime, Genre,
            Plot, Language, Country, imdbRating;
    int movieRuntime;
    String IMDbID, TYPE;
    Intent intent;
    boolean isExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_movie_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        intent = getIntent();
        TYPE = intent.getStringExtra(MovieAppHelper.INTENT_PASS_TYPE);
        IMDbID = intent.getStringExtra(MovieAppHelper.INTENT_PASS_MOVIE_ID);
        relPosterBG = findViewById(R.id.relPosterBG);
        imgMoviePoster = findViewById(R.id.imgMoviePoster);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvShowType = findViewById(R.id.tvShowType);
        tvMovieYear = findViewById(R.id.tvMovieYear);
        tvMovieGenre = findViewById(R.id.tvMovieGenre);
        tvMovieIMDBRating = findViewById(R.id.tvMovieIMDBRating);
        tvMovieLanguage = findViewById(R.id.tvMovieLanguage);
        tvMoviePlot = findViewById(R.id.tvMoviePlot);
        tvMovieRuntime = findViewById(R.id.tvMovieRuntime);
        tvMovieCountry = findViewById(R.id.tvMovieCountry);
        btnAddToFav = findViewById(R.id.btnAddToFav);

        //If TYPE = SERVER then get details from API
        // otherwise get details from local database
        if (TYPE.equals(MovieAppHelper.INTENT_PASS_TYPE_SERVER)) {
            getMovieDetails(IMDbID);
        } else if (TYPE.equals(MovieAppHelper.INTENT_PASS_TYPE_LOCAL)) {
            getMovieDetailsFromDB(IMDbID);
        }

        MovieDBManager movieDbManager = new MovieDBManager(MovieDetailActivity.this);
        movieDbManager.open();
        isExists = movieDbManager.isExists(IMDbID);
        if(isExists){
            btnAddToFav.setText(getResources().getString(R.string.movie_remove_from_favorites));
        }else{
            btnAddToFav.setText(getResources().getString(R.string.movie_add_to_string_favorites));
        }
        movieDbManager.close();
        btnAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDBManager movieDbManager = new MovieDBManager(MovieDetailActivity.this);
                movieDbManager.open();
                String message = "";
                if(isExists){
                    Log.d("MOVIE", "exists, removing...");
                    if(movieDbManager.removeFavorite(imdbID)){
                        Log.d("MOVIE", "Movie removed!");
                        message = getResources().getString(R.string.movie_snackbar_text_movie_removed);
                        setResult(MovieAppHelper.RESULT_MOVIE_DELETED);
                    }else{
                        message = getResources().getString(R.string.movie_snackbar_text_movie_unable_remove);
                    }
                }else{
                    String fileName = Poster.substring(Poster.lastIndexOf('/') + 1, Poster.length());
                    if (movieDbManager.addMovie(imdbID, Title, fileName,
                            Year, Plot, Type, Language, imdbRating, Genre, movieRuntime, Country)
                            != -1) {
                        Log.d("MOVIE", "Movie added!");
                        message = getResources().getString(R.string.movie_snackbar_text_movie_added);
                    }else{
                        message = getResources().getString(R.string.movie_snackbar_text_movie_unable_add);
                    }
                }
                Log.d("MOVIE", "MSG" + message);
                Snackbar snackbar = Snackbar.make(relPosterBG, message, Snackbar.LENGTH_SHORT);
                snackbar.show();
                isExists = movieDbManager.isExists(IMDbID);
                if(isExists){
                    btnAddToFav.setText(getResources().getString(R.string.movie_remove_from_favorites));
                }else{
                    btnAddToFav.setText(getResources().getString(R.string.movie_add_to_string_favorites));
                }
                movieDbManager.close();
            }
        });
    }

    /**
     * to get favorite movie detail using API
     * @param IMDbID to filter movies
     * */
    void getMovieDetails(String IMDbID) {
        //Running async task
        new MovieCallWSMovie(MovieDetailActivity.this, MovieWebUtil.MOVIE_DETAIL + IMDbID, new MovieDataFetchListener() {
            //Retrieving and parsing result
            @Override
            public void onSuccess(String response) {
                try {
                    //JSON parsing
                    JSONObject jsonObject = new JSONObject(response);
                    String Response = jsonObject.getString("Response");
                    if (Response.equals("True")) {
                        Title = jsonObject.getString("Title");
                        Year = jsonObject.getString("Year");
                        imdbID = jsonObject.getString("imdbID");
                        Runtime = jsonObject.getString("Runtime");
                        movieRuntime = Integer.parseInt(Runtime.split(" ")[0]);
                        Genre = jsonObject.getString("Genre");
                        Plot = jsonObject.getString("Plot");
                        Language = jsonObject.getString("Language");
                        Country = jsonObject.getString("Country");
                        imdbRating = jsonObject.getString("imdbRating");
                        Poster = jsonObject.getString("Poster");
                        Type = jsonObject.getString("Type");
                        Picasso.get().load(Poster).into(imgMoviePoster); //Loading image
                        Picasso.get().load(Poster).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                relPosterBG.setBackground(new BitmapDrawable(bitmap));
                                relPosterBG.getBackground().setAlpha(50);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                        tvMovieTitle.setText(Title);
                        tvMovieYear.setText(Year);
                        tvMovieGenre.setText(Genre);
                        tvMoviePlot.setText(Plot);
                        tvMovieLanguage.setText(Language);
                        tvMovieCountry.setText(Country);
                        tvMovieIMDBRating.setText(imdbRating);
                        tvMovieRuntime.setText(Runtime);
                        tvShowType.setText(Type);
                    } else {
                        MovieAppHelper.showToast(MovieDetailActivity.this, jsonObject.getString("Error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String errorMessage) {
                MovieAppHelper.showToast(MovieDetailActivity.this, errorMessage);
            }
        }).execute();
    }

    /**
     * to get favorite movie detail from database
     * @param IMDbID to filter movies
     * */
    void getMovieDetailsFromDB(String IMDbID) {
        MovieDBManager movieDbManager = new MovieDBManager(MovieDetailActivity.this);
        movieDbManager.open();
        Movie movie = movieDbManager.getMovieDetails(IMDbID);
        imdbID = movie.getImdbID();
        Title = movie.getTitle();
        Year = movie.getYear();
        Genre = movie.getGenre();
        Plot = movie.getPlot();
        Language = movie.getLanguage();
        Country = movie.getCountry();
        imdbRating = movie.getRating();
        movieRuntime = movie.getRuntime();
        Type = movie.getType();
        Poster = movie.getPoster();
        Runtime = movieRuntime + " min";

        tvMovieTitle.setText(Title);
        tvMovieYear.setText(Year);
        tvMovieGenre.setText(Genre);
        tvMoviePlot.setText(Plot);
        tvMovieLanguage.setText(Language);
        tvMovieCountry.setText(Country);
        tvMovieIMDBRating.setText(imdbRating);
        tvMovieRuntime.setText(Runtime);
        tvShowType.setText(Type);
        File storagePath = new File(Environment.getExternalStorageDirectory().getPath() +
                File.separator + "DCIM" + File.separator + "GP_MOVIE_IMAGES" + File.separator + Poster);
        //Check if poster file is exists or not
        if (storagePath.exists()) {
            Picasso.get().load(storagePath).into(imgMoviePoster); //loading image
            Picasso.get().load(storagePath).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    relPosterBG.setBackground(new BitmapDrawable(bitmap));
                    relPosterBG.getBackground().setAlpha(50);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
        movieDbManager.close();
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
            case android.R.id.home: //Back button on action bar
                onBackPressed();
                break;
            case R.id.menuFavs:
                startActivity(new Intent(MovieDetailActivity.this, MovieFavoritesActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
            case R.id.menuStat:
                startActivity(new Intent(MovieDetailActivity.this, MovieStatisticsActivity.class));
                break;
        }
        return true;
    }

    /**
     * Showing a help dialog to user
     * */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(MovieDetailActivity.this);
        //We do not need title for custom dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setting layout to dialog
        dialog.setContentView(R.layout.movie_layout_help_dialog);
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
