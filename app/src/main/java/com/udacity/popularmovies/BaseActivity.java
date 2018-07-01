package com.udacity.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.udacity.popularmovies.data.FavoritesContract;
import com.udacity.popularmovies.utils.Movie;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "Debug";
    protected static final String SORT_ORDER_POPULAR = "Popular Movies";
    protected static final String SORT_ORDER_TOP_RATED = "Top Rated Movies";
    protected static final String SORT_ORDER_FAVORITES = "Favorite Movies";
    protected String currentSortOrder;
    protected static final String LIFECYCLE_SORT_ORDER_KEY = "Sort Order";
    protected static final String MOVIE_EXTRA_KEY = "MOVIE";


    protected boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return !(networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE));
    }

    protected String getApiURL(){
            return getResources().getString(currentSortOrder.equals(SORT_ORDER_TOP_RATED)? R.string.BASE_URL_TOP_RATED:R.string.BASE_URL_POPULAR)+"?api_key=" +getResources().getString(R.string.API_KEY);
    }

    protected String getTrailersURL(long id){
        return getResources().getString(R.string.BASE_URL)+id+"/videos?api_key=" +getResources().getString(R.string.API_KEY);
    }

    protected String getReviewsURL(long id){
        return getResources().getString(R.string.BASE_URL)+id+"/reviews?api_key=" +getResources().getString(R.string.API_KEY);
    }

    protected Uri insertNewRecord(Movie movie){
        ContentValues values = new ContentValues();

        values.put(FavoritesContract.FavoritesEntry._ID,movie.getId());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE,movie.getTitle());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE,movie.getRelease_date());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH,movie.getPoster_path());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE,movie.getVote_average());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW,movie.getOverview());

        return getContentResolver().
                insert(FavoritesContract.FavoritesEntry.CONTENT_URI,values);
    }

    protected List<Movie> getAllFavoriteMovies(){

        List<Movie> movies = null;

        Cursor cursor = getContentResolver()
                .query(FavoritesContract.FavoritesEntry.CONTENT_URI,null,null,null,null);

        if(cursor !=null && cursor.getCount() > 0 ) {

            movies = new ArrayList<>();

            cursor.moveToFirst();

            while (!cursor.isAfterLast()){
                movies.add(populateMovie(cursor));
                cursor.moveToNext();
            }

        }

        return movies;
    }

    protected Movie getFavoriteMovie(long _id){

        Cursor cursor = getContentResolver()
                .query(FavoritesContract.FavoritesEntry.buildUriWithId(_id),null,null,null,null);

        if(cursor !=null && cursor.getCount() > 0 ) {

            cursor.moveToFirst();
            return populateMovie(cursor);
        }

        return null;
    }

    private Movie populateMovie(Cursor cursor)
    {
        Movie movie = null;
        try
        {
            movie = new Movie();

            movie.setId(cursor.getLong(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry._ID)));
            movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_TITLE)));
            movie.setPoster_path(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH)));
            movie.setRelease_date(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE)));
            movie.setVote_average(cursor.getDouble(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return movie;
    }

    protected void deleteFavoriteMovie(long _id){

        getContentResolver().delete(FavoritesContract.FavoritesEntry.CONTENT_URI,FavoritesContract.FavoritesEntry._ID + " = ? ",new String[]{_id+""});


    }

}
