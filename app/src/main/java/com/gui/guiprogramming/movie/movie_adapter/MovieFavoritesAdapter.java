package com.gui.guiprogramming.movie.movie_adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gui.guiprogramming.R;
import com.gui.guiprogramming.movie.movie_model.Movie;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * MovieFavoritesAdapter is a custom ListAdapter, used to set custom layout to ListView
 * ViewHolder pattern is used
 */
public class MovieFavoritesAdapter extends BaseAdapter {

    private List<Movie> mRowData;
    private static LayoutInflater layoutInflater;

    /**
     * @param mRowData to set data into ListView
     * @param activity to inflate layout we need
     *                 to use instance of an Activity
     *                 -Pass current activity's this object
     * */
    public MovieFavoritesAdapter(List<Movie> mRowData, Activity activity) {
        this.mRowData = mRowData;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mRowData.size();
    }

    @Override
    public Object getItem(int i) {
        return mRowData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.movie_row_movie_list, null);
            holder = new ViewHolder();
            holder.tvMovieTitle = view.findViewById(R.id.tvMovieTitle);
            holder.tvReleaseYear = view.findViewById(R.id.tvReleaseYear);
            holder.tvType = view.findViewById(R.id.tvType);
            holder.imgPoster = view.findViewById(R.id.imgPoster);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Movie model = mRowData.get(i);
        String movieTitle = model.getTitle();
        String releaseYear = model.getYear();
        String type = model.getType();
        holder.tvMovieTitle.setText(movieTitle);
        holder.tvReleaseYear.setText(releaseYear);
        holder.tvType.setText(type);
        File storagePath = new File(Environment.getExternalStorageDirectory().getPath() +
                File.separator + "DCIM" + File.separator + "GP_MOVIE_IMAGES" + File.separator + model.getPoster());
        //Check if poster file is exists or not on local storage,
        //if exists then load image
        if(storagePath.exists()){
            Picasso.get().load(storagePath).into(holder.imgPoster);
        }
        return view;
    }

    static class ViewHolder {
        TextView tvMovieTitle;
        TextView tvReleaseYear;
        TextView tvType;
        ImageView imgPoster;
    }
}