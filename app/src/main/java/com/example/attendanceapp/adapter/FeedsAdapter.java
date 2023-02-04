package com.example.attendanceapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapp.R;
import com.example.attendanceapp.RecyclerViewInterface;
import com.example.attendanceapp.model.FeedBack;
import com.example.attendanceapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.RequestsviewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<FeedBack> feedArrayList;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    public FeedsAdapter(RecyclerViewInterface recyclerViewInterface, ArrayList<FeedBack> feedArrayList, FirebaseFirestore firestore , Context context) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.feedArrayList = feedArrayList;
        this.firestore = firestore;
    }

    @NonNull
    @Override
    public RequestsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.feedback_item, parent, false);
//       notifyDataSetChanged();

        return new RequestsviewHolder(v, recyclerViewInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull RequestsviewHolder holder, int position) {
        FeedBack feedBack = feedArrayList.get(position);

        holder.feedback_name.setText(feedBack.getFeedname());
        holder.feedback_email.setText(feedBack.getFeedemail());
        holder.feedstatus.setText(feedBack.getFeedstatus());

if (holder.feedstatus.getText().toString().equalsIgnoreCase("Accepted")){
    holder.feedstatus.setTextColor(context.getResources().getColor(R.color.teal_700));


} else if (holder.feedstatus.getText().toString().equalsIgnoreCase("Rejected")) {
    holder.feedstatus.setTextColor(context.getResources().getColor(R.color.red));
}else {
    holder.feedstatus.setTextColor(context.getResources().getColor(R.color.darkgray));

}

        Picasso.get().load(feedBack.getFeedimage()).into(holder.postimg);
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equalsIgnoreCase("admin@admin.com")){


                   holder.reject_feed.setVisibility(View.VISIBLE);
                   holder.accept_feed.setVisibility(View.VISIBLE);


        }else {
            holder.reject_feed.setVisibility(View.GONE);
            holder.accept_feed.setVisibility(View.GONE);
        }





        holder.accept_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), holder.feedback_name.getText() + "\n is accepted ", Toast.LENGTH_SHORT).show();
                firestore.collection("Feedback").document(feedBack.getFeedId()).update("feedstatus", "Accepted");

                firestore.collection("Feedback").document(feedBack.getFeedId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if (task.isSuccessful()){
                       holder.feedstatus.setText(task.getResult().getString("feedstatus"));
                       holder.feedstatus.setTextColor(context.getResources().getColor(R.color.teal_700));

                   }
                    }
                });

            }
        });
        holder.reject_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), holder.feedback_name.getText() + "\n is rejected :(", Toast.LENGTH_SHORT).show();
                firestore.collection("Feedback").document(feedBack.getFeedId()).update("feedstatus", "Rejected");
                firestore.collection("Feedback").document(feedBack.getFeedId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            holder.feedstatus.setText(task.getResult().getString("feedstatus"));
                            holder.feedstatus.setTextColor(context.getResources().getColor(R.color.red));

                        }
                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return feedArrayList.size();
    }

    public static class RequestsviewHolder extends RecyclerView.ViewHolder {

        TextView feedback_name, feedback_email , feedstatus;
        ImageView postimg ;
        ImageButton accept_feed, reject_feed;

        public RequestsviewHolder(@NonNull View itemView , RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            feedback_name = itemView.findViewById(R.id.feedback_name);
            feedback_email = itemView.findViewById(R.id.feedback_email);
            accept_feed = itemView.findViewById(R.id.accept_feed);
            reject_feed = itemView.findViewById(R.id.reject_feed);
            feedstatus = itemView.findViewById(R.id.feedstatus);
            postimg = itemView.findViewById(R.id.postimg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });


        }

    }
}