package com.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.utils.Movie;

public class DetailsActivity extends BaseActivity {

    private TextView title, year, rating, overview;
    private ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if(getIntent() != null) {

            Intent intent = getIntent();
            Movie movie = intent.getParcelableExtra(MOVIE_EXTRA_KEY);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.MOVIE_DETAILS));

            title = findViewById(R.id.tvTitle);
            year = findViewById(R.id.tvYear);
            rating = findViewById(R.id.tvVoteAverage);
            overview = findViewById(R.id.tvOverview);
            poster = findViewById(R.id.imgPoster);

            title.setText(movie.getTitle());
            year.setText(movie.getRelease_date());
            rating.setText(Html.fromHtml("Rating: <b>" + movie.getVote_average() + "/10</b>"));
            overview.setText(movie.getOverview());

            Picasso.with(DetailsActivity.this).load(getResources().getString(R.string.BASE_URL_IMAGE) + movie.getPoster_path()).into(poster);
        }
        else {
            Toast.makeText(DetailsActivity.this,getResources().getString(R.string.SOMETHING_WRONG), Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.btnExit:
                finishAffinity();
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
