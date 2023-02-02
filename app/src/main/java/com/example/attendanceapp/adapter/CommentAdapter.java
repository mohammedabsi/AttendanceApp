package com.example.attendanceapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.attendanceapp.model.Comment;
import com.example.attendanceapp.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private ArrayList<Comment> commentList;
    private Context mContext;
    public CommentAdapter(ArrayList<Comment> commentList, Context mContext) {
        this.commentList = commentList;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);


        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment user_comment = commentList.get(position);

        holder.cooment_txt.setText(user_comment.getCommentText());
        holder.comment_UserName.setText(user_comment.getCommentusername());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView cooment_txt , comment_UserName;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            cooment_txt = itemView.findViewById(R.id.cooment_txt);
            comment_UserName = itemView.findViewById(R.id.comment_UserName);
        }
    }
}
