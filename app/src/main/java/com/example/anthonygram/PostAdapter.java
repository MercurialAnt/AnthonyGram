package com.example.anthonygram;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.anthonygram.model.Like;
import com.example.anthonygram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<Post> posts;
    private Context context;
    ParseRelation<Like> parseRelation;


    public PostAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_post, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Post post = posts.get(position);
        viewHolder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfile;
        private ImageView ivPostImg;
        private ImageView ivLike;
        private TextView tvUsername;
        private TextView tvDescription;
        private TextView tvTimeStamp;
        private TextView tvLikedBy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            ivPostImg = itemView.findViewById(R.id.ivPostImg);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimeStamp = itemView.findViewById(R.id.tvTimestamp);
            tvLikedBy = itemView.findViewById(R.id.tvLikedBy);
        }
        public void bind(final Post post) {

            boolean isLike = false;
            try {
                isLike = !post.getRelation("like").getQuery().whereEqualTo("user", ParseUser.getCurrentUser()).find().isEmpty();
                tvUsername.setText(post.getUser().fetchIfNeeded().getUsername());
                if (isLike) {
                    tvLikedBy.setText("Liked by " + post.getUser().fetchIfNeeded().getUsername());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDescription.setText(post.getDescription());
            ParseFile image = post.getImage();
            if (image != null)  {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivPostImg);
            }
            tvTimeStamp.setText(post.getCreatedAt().toString());
            ivPostImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra("id", post.getObjectId());
                    context.startActivity(intent);
                }
            });

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!post.getRelation("like").getQuery().whereEqualTo("user", ParseUser.getCurrentUser()).find().isEmpty())
                            return;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    final Like like = new Like();
                    like.setUser(ParseUser.getCurrentUser());

                    like.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            try {
                                post.getRelation("like").add(like);
                                post.save();
                                tvLikedBy.setText("Liked by " + post.getUser().fetchIfNeeded().getUsername());

                            } catch (ParseException ea) {
                                ea.printStackTrace();
                            }
                        }
                    });

                }
            });



            ParseFile proImg;
            try {
                proImg = post.getUser().fetchIfNeeded().getParseFile("profile_pic");
                Glide.with(context)
                        .load(proImg.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfile);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
