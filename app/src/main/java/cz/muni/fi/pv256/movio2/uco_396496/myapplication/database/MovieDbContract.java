package cz.muni.fi.pv256.movio2.uco_396496.myapplication.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieDbContract {
    public static final String CONTENT_AUTHORITY = "cz.muni.fi.pv256.movio2.uco_396496.myapplication";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_TITLE_TEXT = "movie_title";
        public static final String COLUMN_RELEASE_DATE_TEXT = "release_date";
        public static final String COLUMN_OVERVIEW_TEXT = "overview";
        public static final String COLUMN_RATING_TEXT = "rating";
        public static final String COLUMN_POSTER_PATH_TEXT = "poster_path";
        public static final String COLUMN_MOVIE_ID_TEXT = "movie_id";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
