package com.udacity.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.utils.JsonUtil;
import com.udacity.popularmovies.utils.Movie;
import com.udacity.popularmovies.utils.Review;
import com.udacity.popularmovies.utils.Trailer;
import org.json.JSONException;
import java.io.IOException;
import java.util.List;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsActivity extends BaseActivity {

    private TextView title, year, rating, overview;
    private ImageView poster;
    private Movie movie;
    private OkHttpClient client;
    private Request request;
    private List<Trailer> trailers;
    private List<Review> reviews;
    private LinearLayout container;
    private Realm realm;
    private MenuItem btnFav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if(getIntent() != null) {

            Realm.init(this);
            realm = Realm.getDefaultInstance();

            Intent intent = getIntent();
            movie = intent.getParcelableExtra(MOVIE_EXTRA_KEY);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.MOVIE_DETAILS));

            title = findViewById(R.id.tvTitle);
            year = findViewById(R.id.tvYear);
            rating = findViewById(R.id.tvVoteAverage);
            overview = findViewById(R.id.tvOverview);
            poster = findViewById(R.id.imgPoster);
            container = findViewById(R.id.container);

            title.setText(movie.getTitle());
            year.setText(movie.getRelease_date());
            rating.setText(Html.fromHtml("Rating: <b>" + movie.getVote_average() + "/10</b>"));
            overview.setText(movie.getOverview());

            Picasso.with(DetailsActivity.this).load(getResources().getString(R.string.BASE_URL_IMAGE) + movie.getPoster_path()).into(poster);

            client = new OkHttpClient();
            loadTrailers();
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
        btnFav =  menu.findItem(R.id.btnFav);
        updateFavStatus();
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
            case R.id.btnFav:
                updateFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateFavStatus() {

        realm.beginTransaction();
        Movie exists = realm.where(Movie.class).equalTo("id",movie.getId()).findFirst();
        realm.commitTransaction();

        if(exists !=null)
            btnFav.setIcon(R.mipmap.ic_fav_filled);
        else
            btnFav.setIcon(R.drawable.ic_fav);
    }

    private void updateFavorites(){


        realm.beginTransaction();
        Movie exists = realm.where(Movie.class).equalTo("id",movie.getId()).findFirst();
        realm.commitTransaction();

        if(exists !=null){
            //delete
            realm.beginTransaction();
            realm.where(Movie.class).equalTo("id", movie.getId()).findFirst().deleteFromRealm();
            realm.commitTransaction();
            btnFav.setIcon(R.drawable.ic_fav);

        }else {
            //insert

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(movie);
            realm.commitTransaction();
            btnFav.setIcon(R.mipmap.ic_fav_filled);

        }

    }

    private void loadReviews(){

        request = new Request.Builder()
                .url(getReviewsURL(movie.getId()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        reviews = JsonUtil.ParseReviews(response.body().string());

                        runOnUiThread (new Thread(new Runnable() {
                            public void run() {
                                if(reviews.size()>0){
                                    View hr = new View(DetailsActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,3);
                                    params.setMargins(0,0,0,10);
                                    hr.setLayoutParams(params);
                                    hr.setBackgroundColor(getResources().getColor(R.color.colorLine));

                                    container.addView(hr);

                                    TextView header = new TextView(DetailsActivity.this);
                                    header.setText(getResources().getString(R.string.REVIEWS_HEADER));
                                    header.setTypeface(Typeface.DEFAULT_BOLD);
                                    header.setTextSize(20);
                                    container.addView(header);

                                    for(Review review: reviews){


                                        TextView content = new TextView(DetailsActivity.this);
                                        content.setText(review.getContent());
                                        container.addView(content);

                                        TextView name = new TextView(DetailsActivity.this);
                                        name.setText(review.getAuthor());
                                        name.setGravity(Gravity.END);
                                        name.setTypeface(Typeface.DEFAULT_BOLD);
                                        container.addView(name);

                                        View divider = new View(DetailsActivity.this);
                                        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,2);
                                        params.setMargins(0,15,0,15);
                                        divider.setLayoutParams(params);

                                        divider.setBackgroundColor(getResources().getColor(R.color.colorDivider));

                                        container.addView(divider);

                                    }

                                }
                            }
                        }));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


    }

    private void loadTrailers(){

        request = new Request.Builder()
                .url(getTrailersURL(movie.getId()))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        trailers = JsonUtil.ParseTrailers(response.body().string());

                        runOnUiThread (new Thread(new Runnable() {
                            public void run() {
                                if(trailers.size()>0){
                                    View hr = new View(DetailsActivity.this);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,3);
                                    params.setMargins(0,20,0,10);
                                    hr.setLayoutParams(params);
                                    hr.setBackgroundColor(getResources().getColor(R.color.colorLine));


                                    container.addView(hr);

                                    TextView header = new TextView(DetailsActivity.this);
                                    header.setText(getResources().getString(R.string.TRAILERS_HEADER));
                                    header.setTypeface(Typeface.DEFAULT_BOLD);
                                    header.setTextSize(20);
                                    container.addView(header);

                                    for(final Trailer trailer: trailers){

                                        LinearLayout ll = new LinearLayout(DetailsActivity.this);
                                        ll.setOrientation(LinearLayout.HORIZONTAL);
                                        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        params.setMargins(0,20,0,20);
                                        ll.setLayoutParams(params);


                                        ImageView play = new ImageView(DetailsActivity.this);
                                        play.setImageResource(R.drawable.ic_play);
                                        params = new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        play.setLayoutParams(params);
                                        play.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                watchYoutubeVideo(trailer.getKey());
                                            }
                                        });
                                        ll.addView(play);

                                        TextView name = new TextView(DetailsActivity.this);
                                        name.setText(trailer.getName());
                                        name.setGravity(Gravity.CENTER_VERTICAL);
                                        name.setTextSize(12);

                                        ll.addView(name);

                                        container.addView(ll);

                                        View divider = new View(DetailsActivity.this);
                                        divider.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.FILL_PARENT,
                                                2));

                                        divider.setBackgroundColor(getResources().getColor(R.color.colorDivider));

                                        container.addView(divider);

                                    }

                                }
                            }
                        }));
                        loadReviews();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    private void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
