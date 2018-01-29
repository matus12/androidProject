package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.MovieDbHelper;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.MovieDbManager;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.loader.MoviesLoader;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.services.DownloadService;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.sync.UpdaterSyncAdapter;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnMovieSelectListener {

    private boolean mTwoPane;
    private boolean mFavorites;
    private RecyclerView rvMovies;
    private Bundle savedInstanceState;
    private ResponseReceiver responseReceiver;
    private MoviesList mComingSoon;
    private MoviesList mInCinemas;
    private ArrayList<MovieInfo> favoriteMovies;
    private MovieDbManager mDbManager;
    private Movie movie;
    public static Button favoriteButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMovies = findViewById(R.id.rvMovies);
        mFavorites = false;
        TextView emptyView = findViewById(R.id.empty_view);
        this.savedInstanceState = savedInstanceState;

        Stetho.initializeWithDefaults(this);

        UpdaterSyncAdapter.initializeSyncAdapter(this);
        UpdaterSyncAdapter.syncImmediately(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setElevation(0f);
        Button discoverButton = getSupportActionBar().getCustomView().findViewById(R.id.button_discover);
        favoriteButton = getSupportActionBar().getCustomView().findViewById(R.id.button_favorites);
        Button syncButton = getSupportActionBar().getCustomView().findViewById(R.id.button_sync);

        getSupportLoaderManager().initLoader(R.id.movie_loader_id, null, mLoaderCallbacks);

        mDbManager = new MovieDbManager(this);
        movie = new Movie();
        View.OnClickListener discoverButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavorites = false;
                Intent intent = new Intent(getApplicationContext(), DownloadService.class);
                stopService(intent);
                startService(intent);
            }
        };
        View.OnClickListener favoriteButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavorites = true;
                favoriteMovies = new ArrayList<>();
                List<Movie> moviesFromDb = mDbManager.getMovies();
                for (int i=0; i < moviesFromDb.size(); i++){
                    Movie movie = moviesFromDb.get(i);
                    MovieInfo movieInfo = new MovieInfo(
                            movie.getTitle(),
                            movie.getRating(),
                            movie.getPoster_path(),
                            movie.getRelease_date(),
                            movie.getOverview(),
                            movie.getMovie_id()
                    );
                    if (!favoriteMovies.contains(movieInfo)){
                        favoriteMovies.add(movieInfo);
                    }
                }
                setMoviesView(favoriteMovies, favoriteMovies.size()+1, true);
            }
        };
        View.OnClickListener syncButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdaterSyncAdapter.syncImmediately(getApplicationContext());
                favoriteButton.performClick();
            }
        };

        discoverButton.setOnClickListener(discoverButtonListener);
        favoriteButton.setOnClickListener(favoriteButtonListener);
        syncButton.setOnClickListener(syncButtonListener);

        if (savedInstanceState == null) {
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
        } else {
            mComingSoon = savedInstanceState.getParcelable("comingSoon");
            mInCinemas = savedInstanceState.getParcelable("inCinemas");
            mFavorites = savedInstanceState.getBoolean("favorites");

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
        outState.putBoolean("favorites", mFavorites);

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

    private LoaderManager.LoaderCallbacks<List<Movie>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Movie>>() {
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            return new MoviesLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> moviesFromDb) {
            if (mFavorites){
                favoriteMovies = new ArrayList<>();
                for (int i = 0; i < moviesFromDb.size(); i++){
                    Movie movie = moviesFromDb.get(i);
                    MovieInfo movieInfo = new MovieInfo(
                            movie.getTitle(),
                            movie.getRating(),
                            movie.getPoster_path(),
                            movie.getRelease_date(),
                            movie.getOverview(),
                            movie.getMovie_id()
                    );
                    if (!favoriteMovies.contains(movieInfo)){
                        favoriteMovies.add(movieInfo);
                    }
                }
                setMoviesView(favoriteMovies, favoriteMovies.size()+1, true);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {

        }
    };
}
