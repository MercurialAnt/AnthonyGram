package com.example.anthonygram;

import android.app.Application;

import com.example.anthonygram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        final Parse.Configuration config = new Parse.Configuration.Builder(this)
                .applicationId("greatanthony")
                .clientKey("thegreatant")
                .server("http://mercurialant-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(config);

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }

}
