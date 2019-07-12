package com.example.instagramapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramapp.model.Post;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    // variables to hold the context and data source
    private Context context;
    private List<Post> posts;
    public int whichFragment;


    public PostsAdapter(Context context, List<Post> posts, int whichFragment) {
        this.context = context;
        this.posts = posts;
        this.whichFragment = whichFragment;
    }

    // methods that are implemented from extending the PostsAdapter class
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvHandle;
        private TextView tvDescription;
        private ImageView ivImage;

        private TextView tvTime;
        private TextView tvNumLikes;
        private TextView tvNumComments;
        private TextView tvHandle2;
        private TextView tvTimeStamp;
        private ImageButton ibLikes;
        private ImageButton ibComments;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTime = itemView.findViewById(R.id.tvTimeStamp);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            tvNumComments = itemView.findViewById(R.id.tvNumComments);
            ibLikes = itemView.findViewById(R.id.ibLike);
            ibComments = itemView.findViewById(R.id.ibComment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position, this won't work if the class is static
                Post post = posts.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, InstagramDetailActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                // show the activity
                context.startActivity(intent);
            }
        }
        public void bind(final Post post) {
            if(whichFragment == 0) {

                ibLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<ParseObject> likedBy = (ArrayList<ParseObject>) post.get("likedBy");
                        if(!likedBy.contains(ParseUser.getCurrentUser())) {
                            likedBy.add(ParseUser.getCurrentUser());
                            tvNumLikes.setText(String.valueOf(likedBy.size()));
                            post.put("likedBy", likedBy);
                            ibLikes.setImageResource(R.drawable.ufi_heart_active);
                            ibLikes.setColorFilter(Color.argb(255, 255, 0, 0));
                        }
                        else {
                            // unlike
                            likedBy.remove(ParseUser.getCurrentUser());
                            tvNumLikes.setText(String.valueOf(likedBy.size()));
                            post.put("likedBy", likedBy);
                            ibLikes.setImageResource(R.drawable.ufi_heart);
                            ibLikes.setColorFilter(Color.argb(255, 0, 0, 0));
                        }
                        post.saveInBackground();
                        tvNumLikes.setText(String.valueOf(likedBy.size()));
                    }
                });
                ArrayList<ParseObject> likedBy = (ArrayList<ParseObject>) post.get("likedBy");
                // total number of likes for post
                if(likedBy == null) {
                    likedBy = new ArrayList<ParseObject>();
                    post.put("likedBy", likedBy);
                }
                tvNumLikes.setText(String.valueOf(likedBy.size()));
                tvHandle.setText(post.getKeyUser().getUsername());
                String date = getRelativeTimeAgo((post.getCreatedAt().toString()));
                tvTime.setText(date);

                if(likedBy.contains(ParseUser.getCurrentUser())) {
                    ibLikes.setImageResource(R.drawable.ufi_heart_active);
                    ibLikes.setColorFilter(Color.argb(255, 255, 0, 0));
                } else {
                    ibLikes.setImageResource(R.drawable.ufi_heart);
                    ibLikes.setColorFilter(Color.argb(255, 0, 0, 0));

                }

                ParseFile image = post.getImage();
                if(image != null) {
                    Glide.with(context).load(image.getUrl()).into(ivImage);
                }

                tvDescription.setText(post.getKeyDescription());
            } else if(whichFragment == 1) {
                tvHandle.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);
                tvNumLikes.setVisibility(View.GONE);
                tvNumComments.setVisibility(View.GONE);
                ibLikes.setVisibility(View.GONE);
                ibComments.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                ParseFile image = post.getImage();

                // gets metrics of current screen dimensions
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                int pxWidth = displayMetrics.widthPixels;

                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(pxWidth/3, pxWidth/3);
                ivImage.setLayoutParams(layoutParams);
                if(image != null) {
                    Glide.with(context).load(image.getUrl()).into(ivImage);
                }
            }
        }
    }
    // methods for refreshing layout/posts
    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}
