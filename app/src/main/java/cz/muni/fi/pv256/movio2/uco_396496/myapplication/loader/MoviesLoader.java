package cz.muni.fi.pv256.movio2.uco_396496.myapplication.loader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.Movie;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.MovieDbManager;

public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

    private MovieDbManager mDbManager;

    public MoviesLoader(Context context) {
        super(context);
        mDbManager = new MovieDbManager(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        return mDbManager.getMovies();
    }

    @Override
    public void deliverResult(List<Movie> data) {
        super.deliverResult(data);
    }
}
