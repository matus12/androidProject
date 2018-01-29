package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import amagi82.flexibleratingbar.FlexibleRatingBar;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.Movie;
import cz.muni.fi.pv256.movio2.uco_396496.myapplication.database.MovieDbManager;

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    private static final String ARGS_MOVIE = "args_movie";
    private MovieDbManager mDbManager;

    private MovieInfo mMovie;

    public static DetailFragment newInstance(MovieInfo movie) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mDbManager = new MovieDbManager(getContext());
        if (args != null) {
            mMovie = args.getParcelable(ARGS_MOVIE);
        } else {
            Movie m = mDbManager.getMovies().get(0);
            if (m != null) {
                mMovie = new MovieInfo(
                        m.getTitle(),
                        m.getRating(),
                        m.getPoster_path(),
                        m.getRelease_date(),
                        m.getOverview(),
                        m.getMovie_id()
                );
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleTv = view.findViewById(R.id.detail_movie);
        TextView titleLowTv = view.findViewById(R.id.detail_movie_low);
        TextView dateReleased = view.findViewById(R.id.release_date);
        FlexibleRatingBar ratingBar = view.findViewById(R.id.rating);

        final FloatingActionButton fab = view.findViewById(R.id.fab);

        if (mMovie == null) return view;

        if (mDbManager.getMovieByName(mMovie.getOriginal_title()) != null){
            fab.setImageResource(R.drawable.ic_delete);
        } else {
            fab.setImageResource(R.drawable.ic_save_black_24dp);
        }

        final Movie movie = new Movie();
        movie.setTitle(mMovie.getOriginal_title());
        movie.setOverview(mMovie.getOverview());
        movie.setRelease_date(mMovie.getRelease_date());
        movie.setRating(mMovie.getVote_average());
        movie.setPoster_path(mMovie.getPoster_path());
        movie.setMovie_id(mMovie.getMovie_id());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDbManager.getMovieByName(mMovie.getOriginal_title()) == null) {
                    mDbManager.createMovie(movie);
                    Toast.makeText(getContext(), "Movie saved to favorites", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.ic_delete);
                } else {
                    mDbManager.deleteMovie(mMovie.getOriginal_title());
                    Toast.makeText(getContext(), "Movie deleted from favorites", Toast.LENGTH_SHORT).show();
                    fab.setImageResource(R.drawable.ic_save_black_24dp);
                    MainActivity.favoriteButton.performClick();
                }
            }
        };
        fab.setOnClickListener(onClickListener);

        if (mMovie != null) {
            titleTv.setText(mMovie.getOriginal_title());
            titleLowTv.setText(mMovie.getOverview());
            dateReleased.setText(mMovie.getRelease_date());

            ratingBar.setStepSize(0.1f);
            ratingBar.setNumStars(10);
            ratingBar.setIsIndicator(true);
            ratingBar.setColorOutlineOff(Color.rgb(255, 255, 255));
            ratingBar.setColorOutlineOn(Color.rgb(255, 255, 255));
            ratingBar.setColorFillOff(Color.rgb(200, 200, 200));
            ratingBar.setRating(Float.parseFloat(mMovie.getVote_average()));

        }
        return view;
    }
}