package com.udacity.popularmovies.utils;

import java.util.ArrayList;

/**
 * Created by sunand on 5/20/18.
 */

public interface IMoviesAsync {
    void showProgress();
    void setupData(ArrayList<Movie> movies);
}
