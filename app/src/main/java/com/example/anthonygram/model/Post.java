package com.example.anthonygram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";
    private static final String KEY_CREATED_AT = "createdAt";




    public Post() {
        // for parcel
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }



    // -TODO fix the query to order by date

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop(int amount) {
            setLimit(amount);

            return this;
        }

        public Query withUser() {
            include("user");
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

        public Query onlyCurrentUser() {
            whereEqualTo(KEY_USER, ParseUser.getCurrentUser());
            return this;
        }
    }
}
