package com.gui.guiprogramming.movie;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toolbar;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.movie.movie_adapter.MovieListAdapter;
import com.gui.guiprogramming.movie.movie_async.MovieCallWSMovie;
import com.gui.guiprogramming.movie.movie_async.MovieDataFetchListener;
import com.gui.guiprogramming.movie.movie_helper.MovieAppHelper;
import com.gui.guiprogramming.movie.movie_helper.MovieImageFactory;
import com.gui.guiprogramming.movie.movie_model.Movie;
import com.gui.guiprogramming.movie.movie_util.MovieWebUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * MovieMainActivity contains two fragments, one for searching movies
 * and other one for showing favorite movies list
 */
public class MovieMainActivity extends Activity {

    SearchView svMovieInput;
    ListView lvMovies;

    MovieListAdapter listAdapter;
    List<Movie> mRowData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        svMovieInput = findViewById(R.id.svMovieInput);
        lvMovies = findViewById(R.id.lvMovies);

        mRowData = new ArrayList<>();
        listAdapter = new MovieListAdapter(mRowData, MovieMainActivity.this);
        lvMovies.setAdapter(listAdapter);

        svMovieInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //Called when user hits Search Icon on keyboard
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("QUERY", query);
//                    MovieAppHelper.showToast(getActivity(), "Searching for..." + query);
                //replacing white spaces with %20 -> URL encoding
                query = query.replaceAll(" ", "%20");
                getSearchResult(query); //get query result and set result to ListView
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(MovieMainActivity.this, MovieDetailActivity.class)
                        .putExtra(MovieAppHelper.INTENT_PASS_MOVIE_ID, mRowData.get(i).getImdbID())
                        .putExtra(MovieAppHelper.INTENT_PASS_TYPE, MovieAppHelper.INTENT_PASS_TYPE_SERVER));
            }
        });
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
            case R.id.menuStat:
                startActivity(new Intent(MovieMainActivity.this, MovieStatisticsActivity.class));
                break;
            case R.id.menuFavs:
                startActivity(new Intent(MovieMainActivity.this, MovieFavoritesActivity.class));
                break;
            case R.id.menuHelp:
                showHelpDialog();
                break;
        }
        return true;
    }


    /**
     * Showing a help dialog to user
     * */
    void showHelpDialog() {
        final Dialog dialog = new Dialog(MovieMainActivity.this);
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

    /**
     * Calling API
     * @param input entered SearchView text
     * */
    void getSearchResult(String input) {
        new MovieCallWSMovie(MovieMainActivity.this, MovieWebUtil.MOVIE_SEARCH + input, new MovieDataFetchListener() {
            //Retrieving and parsing result
            @Override
            public void onSuccess(String response) {
                try {
                    //JSON parsing
                    JSONObject jsonObject = new JSONObject(response);
                    String Response = jsonObject.getString("Response");
                    mRowData.clear();
                    if (Response.equals("True")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Search");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject data = jsonArray.getJSONObject(i);
                            String Title = data.getString("Title");
                            String Year = data.getString("Year");
                            String imdbID = data.getString("imdbID");
                            String Type = data.getString("Type");
                            String Poster = data.getString("Poster");
                            //Running async task to saving posters to local storage
                            new MovieImageFactory(Poster).execute();
                            Movie movie = new Movie();
                            movie.setImdbID(imdbID);
                            movie.setPoster(Poster);
                            movie.setYear(Year);
                            movie.setType(Type);
                            movie.setTitle(Title);
                            mRowData.add(movie);
                        }
                    } else {
                        MovieAppHelper.showToast(MovieMainActivity.this, jsonObject.getString("Error"));
                    }
                    listAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String errorMessage) {
                MovieAppHelper.showToast(MovieMainActivity.this, errorMessage);
            }
        }).execute();
    }
}
