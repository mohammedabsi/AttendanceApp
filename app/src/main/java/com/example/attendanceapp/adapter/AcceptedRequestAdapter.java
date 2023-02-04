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
import com.example.attendanceapp.RecyclerViewInterface;
import com.example.attendanceapp.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AcceptedRequestAdapter extends RecyclerView.Adapter<AcceptedRequestAdapter.RequestsviewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<User> userArrayList;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    public AcceptedRequestAdapter(RecyclerViewInterface recyclerViewInterface, ArrayList<User> userArrayList, FirebaseFirestore firestore , Context context) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.userArrayList = userArrayList;
        this.firestore = firestore;
    }

    @NonNull
    @Override
    public RequestsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.single_accepted_item, parent, false);
//       notifyDataSetChanged();

        return new RequestsviewHolder(v, recyclerViewInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull RequestsviewHolder holder, int position) {
        User user = userArrayList.get(position);

        holder.owner_req.setText(user.getUserName());
        holder.owner_email_req.setText(user.getEmail());







    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class RequestsviewHolder extends RecyclerView.ViewHolder {

        TextView owner_req, owner_email_req;


        public RequestsviewHolder(@NonNull View itemView , RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            owner_req = itemView.findViewById(R.id.ownerName_accepted);
            owner_email_req = itemView.findViewById(R.id.acc_email_req);

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

            itemView.setOnLongClickListener(new
                                                    View.OnLongClickListener() {
                                                        @Override
                                                        public boolean onLongClick(View view) {
                                                            if (recyclerViewInterface != null) {
                                                                int pos = getAdapterPosition();
                                                                if (pos != RecyclerView.NO_POSITION) {
                                                                    recyclerViewInterface.onItemLongClick(pos);
                                                                }
                                                            }

                                                            return true;
                                                        }
                                                    });


        }

    }
}