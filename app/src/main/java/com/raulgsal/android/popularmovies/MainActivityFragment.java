package com.raulgsal.android.popularmovies;

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


        private MovieData[] getMovieDataFromJson(String movieJsonStr, int numMovies)
                throws JSONException {

            final String OWM_RESULTS = "results";
            final String OWM_POSTER = "poster_path";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            MovieData[] movieData = new MovieData[numMovies];

            for(int i = 0; i < numMovies; i++) {
                String poster;

                JSONObject movieDescription = movieArray.getJSONObject(i);

                poster = movieDescription.getString(OWM_POSTER);

                movieData[i] = new MovieData();
                movieData[i].setPoster(poster);

            }

            return movieData;

        }

        @Override
        protected MovieData[] doInBackground(String...sort) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            int numMovies = 8;


            try {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon().appendPath(sort[0])
                        .appendQueryParameter(API_PARAM, BuildConfig.POPULAR_MOVIES_API_KEY).build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
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
                return getMovieDataFromJson(movieJsonStr, numMovies);
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
