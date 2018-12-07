package com.gui.guiprogramming.movie.movie_helper;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * MovieImageFactory is an AsyncTask used to storing poster-images to local storage
 * */
public class MovieImageFactory extends AsyncTask<Void, Integer, String> {

    //URL to fetch poster-image
    private String imgURL;

    /**
     * @param imgURL URL to fetch poster-image
     * */
    public MovieImageFactory(String imgURL) {
        this.imgURL = imgURL;
    }

    @Override
    protected String doInBackground(Void... voids) {
        InputStream input = null;
        try {
            URL url = new URL(imgURL);

            //File name to be stored with extension
            //i.e. if URL is
            // https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg
            //then file name will be MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg
            String fileName = imgURL.substring(imgURL.lastIndexOf('/') + 1, imgURL.length());
            File storagePath = new File(Environment.getExternalStorageDirectory().getPath() +
                    File.separator + "DCIM" + File.separator + "GP_MOVIE_IMAGES");
            //If folder does not exists, then create a new one
            if(!storagePath.exists()){
                storagePath.mkdirs();
            }

            //If poster-image already exists, then return
            if(new File(storagePath + "/" + fileName).exists()){
                return null;
            }

            //Downloading image using 1024 sized buffer,
            // and saving it to directory : GP_MOVIE_IMAGES inside DCIM directory on local storage
            input = url.openStream();
            OutputStream output = new FileOutputStream(storagePath + "/" + fileName);
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                //reading file in byte form.
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                }
            } finally {
                output.close();
                input.close();
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
