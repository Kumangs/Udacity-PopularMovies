package com.raulgsal.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieGridAdapter movieGridAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieGridAdapter = new MovieGridAdapter(getActivity(), new ArrayList<MovieData>());

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(movieGridAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // launch DetailActiviy
                MovieData movieDetail = movieGridAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),DetailActivity.class)
                       .putExtra("MovieDetail", movieDetail);
                startActivity(intent);

            }
        });
        return rootView;
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_sort_key),
                "popular");
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, MovieData[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();


        private MovieData[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULTS = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_TITLE = "title";
            final String OWN_SYPNOSIS = "overview";
            final String OWN_RATING = "vote_average";
            final String OWN_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            MovieData[] movieData = new MovieData[movieArray.length()];

            for(int i = 0; i < movieArray.length(); i++) {
                String poster;
                String title;
                String synopsis;
                String rating;
                String date;

                // Get the JSON object representing the movie
                JSONObject movieDescription = movieArray.getJSONObject(i);

                // movie poster is in a child array called "poster_path"
                poster = movieDescription.getString(OWM_POSTER);
                // movie title is in a child array called "title"
                title = movieDescription.getString(OWM_TITLE);
                // movie description is in a child array called "overview"
                synopsis = movieDescription.getString(OWN_SYPNOSIS);
                // movie rating is in a child array called "vote_average"
                rating = movieDescription.getString(OWN_RATING);
                // movie release date is in a child array called "release_date"
                date = movieDescription.getString(OWN_DATE);

                //Add the movie details in the object MovieData
                movieData[i] = new MovieData();
                movieData[i].setPoster(poster);
                movieData[i].setTitle(title);
                movieData[i].setSypnosis(synopsis);
                movieData[i].setRating(rating);
                movieData[i].setDate(date);

            }

            return movieData;

        }

        @Override
        protected MovieData[] doInBackground(String...sort) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            //int numMovies = 8;


            try {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_PARAM = "api_key";

                // Construct the URL for the themoviedb query
                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon().appendPath(sort[0])
                        .appendQueryParameter(API_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY).build();

                URL url = new URL(builtUri.toString());

                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(MovieData[] result) {
            if (result != null) {
                movieGridAdapter.clear();
                for(MovieData descriptionMove : result){
                    movieGridAdapter.add(descriptionMove);
                }
            }
        }
    }


}
