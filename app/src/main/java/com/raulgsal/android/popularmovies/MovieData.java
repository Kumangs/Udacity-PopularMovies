package com.raulgsal.android.popularmovies;

/**
* This class contains all necessary data of the movies
*/

import java.io.Serializable;

public class MovieData implements Serializable {

    //Class variables
    private String poster;
    private String title;
    private String sypnosis;
    private String rating;
    private String date;

    //Object constructor
    public MovieData() {
        super();
    }

    /**
    * Getters and setters for all variables of the object
    */
    public void setPoster(String poster){
        this.poster = poster;
    }

    public String getPoster(){
        return poster;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setSypnosis(String sypnosis){
        this.sypnosis = sypnosis;
    }

    public String getSypnosis() {
        return sypnosis;
    }

    public void setRating(String rating){
        this.rating = rating;
    }

    public String getRating(){
        return rating;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }





}
