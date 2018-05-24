package com.udacity.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "Debug";
    protected static final String SORT_ORDER_POPULAR = "Popular Movies";
    protected static final String SORT_ORDER_TOP_RATED = "Top Rated Movies";
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


}
