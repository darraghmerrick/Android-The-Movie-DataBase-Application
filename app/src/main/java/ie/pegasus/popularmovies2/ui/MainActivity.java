package ie.pegasus.popularmovies2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import ie.pegasus.popularmovies2.R;
import ie.pegasus.popularmovies2.model.MovieModel;

/**
 * Created by Darragh Merrick May 2017.
 * This is the MainActivity class of the Popular Movies, Stage 2 application
 * **/

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {


    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.activity_detail ) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_detail, new DetailActivityFragment(),
                                DetailActivityFragment.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override   //when a movieModel is selected from the main page
    public void onItemSelected(MovieModel movieModel) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailActivityFragment.movieDetails, movieModel );

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_detail, fragment, DetailActivityFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    //the intent for the detail activity passes the movieModel data with .putExtra
                    .putExtra(DetailActivityFragment.movieDetails, movieModel );
            startActivity(intent);
        }
    }
}
