package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnMovieSelectListener {

    private boolean mTwoPane;
    private Context mContext;
    private Movie movie;
    ArrayList<Movie> movies;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        RecyclerView rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);

        movies = new ArrayList<>();
        movie = new Movie(Parcel.obtain());
        movie.setTitle("Blade Runner 2049");
        movies.add(movie);

        movie = new Movie(Parcel.obtain());
        movie.setTitle("Thor: Ragnarok");
        movies.add(movie);

        movie = new Movie(Parcel.obtain());
        movie.setTitle("It");
        movies.add(movie);

        if (movies.isEmpty()) {
            rvMovies.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rvMovies.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        MoviesAdapter adapter = new MoviesAdapter(this, movies, mTwoPane);
        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvMovies.addItemDecoration(itemDecoration);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment());
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }
    @Override
    public void onMovieSelect(Movie movie) {
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
}
