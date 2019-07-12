package com.example.instagramapp;

import android.app.Application;

import com.example.instagramapp.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application { // Parse class to set up Parsing for MainActivity

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("facebook")
                .clientKey("facebookMPK0815")
                .server("http://dignacio0815-fbu-instagram.herokuapp.com/parse")
                .build(); // sets up Parse serve by taking in app. Id, client key, server
        Parse.initialize(configuration);
    }
}
