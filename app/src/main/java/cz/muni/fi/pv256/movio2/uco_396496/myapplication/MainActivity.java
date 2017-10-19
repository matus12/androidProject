package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnMovieSelectListener {

    private boolean mTwoPane;
    private Button btn;
    private Button btn2;
    private Button btn3;
    private Context mContext;
    private Movie movie;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movie = new Movie(Parcel.obtain());
                movie.setBackdrop("backdrop");
                movie.setCoverPath("path");
                movie.setPopularity(10.0f);
                movie.setTitle("Movie 1");
                if (mTwoPane) {
                    FragmentManager fm = getSupportFragmentManager();

                    DetailFragment fragment = DetailFragment.newInstance(movie);
                    fm.beginTransaction()
                            .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                            .commit();

                } else {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
                    startActivity(intent);
                }
            }
        });
        btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movie = new Movie(Parcel.obtain());
                movie.setBackdrop("backdrop");
                movie.setCoverPath("path");
                movie.setPopularity(10.0f);
                movie.setTitle("Movie 2");
                if (mTwoPane) {
                    FragmentManager fm = getSupportFragmentManager();

                    DetailFragment fragment = DetailFragment.newInstance(movie);
                    fm.beginTransaction()
                            .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                            .commit();

                } else {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
                    startActivity(intent);
                }
            }
        });
        btn = (Button) findViewById(R.id.button3);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movie = new Movie(Parcel.obtain());
                movie.setBackdrop("backdrop");
                movie.setCoverPath("path");
                movie.setPopularity(10.0f);
                movie.setTitle("Movie 3");
                if (mTwoPane) {
                    FragmentManager fm = getSupportFragmentManager();

                    DetailFragment fragment = DetailFragment.newInstance(movie);
                    fm.beginTransaction()
                            .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                            .commit();

                } else {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
                    startActivity(intent);
                }
            }
        });

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
