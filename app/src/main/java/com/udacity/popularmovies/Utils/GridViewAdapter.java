package com.udacity.popularmovies.Utils;

/**
 * Created by sunand on 11/30/17.
 */

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;
import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Movie> data;
    private int windowWidth,windowHeight;

    public GridViewAdapter(Context context, ArrayList<Movie> d,Display display) {
        mContext = context;
        data=d;


        Point size = new Point();
        display.getSize(size);
        windowWidth = size.x;
        windowHeight = size.y;

    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(windowWidth/2, windowHeight>1750? windowHeight/2: 900));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        Movie movie = data.get(position);

        Picasso.with(mContext).load(mContext.getResources().getString(R.string.BASE_URL_IMAGE) +movie.getPoster_path() ).into(imageView);

        return imageView;
    }
}