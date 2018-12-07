package com.gui.guiprogramming.movie.movie_util;

/**
 * MovieWebUtil class contains URL constants, can be used to anywhere in module
 * */
public class MovieWebUtil {

    //IMDb API KEY = d18bb424
    //SAMPLE URL = http://www.omdbapi.com/?i=tt3896198&apikey=d18bb424
    //http://www.omdbapi.com/?y=2018&apikey=d18bb424

    //HOST URL
    private static final String DOMAIN_MOVIE_INFO = "http://www.omdbapi.com/?apikey=d18bb424";

    //URL for search for movie
    public static final String MOVIE_SEARCH = DOMAIN_MOVIE_INFO + "&s=";

    //URL for getting particular movie detail
    public static final String MOVIE_DETAIL = DOMAIN_MOVIE_INFO + "&i=";

    /**
     * RequestMethod contains request method types,
     * Used in MovieWebApiCall.java
     * Used to set request method of HTTP request
     * */
    public static class RequestMethod {
        public static final String GET = "Get";
        public static final String POST = "Post";
    }

}
