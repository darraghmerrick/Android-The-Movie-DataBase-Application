package ie.pegasus.popularmovies2.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ie.pegasus.popularmovies2.R;
import ie.pegasus.popularmovies2.adapters.ReviewAdapter;
import ie.pegasus.popularmovies2.adapters.TrailerAdapter;
import ie.pegasus.popularmovies2.database.dbContract;
import ie.pegasus.popularmovies2.model.MovieModel;
import ie.pegasus.popularmovies2.model.ReviewModel;
import ie.pegasus.popularmovies2.model.TrailerModel;

/**
 * Created by Pegasus on 07/05/2017.
 * This code handles the detail page of the Popular Movies 2 Application
 * Fragment of DetailActivity
 */

public class DetailActivityFragment extends Fragment {

    public static final String TAG = DetailActivityFragment.class.getSimpleName();

    static final String movieDetails = "movieDetails";

    private MovieModel mMovieModel;

    private CardView mReviewsCardview;
    private CardView mTrailersCardview;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private Toast mToast;

    private ShareActionProvider mShareActionProvider;

    private TrailerModel mTrailerModel;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mMovieModel != null) {
            inflater.inflate(R.menu.detail_menu, menu);

            final MenuItem action_favorite = menu.findItem(R.id.favourited );
            MenuItem action_share = menu.findItem(R.id.shared );

            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return DetailActivityHelper.addedFavourite(getActivity(), mMovieModel.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavorited) {
                    action_favorite.setIcon(isFavorited == 1 ?
                            R.drawable.ic_star_black_18dp :
                            R.drawable.ic_star_border_18dp);
                }
            }.execute();

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

            if (mTrailerModel != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favourited:
                if (mMovieModel != null) {
                    // check if movie is in favorites or not
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return DetailActivityHelper.addedFavourite(getActivity(), mMovieModel.getId());
                        }

                        @Override
                        protected void onPostExecute(Integer isFavorited) {
                            // if movie is added to favourites
                            if (isFavorited == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                dbContract.MovieEntry.CONTENT_URI,
                                                dbContract.MovieEntry.MOVIE_ID + " = ?",
                                                new String[]{Integer.toString( mMovieModel.getId())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.ic_star_border_18dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            // if movie is not yet in favorites
                            else {
                                // add movie to favorites
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put( dbContract.MovieEntry.MOVIE_ID, mMovieModel.getId());
                                        values.put( dbContract.MovieEntry.TITLE, mMovieModel.getTitle());
                                        values.put( dbContract.MovieEntry.POSTER, mMovieModel.getPoster());
                                        values.put( dbContract.MovieEntry.BACKDROP, mMovieModel.getBackdrop());
                                        values.put( dbContract.MovieEntry.SYNOPSIS, mMovieModel.getSynopsis());
                                        values.put( dbContract.MovieEntry.RATING, mMovieModel.getRating());
                                        values.put( dbContract.MovieEntry.RELEASE, mMovieModel.getDate());

                                        return getActivity().getContentResolver().insert( dbContract.MovieEntry.CONTENT_URI,
                                                values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.ic_star_black_18dp);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieModel = arguments.getParcelable(DetailActivityFragment.movieDetails );
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ScrollView mDetailLayout = (ScrollView) rootView.findViewById( R.id.detail_layout );

        if (mMovieModel != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
        }

        ImageView mBackdropView = (ImageView) rootView.findViewById( R.id.detail_image );

        TextView mTitleView = (TextView) rootView.findViewById( R.id.detail_title );
        TextView mSynopsisView = (TextView) rootView.findViewById( R.id.detail_synopsis );
        TextView mDateView = (TextView) rootView.findViewById( R.id.detail_date );
        TextView mRatingView = (TextView) rootView.findViewById( R.id.detail_vote_average );

        LinearListView mTrailersView = (LinearListView) rootView.findViewById( R.id.detail_trailers );
        LinearListView mReviewsView = (LinearListView) rootView.findViewById( R.id.detail_reviews );

        mReviewsCardview = (CardView) rootView.findViewById(R.id.detail_reviews_cardview);
        mTrailersCardview = (CardView) rootView.findViewById(R.id.detail_trailers_cardview);

        mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<TrailerModel>());
        mTrailersView.setAdapter(mTrailerAdapter);

        mTrailersView.setOnItemClickListener( new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view,
                                    int position, long id) {
                TrailerModel trailerModel = mTrailerAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailerModel.getKey()));
                startActivity(intent);
            }
        });

        mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<ReviewModel>());
        mReviewsView.setAdapter(mReviewAdapter);

        if (mMovieModel != null) {

            String image_url = DetailActivityHelper.buildImageUrl(342, mMovieModel.getBackdrop());
            //Download the Backdrop Image
            Glide.with(this).load(image_url).into( mBackdropView );

            mTitleView.setText( mMovieModel.getTitle());
            mSynopsisView.setText( mMovieModel.getSynopsis());

            String movie_date = mMovieModel.getDate();

            SimpleDateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                String date = DateUtils.formatDateTime(getActivity(),
                        formatter.parse(movie_date).getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
                mDateView.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mRatingView.setText(Integer.toString( mMovieModel.getRating()));
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMovieModel != null) {
            new trailerTask().execute(Integer.toString( mMovieModel.getId()));
            new FetchReviewsTask().execute(Integer.toString( mMovieModel.getId()));
        }
    }

    @SuppressWarnings("deprecation")
    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //noinspection deprecation
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieModel.getTitle() + " " +
                "http://www.youtube.com/watch?v=" + mTrailerModel.getKey());
        return shareIntent;
    }

    private class trailerTask extends AsyncTask<String, Void, List<TrailerModel>> {

        private final String LOG_TAG = trailerTask.class.getSimpleName();

        private List<TrailerModel> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<TrailerModel> results = new ArrayList<>();

            for(int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                if (trailer.getString("site").contentEquals("YouTube")) {
                    TrailerModel trailerModel = new TrailerModel(trailer);
                    results.add( trailerModel );
                }
            }

            return results;
        }

        @Override
        protected List<TrailerModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.tmdb_api_key))
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
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<TrailerModel> trailerModels) {
            if (trailerModels != null) {
                if (trailerModels.size() > 0) {
                    mTrailersCardview.setVisibility(View.VISIBLE);
                    if (mTrailerAdapter != null) {
                        mTrailerAdapter.clear();
                        for (TrailerModel trailerModel : trailerModels) {
                            mTrailerAdapter.add( trailerModel );
                        }
                    }

                    mTrailerModel = trailerModels.get(0);
                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareMovieIntent());
                    }
                }
            }
        }
    }

    private class FetchReviewsTask extends AsyncTask<String, Void, List<ReviewModel>> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private List<ReviewModel> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<ReviewModel> results = new ArrayList<>();

            for(int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new ReviewModel(review));
            }

            return results;
        }

        @Override
        protected List<ReviewModel> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, getString(R.string.tmdb_api_key))
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
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<ReviewModel> reviewModels) {
            if (reviewModels != null) {
                if (reviewModels.size() > 0) {
                    mReviewsCardview.setVisibility(View.VISIBLE);
                    if (mReviewAdapter != null) {
                        mReviewAdapter.clear();
                        for (ReviewModel reviewModel : reviewModels) {
                            mReviewAdapter.add( reviewModel );
                        }
                    }
                }
            }
        }
    }
}


