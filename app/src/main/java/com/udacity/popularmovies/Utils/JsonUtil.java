package com.udacity.popularmovies.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunand on 5/16/18.
 */

public class JsonUtil {

    public static List<Movie> ParseMovies(String json) throws JSONException {

        List<Movie> movieList = new ArrayList<>();
        Movie movie;

        JSONObject root = new JSONObject(json);
        JSONArray results = root.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {

            JSONObject movieObj = results.getJSONObject(i);

            movie = new Movie();
            movie.setId(movieObj.getLong("id"));
            movie.setTitle(movieObj.getString("title"));
            movie.setRelease_date(movieObj.getString("release_date"));
            movie.setPoster_path(movieObj.getString("poster_path"));
            movie.setVote_average(movieObj.getDouble("vote_average"));
            movie.setOverview(movieObj.getString("overview"));

            movieList.add(movie);

        }

        return movieList;
    }
}
