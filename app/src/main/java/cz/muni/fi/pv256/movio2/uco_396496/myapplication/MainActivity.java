package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.muni.fi.pv256.movio2.uco_396496.myapplication.services.DownloadService;

public class MainActivity extends AppCompatActivity
        implements MainFragment.OnMovieSelectListener {

    private boolean mTwoPane;
    private RecyclerView rvMovies;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMovies = findViewById(R.id.rvMovies);
        TextView emptyView = findViewById(R.id.empty_view);
        this.savedInstanceState = savedInstanceState;

        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        ResponseReceiver responseReceiver = new ResponseReceiver(this);
        registerReceiver(responseReceiver, filter);
    }

    public void setMoviesView(ArrayList<MovieInfo> movieList) {
        MoviesAdapter adapter = new MoviesAdapter(this, movieList, movieList.size(), mTwoPane);
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

    @Override
    public void onStart() {
        super.onStart();
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

    /*private static class DownloadDataTask extends AsyncTask<Boolean, Integer, Integer> {
        private boolean secondTaskFinished = false;

        private final WeakReference<MainActivity> mActivityWeakReference;

        DownloadDataTask(MainActivity mainActivity) {
            mActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        protected Integer doInBackground(Boolean... booleen) {
            String fromYear, fromMonth, fromDay, toYear, toMonth, toDay;
            Date currentTime = Calendar.getInstance().getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);

            if (booleen[0]) {
                fromYear = Integer.toString(calendar.get(Calendar.YEAR));
                fromMonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
                fromDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
                calendar.add(Calendar.DAY_OF_MONTH, 7);
                toYear = Integer.toString(calendar.get(Calendar.YEAR));
                toMonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
                toDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://api.themoviedb.org/3/discover/movie?primary_release_date.gte="
                                + fromYear + "-" + fromMonth + "-" + fromDay
                                + "&primary_release_date.lte="
                                + toYear + "-" + toMonth + "-" + toDay +
                                "&sort_by=popularity.desc&with_original_language=en&" +
                                "api_key=4d1917c52de723c48c649b3eb9955c8f")
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
            } else {
                toYear = Integer.toString(calendar.get(Calendar.YEAR));
                toMonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
                toDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
                calendar.add(Calendar.DAY_OF_MONTH, -14);
                fromYear = Integer.toString(calendar.get(Calendar.YEAR));
                fromMonth = Integer.toString(calendar.get(Calendar.MONTH) + 1);
                fromDay = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://api.themoviedb.org/3/discover/movie?primary_release_date.gte="
                                + fromYear + "-" + fromMonth + "-" + fromDay
                                + "&primary_release_date.lte="
                                + toYear + "-" + toMonth + "-" + toDay +
                                "&sort_by=popularity.desc&with_original_language=en&" +
                                "api_key=4d1917c52de723c48c649b3eb9955c8f")
                        .build();

                Call call = client.newCall(request);
                Response response;
                try {
                    response = call.execute();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    Data.getInstance().setData2(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                secondTaskFinished = true;
            }
            return null;
        }
    }*/

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "MESSAGE_PROCESSED";
        private MainActivity mMainActivity;

        public ResponseReceiver(MainActivity activity) {
            mMainActivity = activity;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            MoviesList movieList = intent.getParcelableExtra("movies");
            ArrayList<MovieInfo> movies = movieList.getResults();

            mMainActivity.setMoviesView(movies);
        }
    }
}
