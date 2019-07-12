package com.example.anthonygram;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.anthonygram.model.Comment;
import com.example.anthonygram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseUser.getCurrentUser;

public class PostDetailsActivity extends AppCompatActivity {

    Post post;
    Context context;
    ArrayList<Comment> comments;
    CommentAdapter commentAdapter;
    RecyclerView rvComments;
    final int current = 20;

    ParseRelation<Comment> parseRelation;

    private SwipeRefreshLayout swipeContainer;

    private ImageView  ivProfile;
    private TextView tvDescription;
    private TextView tvUsername;
    private TextView tvTimestamp;
    private EditText etComments;
    private Button btnPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        rvComments = findViewById(R.id.rvComments);

        context = PostDetailsActivity.this;
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(context, comments);
        rvComments.setAdapter(commentAdapter);
        rvComments.setLayoutManager(new LinearLayoutManager(context));
        swipeContainer = findViewById(R.id.swipeContainer);


        String postId = getIntent().getStringExtra("id");


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                comments.clear();
                loadTopComments(0, current);
                commentAdapter.notifyDataSetChanged();
            }
        });

        // -TODO put a progress bar for getting the post
        final Post.Query postQuery = new Post.Query();
        try {
            postQuery.get(postId);
            postQuery.findInBackground(new FindCallback<Post>() {

                @Override
                public void done(List<Post> objects, ParseException e) {
                    if (!objects.isEmpty()) {
                        post = objects.get(0);
                        inflateActivity();
                        loadTopComments(0, current);
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    private void inflateActivity() {
        parseRelation = post.getRelation("comment");
        tvUsername = findViewById(R.id.tvUsername);
        tvDescription = findViewById(R.id.tvDescription);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivProfile = findViewById(R.id.ivProfile);
        etComments = findViewById(R.id.etComment);
        btnPost = findViewById(R.id.btnPost);



        try {
            tvUsername.setText(post.getUser().fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvDescription.setText(post.getDescription());
        tvTimestamp.setText(post.getCreatedAt().toString());

        ParseFile proImg;
        try {
            proImg = post.getUser().fetchIfNeeded().getParseFile("profile_pic");

            Glide.with(PostDetailsActivity.this)
                    .load(proImg.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfile);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Comment comment = new Comment();
                comment.setText(etComments.getText().toString());
                comment.setUser(getCurrentUser());

                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        etComments.setText("");
                        parseRelation.add(comment);
                        try {
                            post.save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        loadTopComments(0, current);
                    }
                });
                // save in background
            }
        });
    }


    protected void loadTopComments(int offset, int amount) {
        parseRelation
                .getQuery()
                .setSkip(offset)
                .setLimit(current)
                .include("user")
                .addDescendingOrder("createdAt")
                .findInBackground(new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> objects, ParseException e) {
                        if (e == null) {
                            comments.clear();
                            comments.addAll(objects);
                            commentAdapter.notifyDataSetChanged();

                        } else {
                            e.printStackTrace();
                        }
                        swipeContainer.setRefreshing(false);
                    }
                });

    }
}
