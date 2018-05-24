package com.udacity.popularmovies.Utils;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sunand on 5/16/18.
 */

public class MoviesAsync extends AsyncTask<String, Void, ArrayList<Movie>> {

    private IMoviesAsync activity;

    public MoviesAsync(IMoviesAsync activity){
        this.activity = activity;

    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        HttpURLConnection connection = null;
        ArrayList<Movie> result = new ArrayList<>();
        try {

            if (isCancelled())
                return null;

            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String json = IOUtils.toString(connection.getInputStream(), "UTF8");
                    result.addAll(JsonUtil.ParseMovies(json));
                }
            } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        activity.showProgress();
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);

        activity.setupData(movies);
    }
}


