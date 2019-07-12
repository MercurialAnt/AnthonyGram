package com.example.anthonygram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.anthonygram.model.Comment;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private ArrayList<Comment> comments;
    private Context context;

    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_comment, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Comment comment = comments.get(position);
        viewHolder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfile;
        private TextView tvBody;
        private TextView tvUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }

        public void bind(Comment comment) {
            try {
                tvUsername.setText(comment.getUser().getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvBody.setText(comment.getText());
            ParseFile proImg;
            try {
                proImg = comment.getUser().fetchIfNeeded().getParseFile("profile_pic");
                if (proImg != null) {
                    Glide.with(context)
                            .load(proImg.getUrl())
                            .apply(RequestOptions.circleCropTransform())
                            .into(ivProfile);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
