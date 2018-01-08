package cz.muni.fi.pv256.movio2.uco_396496.myapplication.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MovieDbManager {

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_OVERVIEW = 2;
    public static final int COL_MOVIE_RELEASE_DATE = 3;
    private static final String[] MOVIE_COLUMNS = {
            MovieDbContract.MovieEntry._ID,
            MovieDbContract.MovieEntry.COLUMN_MOVIE_TITLE_TEXT,
            MovieDbContract.MovieEntry.COLUMN_OVERVIEW_TEXT,
            MovieDbContract.MovieEntry.COLUMN_RELEASE_DATE_TEXT
    };

    private static final String WHERE_ID = MovieDbContract.MovieEntry._ID + " = ?";

    private Context mContext;

    public MovieDbManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public void createMovie(Movie movie) {
        if (movie == null) {
            throw new NullPointerException("movie == null");
        }
        if (movie.getId() != null) {
            throw new IllegalStateException("movie id shouldn't be set");
        }
        if (movie.getOverview() == null) {
            throw new IllegalStateException("movie overview cannot be null");
        }
        if (movie.getRelease_date() == null) {
            throw new IllegalStateException("movie release date cannot be null");
        }
        if (movie.getTitle() == null) {
            throw new IllegalStateException("movie title date cannot be null");
        }

        movie.setId(ContentUris.parseId(mContext.getContentResolver().insert(MovieDbContract.MovieEntry.CONTENT_URI, prepareMovieValues(movie))));
    }

    public List<Movie> getMovies() {
        Cursor cursor = mContext.getContentResolver().query(MovieDbContract.MovieEntry.CONTENT_URI, MOVIE_COLUMNS, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            List<Movie> movies = new ArrayList<>(cursor.getCount());
            try {
                while (!cursor.isAfterLast()) {
                    movies.add(getMovie(cursor));
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
            return movies;
        }

        return Collections.emptyList();
    }

    public void deleteMovie(Movie movie) {
        if (movie == null) {
            throw new NullPointerException("movie == null");
        }
        if (movie.getId() == null) {
            throw new IllegalStateException("movie id cannot be null");
        }

        mContext.getContentResolver().delete(MovieDbContract.MovieEntry.CONTENT_URI, WHERE_ID, new String[]{String.valueOf(movie.getId())});
    }

    private ContentValues prepareMovieValues(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_TITLE_TEXT, movie.getTitle());
        values.put(MovieDbContract.MovieEntry.COLUMN_OVERVIEW_TEXT, movie.getOverview());
        values.put(MovieDbContract.MovieEntry.COLUMN_RELEASE_DATE_TEXT, movie.getRelease_date());
        return values;
    }

    private Movie getMovie(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getLong(COL_MOVIE_ID));
        movie.setTitle(cursor.getString(COL_MOVIE_TITLE));
        movie.setOverview(cursor.getString(COL_MOVIE_OVERVIEW));
        movie.setRelease_date(cursor.getString(COL_MOVIE_RELEASE_DATE));

        return movie;
    }
}