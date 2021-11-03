package ie.pegasus.popularmovies2.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ie.pegasus.popularmovies2.R;

/**
 * Created by Pegasus in May 2017.
 * The Movie Detail activity, uses a fragment
 */

public class DetailActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailActivityFragment.movieDetails,
                    getIntent().getParcelableExtra(DetailActivityFragment.movieDetails ));

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add( R.id.activity_detail, fragment)
                    .commit();
        }
    }
}
