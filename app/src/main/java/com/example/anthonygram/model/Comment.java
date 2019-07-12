package com.example.anthonygram.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {


    private static final String KEY_TEXT = "text";
    private static final String KEY_USER = "user";
    private static final String KEY_CREATED_AT = "createdAt";

    public Comment () {

    }
    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(com.parse.ParseUser user) {
        put(KEY_USER, user);
    }

    public static class Query extends ParseQuery<Comment> {
        public Query() {
            super(Comment.class);
        }

        public Query getTop(int amount) {
            setLimit(amount);

            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }

        public Query thisPost() {
            return this;
        }


        public Query orderByDate() {
            addDescendingOrder(KEY_CREATED_AT);
            return this;
        }

        public Query offSet(int amount) {
            setSkip(amount);
            return this;
        }
    }
}
