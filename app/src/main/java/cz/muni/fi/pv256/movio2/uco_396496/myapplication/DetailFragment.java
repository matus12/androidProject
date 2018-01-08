package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        if (args != null) {
            mMovie = args.getParcelable(ARGS_MOVIE);
        }
        mDbManager = new MovieDbManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleTv = view.findViewById(R.id.detail_movie);
        TextView titleLowTv = view.findViewById(R.id.detail_movie_low);
        TextView dateReleased = view.findViewById(R.id.release_date);
        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie movie = new Movie();
                movie.setTitle(mMovie.getOriginal_title());
                movie.setOverview(mMovie.getOverview());
                movie.setRelease_date(mMovie.getRelease_date());

                mDbManager.createMovie(movie);
                fab.setImageResource(R.drawable.ic_delete);
            }
        });

        if (mMovie != null) {
            titleTv.setText(mMovie.getOriginal_title());
            titleLowTv.setText(mMovie.getOverview());
            dateReleased.setText(mMovie.getRelease_date());
        }
        return view;
    }
}