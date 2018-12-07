package com.gui.guiprogramming.movie.movie_async;

/**
 * MovieDataFetchListener works as communicator between MovieCallWSMovie to originated Activity
 * */
public interface MovieDataFetchListener {
    public void onSuccess(String response);
    public void onFail(String errorMessage);
}
