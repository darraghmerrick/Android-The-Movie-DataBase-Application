package ie.pegasus.popularmovies2.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import ie.pegasus.popularmovies2.R;
import ie.pegasus.popularmovies2.adapters.MainScreenAdapter;
import ie.pegasus.popularmovies2.database.dbContract;
import ie.pegasus.popularmovies2.model.MovieModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MainScreenAdapter mMainScreenAdapter;

    private static final String SORT_SETTING_KEY = "sort_setting";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITE = "favorite";
    private static final String MOVIES_KEY = "movies";

    private String mSortBy = POPULAR;

    private ArrayList<MovieModel> mMovies = null;

    private static final String[] MOVIE_COLUMNS = {
            dbContract.MovieEntry._ID,
            dbContract.MovieEntry.MOVIE_ID,
            dbContract.MovieEntry.TITLE,
            dbContract.MovieEntry.POSTER,
            dbContract.MovieEntry.BACKDROP,
            dbContract.MovieEntry.SYNOPSIS,
            dbContract.MovieEntry.RATING,
            dbContract.MovieEntry.RELEASE
    };

    public static final int COL_ID = 0;
    public static final int MOVIE_ID = 1;
    public static final int TITLE = 2;
    public static final int POSTER = 3;
    public static final int BACKDROP = 4;
    public static final int SYNOPSIS = 5;
    public static final int RATING = 6;
    public static final int RELEASE = 7;

    public MainActivityFragment() {
    }

    interface Callback {
        void onItemSelected(MovieModel movieModel);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        MenuItem action_sort_by_favorite = menu.findItem(R.id.action_sort_by_favorite);

        if (mSortBy.contentEquals( POPULAR )) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_popularity.setChecked(true);
            }
        } else if (mSortBy.contentEquals( TOP_RATED )) {
            if (!action_sort_by_rating.isChecked()) {
                action_sort_by_rating.setChecked(true);
            }
        } else if (mSortBy.contentEquals(FAVORITE)) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_favorite.setChecked(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by_popularity:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = POPULAR;
                updateMovies(mSortBy);
                return true;
            case R.id.action_sort_by_rating:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = TOP_RATED;
                updateMovies(mSortBy);
                return true;
            case R.id.action_sort_by_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = FAVORITE;
                updateMovies(mSortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override  //Create main Screen layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        GridView mGridView = (GridView) view.findViewById( R.id.gridview_movies );
        //change the column to 3 if the screen is rotated to Landscape. It is set to 2 for Portrait
        // in fragment_main.xml
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
           mGridView.setNumColumns( 3 );
        }


        mMainScreenAdapter = new MainScreenAdapter(getActivity(), new ArrayList<MovieModel>());
        //set the mainscreen view to MainScreenAdapter to populate it with movie data
        mGridView.setAdapter( mMainScreenAdapter );

        //Listens for movie selected from main view
        mGridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieModel movieModel = mMainScreenAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected( movieModel );  //movieModel contains all the JSON data for thae selected movieModel
            }
        });

        if (savedInstanceState != null) {
            // mSortby will be used as the param for the http request.
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                mSortBy = savedInstanceState.getString(SORT_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                mMainScreenAdapter.setData(mMovies);
            } else {
                updateMovies(mSortBy);
            }
        } else {
            updateMovies(mSortBy);
        }

        return view;
    }

    private void updateMovies(String sort_by) {
        if (!sort_by.contentEquals(FAVORITE)) {
            new FetchMoviesTask().execute(sort_by);
        } else {
            new FetchFavoriteMoviesTask(getActivity()).execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mSortBy.contentEquals( POPULAR )) {
            outState.putString(SORT_SETTING_KEY, mSortBy);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, List<MovieModel>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private List<MovieModel> getMoviesDataFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<MovieModel> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                MovieModel movieModelModel = new MovieModel(movie);  //Handles an individual movie
                results.add( movieModelModel ); //results contents all 20 movies
            }

            return results;
        }

        @Override
        protected List<MovieModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                String MOVIE_DATABASE_URL =
                        "https://api.themoviedb.org/3/movie/";

                String PARAM_API = "api_key";
                String api_key = "f9c49fc67bf403d0797e6333b70f2bdf";
                String MovieDB_query = params[0];
                String PARAM_LANGUAGE = "language";
                String language = "en-US";

                // Construct the URL for the themoviedb query
                // Possible parameters are top_rated or popular


                Uri builtUri = Uri.parse(MOVIE_DATABASE_URL).buildUpon()
                        .appendPath(MovieDB_query)
                        .appendQueryParameter(PARAM_API, api_key)
                        .appendQueryParameter(PARAM_LANGUAGE, language)

                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append( line ).append( "\n" );
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
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
                return getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            if (movies != null) {
                if (mMainScreenAdapter != null) {
                    mMainScreenAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }

    private class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<MovieModel>> {

        private Context mContext;

        FetchFavoriteMoviesTask(Context context) {
            mContext = context;
        }

        private List<MovieModel> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            List<MovieModel> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MovieModel movieModel = new MovieModel(cursor);
                    results.add( movieModel );
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }

        @Override
        protected List<MovieModel> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    dbContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<MovieModel> movies) {
            if (movies != null) {
                if (mMainScreenAdapter != null) {
                    mMainScreenAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }
}

