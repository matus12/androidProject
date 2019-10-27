package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        DownloadDataTask downloadTask = new DownloadDataTask(MainActivity.this);
        downloadTask.execute(true);
        DownloadDataTask downloadTask2 = new DownloadDataTask(MainActivity.this);
        downloadTask2.execute(false);
    }

    public void onTaskFinished() {
        Gson gson = new GsonBuilder().create();
        if (Data.getInstance().getData2() == null) {
        }
        MoviesList list = gson.fromJson(Data.getInstance().getData(), MoviesList.class);
        MoviesList list2 = gson.fromJson(Data.getInstance().getData2(), MoviesList.class);
        if (list != null) {
            Data.getInstance().setMovies(list.getResults());
        }
        if (list2 != null) {
            Data.getInstance().setMoviesInTheaters(list2.getResults());
        }

        List<MovieInfo> movieList = new ArrayList<>(Data.getInstance().getMovies());
        movieList.addAll(Data.getInstance().getMoviesInTheaters());

        MoviesAdapter adapter = new MoviesAdapter(this, movieList, Data.getInstance()
                .getMovies().size(), mTwoPane);
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

    private static class DownloadDataTask extends AsyncTask<Boolean, Integer, Integer> {
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

        @Override
        protected void onPostExecute(Integer result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity == null) {
                return;
            }
            if (secondTaskFinished) {
                activity.onTaskFinished();
            }
        }
    }
}
