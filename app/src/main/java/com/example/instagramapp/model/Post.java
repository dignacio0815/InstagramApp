package com.example.instagramapp.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_USER = "user";
    // handle post object right here
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_LIKED_BY = "likedBy";

    // accessor and mutator methods
    public String getKeyDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setKeyDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setKeyImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getKeyUser() {
        return getParseUser(KEY_USER);
    }

    // Getter to return the date of creation
    public Date getKeyCreatedAt() {return getDate(KEY_CREATED_AT);}

    public void setKeyUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void like() {
        ParseUser u = ParseUser.getCurrentUser();
        add(KEY_LIKED_BY, u);
    }

    public void unlike() {
        ParseUser u = ParseUser.getCurrentUser();
        ArrayList<ParseUser> users = new ArrayList<>();
        users.add(u);
        removeAll(KEY_LIKED_BY, users);
    }

//    public int getNumLikes() {
//        if(getLikedBy() == null) {
//
//        }
//        return getLikedBy().length();
//    }

    // creating an inner class to Querry our post models
    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
}
