package com.example.anthonygram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anthonygram.EndlessRecyclerViewScrollListener;
import com.example.anthonygram.PostAdapter;
import com.example.anthonygram.R;
import com.example.anthonygram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public static final String TAG = "PostsFragment";

    final int increment = 5;
    final int start = 20;
    int current;

    private RecyclerView rvPosts;
    protected PostAdapter postAdapter;
    protected ArrayList<Post> posts;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPosts = view.findViewById(R.id.rvPosts);
        current = start;

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        rvPosts.setAdapter(postAdapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeContainer = view.findViewById(R.id.swipeContainer);

        scrollListener = new EndlessRecyclerViewScrollListener(new LinearLayoutManager(getContext())) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                loadTopPosts(current, increment);
                current += increment;
            }


        };


        rvPosts.addOnScrollListener(scrollListener);


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                posts.clear();
                loadTopPosts(0, current);
                scrollListener.resetState();
                postAdapter.notifyDataSetChanged();
            }
        });

        loadTopPosts(0, current);
    }

    protected void loadTopPosts(int offset, int amount) {
        final Post.Query postQuery = new Post.Query();
        postQuery.offSet(offset).getTop(amount).withUser().orderByDate();


        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts.addAll(objects);
                    postAdapter.notifyDataSetChanged();

                } else {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);

            }
        });

    }
}
