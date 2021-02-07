package cz.muni.fi.pv256.movio2.uco_396496.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by matus on 11/2/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleTextView;
        public ImageView mImageView;
        private TextView numberOfStarsView;
        private Context context;
        private boolean mTwoPane;

        public ViewHolder(Context context, View itemView, boolean twoPane){
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.movies_name);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            numberOfStarsView = (TextView) itemView.findViewById(R.id.numberOfStars);
            this.context = context;
            mTwoPane = twoPane;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Movie movie = mMovies.get(position);

                if (mTwoPane) {
                    FragmentManager fm = ((FragmentActivity)mContext).getSupportFragmentManager();

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

    private List<Movie> mMovies;
    private Context mContext;
    private boolean mTwoPane;

    public MoviesAdapter(Context context, List<Movie> movies, boolean twoPane) {
        mMovies = movies;
        mContext = context;
        mTwoPane = twoPane;
    }

    private Context getContext(){
        return mContext;
    }
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(mContext, movieView, mTwoPane);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, int position) {
        Movie movie = mMovies.get(position);

        TextView textView = holder.titleTextView;
        textView.setText(movie.getTitle());
        ImageView imageView = holder.mImageView;
        TextView starsText = holder.numberOfStarsView;
        switch (movie.getTitle()){
            case "Blade Runner 2049":
                imageView.setImageResource(R.drawable.bladerunner);
                starsText.setText("7.5");
                break;
            case "It":
                imageView.setImageResource(R.drawable.it);
                starsText.setText("7.3");
                break;
            case "Thor: Ragnarok":
                imageView.setImageResource(R.drawable.thor);
                starsText.setText("7.5");
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
