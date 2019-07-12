package com.example.instagramapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramapp.model.Post;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class InstagramDetailActivity extends AppCompatActivity {
    // movie to display
    Post post;
    private TextView tvHandle;
    private TextView tvDescription;
    private ImageView ivImage;
    private TextView tvNumLikes;
    private TextView tvTime;
    private TextView tvComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_detail);
        tvHandle = findViewById(R.id.tvHandle);
        tvDescription = findViewById(R.id.tvDescription);
        tvNumLikes = findViewById(R.id.tvNumLikes);
        tvTime = findViewById(R.id.tvTimeStamp);
        ivImage = findViewById(R.id.ivImage);
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        // total number of likes for post
        if (post.get("likedBy") == null) {
            ArrayList newList = new ArrayList<>();
            post.put("likedBy", newList);
        }
        ArrayList<ParseObject> list = (ArrayList<ParseObject>) post.get("likedBy");
        tvNumLikes.setText(String.valueOf(list.size()));
        String date = getRelativeTimeAgo((post.getCreatedAt().toString()));
        tvTime.setText(date);
        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
       // tvHandle.setText(post.getKeyUser().getUsername());
        tvDescription.setText(post.getKeyDescription());
        ParseFile image = post.getImage();
        Glide.with(this).load(image.getUrl()).into(ivImage);
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
