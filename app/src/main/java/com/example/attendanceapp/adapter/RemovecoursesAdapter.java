package com.example.attendanceapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapp.R;
import com.example.attendanceapp.model.Comment;
import com.example.attendanceapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RemovecoursesAdapter extends RecyclerView.Adapter<RemovecoursesAdapter.CommentViewHolder> {
    private ArrayList<User> commentList;
    private Context mContext;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();



    public RemovecoursesAdapter(ArrayList<User> commentList, Context mContext , FirebaseFirestore firestore ) {
        this.commentList = commentList;
        this.mContext = mContext;
        this.firestore = firestore;

    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.courses_item, parent, false);


        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        User user_comment = commentList.get(position);

      //  holder.cooment_txt.setText(user_comment.getSubs().get(position));

        Log.d("binder22", "onBindViewHolder: "+user_comment.getSubs().get(position));

       // Toast.makeText(mContext, "User account deleted.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView cooment_txt ;

         ImageButton comment_UserName;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            cooment_txt = itemView.findViewById(R.id.comment_UserName);
            comment_UserName = itemView.findViewById(R.id.deletecourse);
        }
    }
}
