package com.codepath.wjzhang.flixster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codepath.wjzhang.flixster.DetailActivity;
import com.codepath.wjzhang.flixster.MainActivity;
import com.codepath.wjzhang.flixster.R;
import com.codepath.wjzhang.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter","onCreateViewHolder");
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter","onBindViewHolder " + position);
        // Get the movie at the passed in position
        Movie movie = movies.get(position);
        // Bind the movie data into the VH
        holder.bind(movie);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout container;
        TextView tvTitle;
        TextView tvOverview;
        TextView tvRating;
        ImageView ivPoster;
        ImageView ivPlayBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            tvRating = itemView.findViewById(R.id.tvRating);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            ivPlayBtn = itemView.findViewById(R.id.ivPlayBtn);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(final Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            tvRating.setText(String.format("Rating: %.1f/10", (float) movie.getRating()));

            String imageUrl;
            // if phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then imageUrl = back drop image
                imageUrl = movie.getBackdropPath();
            }else{
                // else imageUrl = poster image
                imageUrl = movie.getPosterPath();
            }

//            Glide.with(context).load(imageUrl).into(ivPoster);
            final ProgressBar progressBar =itemView.findViewById(R.id.loadingBar);
            int radius = 30; // corner radius, higher value = more rounded
            int margin = 8; // crop margin, set to 0 for corners with no crop
            Glide.with(context)
                    .load(imageUrl)
                    //.placeholder(Drawable.createFromPath("ic_pineapple_icon"))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            if (movie.getRating() < 6){
                                ivPlayBtn.setVisibility(View.GONE);
                            }
                            else{
                                ivPoster.setColorFilter(new PorterDuffColorFilter(Color.argb(50, 0, 0, 0), PorterDuff.Mode.SRC_ATOP));
                            }
                            return false;
                        }
                    })
                    .fitCenter()
                    .transform(new MultiTransformation(new FitCenter(), new RoundedCornersTransformation(radius, margin)))
                    //.transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivPoster);
            // 1. Register click listener on the whole row

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 2. Navigate to a new activity on tap
                    Intent i = new Intent(context, DetailActivity.class);
                    //i.putExtra("title",movie.getTitle());
                    Pair<View, String> p1 = Pair.create((View)tvTitle, "title");
                    Pair<View, String> p2 = Pair.create((View)tvOverview, "overview");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2);
                    i.putExtra("movie", Parcels.wrap(movie));
                    context.startActivity(i, options.toBundle());
//                    Toast.makeText(context, movie.getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
