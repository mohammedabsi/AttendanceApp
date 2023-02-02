package com.example.attendanceapp.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
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
import com.example.attendanceapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.RequestsviewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<User> userArrayList;
    String course;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    Date d = new Date();
    SimpleDateFormat enDate = new SimpleDateFormat("MMMM d, yyyy ", new Locale("en"));
    String en = enDate.format(d);


    public AttendanceAdapter(RecyclerViewInterface recyclerViewInterface, ArrayList<User> userArrayList, FirebaseFirestore firestore, Context context, String course) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.userArrayList = userArrayList;
        this.firestore = firestore;
        this.course = course;
    }

    @NonNull
    @Override
    public RequestsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.attendance_item, parent, false);

        return new RequestsviewHolder(v, recyclerViewInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull RequestsviewHolder holder, int position) {
        User user = userArrayList.get(position);

        holder.attname.setText(user.getUserName());
        holder.attemail.setText(user.getEmail());

        firestore
                .collection("User")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection(course)
                .document(course)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
//                            List<String> group = (List<String>) task.getResult().get(en);
//
//                            for (String x :group){
//                                if (x.contains(en)){
//                                    holder.attchecked.setVisibility(View.VISIBLE);
//                                }else {
//                                    holder.attchecked.setVisibility(View.INVISIBLE);
//
//                                }
//
//                            }
                            DocumentSnapshot document = task.getResult();
                            List<String> list = new ArrayList<>();

                            if (document.exists()) {
                                Map<String, Object> map = document.getData();
                                if (map != null) {
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                                        list.add(entry.getValue().toString());
                                    }


                                }

                            }
                            //So what you need to do with your list
                            for (String s : list) {
                                Log.d("aaTAG", s);
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class RequestsviewHolder extends RecyclerView.ViewHolder {

        TextView attname, attemail;
        ImageView attchecked;

        public RequestsviewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            attname = itemView.findViewById(R.id.attname);
            attemail = itemView.findViewById(R.id.attemail);
            attchecked = itemView.findViewById(R.id.attchecked);


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