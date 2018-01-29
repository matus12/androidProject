package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
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
            int moviePosition;
            if (position < mSectionSize) {
                moviePosition = position - 1;
            } else {
                moviePosition = position - 2;
            }
            if (position != RecyclerView.NO_POSITION) {
                MovieInfo movie = mMovies.get(moviePosition);

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
    private boolean mFavorites;
    private List<RequestCreator> mRequestCreators;
    private RequestCreator defaultCreator;
    private static final int TYPE_MOVIE = 0;
    private static final int TYPE_HEADER = 1;

    MoviesAdapter(Context context, List<MovieInfo> movies, int sectionSize, boolean twoPane, boolean favorites) {
        mMovies = movies;
        mContext = context;
        mTwoPane = twoPane;
        mSectionSize = sectionSize;
        mFavorites = favorites;
        mRequestCreators = Data.getInstance().getData();
        defaultCreator = Data.getInstance().getDefaultCreator();

        if (mTwoPane) {
            final FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();

            if (mMovies.size() > 0) {
                final int WHAT = 1;
                Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        if(msg.what == WHAT) {
                            DetailFragment fragment = DetailFragment.newInstance(mMovies.get(0));
                            fm.beginTransaction()
                                    .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                                    .commit();
                        }
                    }
                };
                handler.sendEmptyMessage(WHAT);

            }
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
        int moviePosition;
        if (position < mSectionSize) {
            moviePosition = position - 1;
        } else {
            moviePosition = position - 2;
        }
        if (getItemViewType(position) == TYPE_MOVIE) {

            MovieInfo movie = mMovies.get(moviePosition);

            TextView textView = holder.titleTextView;
            textView.setText(movie.getOriginal_title());
            TextView starts = holder.numberOfStars;
            starts.setText(movie.getVote_average());
            ImageView imageView = holder.mImageView;
            if (movie.getPoster_path() != null && mRequestCreators != null) {
                Picasso.with(mContext).load("https://image.tmdb.org/t/p/w500/" + movie.getPoster_path()).into(imageView);
            } else {
                imageView.setImageResource(R.drawable.noposter);
            }
        } else if (position == 0 && !mFavorites) {
            TextView textView = holder.titleTextView;
            textView.setText(R.string.comingSoon);

        } else if (position == 0 && mFavorites) {
            TextView textView = holder.titleTextView;
            textView.setText(R.string.favorites);
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
        if (mFavorites) {
            return mMovies.size() + 1;
        }
        return mMovies.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == mSectionSize) {
            return TYPE_HEADER;
        }
        return TYPE_MOVIE;
    }
}
