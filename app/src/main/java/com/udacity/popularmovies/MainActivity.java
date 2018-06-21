package com.udacity.popularmovies;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.udacity.popularmovies.utils.GridViewAdapter;
import com.udacity.popularmovies.utils.IMoviesAsync;
import com.udacity.popularmovies.utils.Movie;
import com.udacity.popularmovies.utils.MoviesAsync;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends BaseActivity implements IMoviesAsync{

    private ProgressDialog progressDialog;
    private ArrayList<Movie> movieArrayList;
    private GridViewAdapter adapter;
    private GridView gridView;
    Realm realm;
    private List<Movie> favoriteMovies;

    private final String KEY_GRID_VIEW_STATE = "grid_view_state";
    private static Bundle mBundleGridViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(MainActivity.this);
        realm = Realm.getDefaultInstance();

        progressDialog = new ProgressDialog(MainActivity.this);
        movieArrayList = new ArrayList<>();
        gridView = findViewById(R.id.container);
        Display display = getWindowManager().getDefaultDisplay();

        adapter = new GridViewAdapter(this, movieArrayList, display);
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(MOVIE_EXTRA_KEY,movieArrayList.get(position));
                startActivity(intent);
            }
        });

        if(savedInstanceState !=null) {
            if (savedInstanceState.containsKey(LIFECYCLE_SORT_ORDER_KEY))
                currentSortOrder = savedInstanceState.getString(LIFECYCLE_SORT_ORDER_KEY);
        }
        else
            currentSortOrder = SORT_ORDER_POPULAR;

        startAsyncTask();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LIFECYCLE_SORT_ORDER_KEY, currentSortOrder);
    }

    @Override
    public void showProgress() {
        progressDialog.setTitle("Fetching Movies");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void setupData(ArrayList<Movie> movies) {
        if(movies ==null || movies.size()==0){

            Toast.makeText(MainActivity.this,getResources().getString(R.string.NO_INTERNET), Toast.LENGTH_LONG).show();
        }
        else {

            Log.d(TAG, "setupData: "+movies);
            setTitle(currentSortOrder);
            progressDialog.dismiss();
            movieArrayList.clear();
            movieArrayList.addAll(movies);

            adapter.notifyDataSetChanged();


            if (mBundleGridViewState != null) {
                int listState = mBundleGridViewState.getInt(KEY_GRID_VIEW_STATE);

                gridView.smoothScrollToPosition(listState);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnPopularity:
                if(!currentSortOrder.equals(SORT_ORDER_POPULAR)) {
                    currentSortOrder = SORT_ORDER_POPULAR;
                    startAsyncTask();
                }
                return true;
            case R.id.btnRating:
                if(!currentSortOrder.equals(SORT_ORDER_TOP_RATED))
                    currentSortOrder = SORT_ORDER_TOP_RATED;
                    startAsyncTask();
                return true;
            case R.id.btnFav:
                currentSortOrder = SORT_ORDER_FAVORITES;
                loadFavorites();
                return true;
            case R.id.btnExit:
                finishAffinity();
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFavorites(){

        realm.beginTransaction();
        favoriteMovies = realm.where(Movie.class).findAll();
        realm.commitTransaction();

        movieArrayList.clear();
        movieArrayList.addAll(favoriteMovies);

        adapter.notifyDataSetChanged();

        setTitle(currentSortOrder);

    }

    private void startAsyncTask(){

        if(!isConnected()){

            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(this)
                            .setTitle(getResources().getString(R.string.NO_INTERNET_TITLE))
                            .setMessage(getResources().getString(R.string.NO_INTERNET_MESSAGE))
                            .setPositiveButton(getResources().getString(R.string.SETTINGS), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivities(new Intent[]{intent});
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.EXIT), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    finish();
                                }
                            });

            alertDialogBuilder.show();
        }
        else {
            new MoviesAsync(MainActivity.this, getResources().getInteger(R.integer.CONNECTION_TIMEOUT)).execute(getApiURL());

        }

    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // save gridView state
        mBundleGridViewState = new Bundle();
        int listState = gridView.getFirstVisiblePosition();
        mBundleGridViewState.putInt(KEY_GRID_VIEW_STATE, listState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // restore gridView state
        if (mBundleGridViewState != null) {
            int listState = mBundleGridViewState.getInt(KEY_GRID_VIEW_STATE);

            gridView.smoothScrollToPosition(listState);
        }
    }


}
