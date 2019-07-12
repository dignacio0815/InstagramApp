package com.example.instagramapp.fragments;

import android.support.v7.widget.GridLayoutManager;
import android.util.Log;

import com.example.instagramapp.PostsAdapter;
import com.example.instagramapp.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment{

    @Override
    protected void setRecyclerView() {
        whichFragment = 1;
        // set the adapter in the recycler view
        adapter = new PostsAdapter(getContext(), mPosts, whichFragment);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }
    @Override
    protected void queryPosts(int page) {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        // register  a sub parse class to encapsulate our data when dealing with parse classes
        ParseObject.registerSubclass(Post.class);
        postQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postQuery.include(Post.KEY_USER);
        // will only load 20 Post image items at a time
        postQuery.setLimit(20);
        postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                mPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                for (int i = 0; i < posts.size(); ++i) {
                    Log.d("ComposeActivity", "Post[" + i + "] = " + posts.get(i).getKeyDescription() + "\nusername = " + posts.get(i).getKeyUser().getUsername());
                }
            }
        });
    }
}
