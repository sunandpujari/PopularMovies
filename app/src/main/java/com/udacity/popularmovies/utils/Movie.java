package com.udacity.popularmovies.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{

    private Long id;
    private String title;
    private String release_date;
    private String poster_path;
    private double vote_average;
    private String overview;

    public Movie() {
    }

    @Override
    public String toString() {
        return "id: " +id
                +",title: " + title
                 +", release_date: " +release_date
                +", poster_path: " +poster_path
                +", vote_average: " +vote_average
                +", overview: " +overview;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        dest.writeString(this.release_date);
        dest.writeString(this.poster_path);
        dest.writeDouble(this.vote_average);
        dest.writeString(this.overview);
    }

    protected Movie(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.release_date = in.readString();
        this.poster_path = in.readString();
        this.vote_average = in.readDouble();
        this.overview = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
