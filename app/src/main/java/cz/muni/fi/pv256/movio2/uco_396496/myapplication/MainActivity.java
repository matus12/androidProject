package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnMovieSelectListener {

    private boolean mTwoPane;
    private Context mContext;
    ArrayList<Movie> movies;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        TextView emptyView = findViewById(R.id.empty_view);

        DownloadDataTask downloadTask = new DownloadDataTask(MainActivity.this);
        downloadTask.execute();

        movies = new ArrayList<>();
        Movie movie = new Movie(Parcel.obtain());
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
        } else {
            rvMovies.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        MoviesAdapter adapter = new MoviesAdapter(this, movies, mTwoPane);
        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    public void onTaskFinished() {
        Gson gson = new GsonBuilder().create();
        MovieInfo info = gson.fromJson(Data.getInstance().getData(), MovieInfo.class);
        Toast.makeText(this, "TITLE: " + info.getOriginal_title() + "\nRating: " + info.getVote_average(), Toast.LENGTH_SHORT).show();
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

    private static class DownloadDataTask extends AsyncTask<Void, Integer, Integer> {

        private final WeakReference<MainActivity> mActivityWeakReference;

        DownloadDataTask(MainActivity mainActivity) {
            mActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/550?api_key=4d1917c52de723c48c649b3eb9955c8f")
                    .build();

            Call call = client.newCall(request);
            Response response;
            try {
                response = call.execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                Data.getInstance().setData(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.onTaskFinished();
        }
    }
}
