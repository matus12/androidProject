package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        ImageView mImageView;
        TextView numberOfStars;
        private boolean mTwoPane;

        ViewHolder(Context context, View itemView, int viewType, boolean twoPane) {
            super(itemView);

            if (viewType == TYPE_MOVIE) {
                titleTextView = itemView.findViewById(R.id.movies_name);
                mImageView = itemView.findViewById(R.id.imageView);
                numberOfStars = itemView.findViewById(R.id.numberOfStars);
                mTwoPane = twoPane;
                itemView.setOnClickListener(this);
            } else {
                titleTextView = itemView.findViewById(R.id.textView);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                MovieInfo movie = mMovies.get(position);

                if (mTwoPane) {
                    FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();

                    DetailFragment fragment = DetailFragment.newInstance(movie);
                    fm.beginTransaction()
                            .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                            .commit();
                } else {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
                    view.getContext().startActivity(intent);
                }
            }
        }
    }

    private List<MovieInfo> mMovies = new ArrayList<>();
    private Context mContext;
    private int mSectionSize;
    private boolean mTwoPane;
    private List<RequestCreator> mRequestCreators;
    private RequestCreator defaultCreator;
    private static final int TYPE_MOVIE = 0;
    private static final int TYPE_HEADER = 1;

    MoviesAdapter(Context context, List<MovieInfo> movies, int sectionSize, boolean twoPane) {
        mMovies = movies;
        mContext = context;
        mTwoPane = twoPane;
        mSectionSize = sectionSize;
        mRequestCreators = Data.getInstance().getData();
        defaultCreator = Data.getInstance().getDefaultCreator();

        if (mTwoPane) {
            FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();

            DetailFragment fragment = DetailFragment.newInstance(mMovies.get(1));
            fm.beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                    .commit();
        }
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;

        if (viewType == TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.header, parent, false);
            viewHolder = new ViewHolder(mContext, headerView, TYPE_HEADER, mTwoPane);
        } else {
            View movieView = inflater.inflate(R.layout.item_movie, parent, false);
            viewHolder = new ViewHolder(mContext, movieView, TYPE_MOVIE, mTwoPane);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MOVIE) {
            MovieInfo movie = mMovies.get(position);

            TextView textView = holder.titleTextView;
            textView.setText(movie.getOriginal_title());
            TextView starts = holder.numberOfStars;
            starts.setText(movie.getVote_average());
            ImageView imageView = holder.mImageView;
            if (movie.getPoster_path() != null) {
                mRequestCreators.get(position).into(imageView);
            } else {
                defaultCreator.into(imageView);
            }
        } else if (position == 0) {
            TextView textView = holder.titleTextView;
            textView.setText(R.string.comingSoon);
        } else {
            TextView textView = holder.titleTextView;
            textView.setText(R.string.inCinemas);
        }
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        }
        return mMovies.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == mSectionSize) {
            return TYPE_HEADER;
        }
        return TYPE_MOVIE;
    }
}
