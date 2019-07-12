package com.example.anthonygram.model;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;



@ParseClassName("Like")
public class Like extends ParseObject {

    private static final String KEY_USER = "user";

    public Like() {

    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
