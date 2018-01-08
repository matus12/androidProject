package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

import cz.muni.fi.pv256.movio2.uco_396496.myapplication.services.DownloadService;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnMovieSelectListener {

    private boolean mTwoPane;
    private RecyclerView rvMovies;
    private Bundle savedInstanceState;
    private ResponseReceiver responseReceiver;
    private MoviesList mComingSoon;
    private MoviesList mInCinemas;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMovies = findViewById(R.id.rvMovies);
        TextView emptyView = findViewById(R.id.empty_view);
        this.savedInstanceState = savedInstanceState;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setElevation(0f);

        if(savedInstanceState == null){
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
        } else {
            mComingSoon = savedInstanceState.getParcelable("comingSoon");
            ArrayList<MovieInfo> movies = mComingSoon.getResults();
            int divider = movies.size();
            mInCinemas = savedInstanceState.getParcelable("inCinemas");
            movies.addAll(mInCinemas.getResults());

            setMoviesView(movies, divider);
        }

    }

    public void setMoviesView(ArrayList<MovieInfo> movieList, int divider) {
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        MoviesAdapter adapter = new MoviesAdapter(this, movieList, divider, mTwoPane);
        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        responseReceiver = new ResponseReceiver(this);
        registerReceiver(responseReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(responseReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("comingSoon", mComingSoon);
        outState.putParcelable("inCinemas", mInCinemas);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMovieSelect(MovieInfo movie) {
        if (mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();

            DetailFragment fragment = DetailFragment.newInstance(movie);
            fm.beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
            startActivity(intent);
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "MESSAGE_PROCESSED";
        private MainActivity mMainActivity;

        public ResponseReceiver(MainActivity activity) {
            mMainActivity = activity;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            mComingSoon = intent.getParcelableExtra("comingSoon");
            ArrayList<MovieInfo> movies = mComingSoon.getResults();
            int divider = movies.size();
            mInCinemas = intent.getParcelableExtra("inCinemas");
            movies.addAll(mInCinemas.getResults());

            mMainActivity.setMoviesView(movies, divider);
        }
    }
}
