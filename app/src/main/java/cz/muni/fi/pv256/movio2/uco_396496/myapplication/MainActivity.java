package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.Movie;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.MovieDbManager;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.services.DownloadService;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnMovieSelectListener {

    private boolean mTwoPane;
    private RecyclerView rvMovies;
    private Bundle savedInstanceState;
    private ResponseReceiver responseReceiver;
    private MoviesList mComingSoon;
    private MoviesList mInCinemas;
    private ArrayList<MovieInfo> favoriteMovies;
    private MovieDbManager mDbManager;
    private Movie movie;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMovies = findViewById(R.id.rvMovies);
        TextView emptyView = findViewById(R.id.empty_view);
        this.savedInstanceState = savedInstanceState;
        Stetho.initializeWithDefaults(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setElevation(0f);
        Button discoverButton = getSupportActionBar().getCustomView().findViewById(R.id.button_discover);
        Button favoriteButton = getSupportActionBar().getCustomView().findViewById(R.id.button_favorites);

        mDbManager = new MovieDbManager(this);
        movie = new Movie();
        View.OnClickListener discoverButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DownloadService.class);
                startService(intent);
            }
        };
        View.OnClickListener favoriteButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteMovies = new ArrayList<>();
                List<Movie> moviesFromDb = mDbManager.getMovies();
                for (int i=0; i < moviesFromDb.size(); i++){
                    Movie movie = moviesFromDb.get(i);
                    MovieInfo movieInfo = new MovieInfo(
                            movie.getTitle(),
                            movie.getRating(),
                            movie.getPoster_path(),
                            movie.getRelease_date(),
                            movie.getOverview()
                    );
                    if (!favoriteMovies.contains(movieInfo)){
                        favoriteMovies.add(movieInfo);
                    }
                }
                Toast.makeText(getApplicationContext(), String.valueOf(favoriteMovies.size()), Toast.LENGTH_SHORT).show();
                setMoviesView(favoriteMovies, favoriteMovies.size()+1, true);
            }
        };
        discoverButton.setOnClickListener(discoverButtonListener);
        favoriteButton.setOnClickListener(favoriteButtonListener);

        if (savedInstanceState == null) {
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
        } else {
            mComingSoon = savedInstanceState.getParcelable("comingSoon");
            mInCinemas = savedInstanceState.getParcelable("inCinemas");

            ArrayList<MovieInfo> movies = new ArrayList<>();
            movies.addAll(mComingSoon.getResults());
            movies.addAll(mInCinemas.getResults());
            int divider = mComingSoon.getResults().size();

            setMoviesView(movies, divider, false);
        }
    }

    public void setMoviesView(ArrayList<MovieInfo> movieList, int divider, boolean favorites) {
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
        MoviesAdapter adapter = new MoviesAdapter(this, movieList, divider, mTwoPane, favorites);
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
            ArrayList<MovieInfo> movies = new ArrayList<>();
            movies.addAll(mComingSoon.getResults());
            int divider = mComingSoon.getResults().size();
            mInCinemas = intent.getParcelableExtra("inCinemas");
            movies.addAll(mInCinemas.getResults());

            movie.setTitle(movies.get(0).getOriginal_title());
            movie.setRelease_date(movies.get(0).getRelease_date());
            movie.setOverview(movies.get(0).getOverview());

            mMainActivity.setMoviesView(movies, divider, false);
        }
    }
}
