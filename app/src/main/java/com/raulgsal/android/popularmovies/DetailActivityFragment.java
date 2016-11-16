package com.raulgsal.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private MovieData movieData;
    private String movieRating;
    private String movieDate;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent !=null && intent.hasExtra("MovieDetail")) {
            movieData = (MovieData) intent.getSerializableExtra("MovieDetail");

            ((TextView) rootView.findViewById(R.id.original_title_text)).setText(movieData.getTitle());
            ImageView posterView = (ImageView) rootView.findViewById(R.id.poster_detail_image);

            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500/" + movieData.getPoster()).into(posterView);

            movieDate = "Release Date: " + movieData.getDate();
            movieRating = "Rated: " + movieData.getRating() + "/10";
            ((TextView) rootView.findViewById(R.id.rating_text)).setText(movieRating);
            ((TextView) rootView.findViewById(R.id.release_date_text)).setText(movieDate);

            ((TextView) rootView.findViewById(R.id.sypnosis_text)).setText(movieData.getSypnosis());




        }





        return rootView;
    }
}
