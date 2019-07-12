package com.example.anthonygram;

import android.app.Application;

import com.example.anthonygram.model.Comment;
import com.example.anthonygram.model.Like;
import com.example.anthonygram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Like.class);

        final Parse.Configuration config = new Parse.Configuration.Builder(this)
                .applicationId("greatanthony")
                .clientKey("thegreatant")
                .server("http://mercurialant-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(config);


    }

}
