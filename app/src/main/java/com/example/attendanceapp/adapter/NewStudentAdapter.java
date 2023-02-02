package com.example.attendanceapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapp.R;
import com.example.attendanceapp.model.Comment;
import com.example.attendanceapp.model.User;

import java.util.ArrayList;

public class NewStudentAdapter extends RecyclerView.Adapter<NewStudentAdapter.CommentViewHolder> {
    private ArrayList<User> commentList;
    private Context mContext;
    public NewStudentAdapter(ArrayList<User> commentList, Context mContext) {
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
        User uStudent = commentList.get(position);

        holder.stdname.setText(uStudent.getUserName());
        holder.stdemail.setText(uStudent.getEmail());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView stdname , stdemail;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            stdname = itemView.findViewById(R.id.comment_UserName);
            stdemail = itemView.findViewById(R.id.cooment_txt);
        }
    }
}
