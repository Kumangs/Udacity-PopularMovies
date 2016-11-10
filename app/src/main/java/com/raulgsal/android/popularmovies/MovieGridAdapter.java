package com.raulgsal.android.popularmovies;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieGridAdapter extends ArrayAdapter<MovieData> {

    public MovieGridAdapter (Activity context, List<MovieData> movieDataList){
        super(context, 0, movieDataList);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        MovieData movieData = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_item, parent, false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.poster_image);

        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500/" + movieData.getPoster()).into(posterView);

        return convertView;
    }




}
