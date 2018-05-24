package com.udacity.popularmovies.Utils;

import java.io.Serializable;

public class Movie implements Serializable{

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
}
