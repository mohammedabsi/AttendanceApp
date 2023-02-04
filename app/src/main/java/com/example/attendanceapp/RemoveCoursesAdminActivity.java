package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.attendanceapp.adapter.RemovecoursesAdapter;
import com.example.attendanceapp.databinding.ActivityRemoveCoursesAdminBinding;
import com.example.attendanceapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RemoveCoursesAdminActivity extends AppCompatActivity {

    private ActivityRemoveCoursesAdminBinding binding;

    ArrayList<User> coursesArrayList;
    RemovecoursesAdapter removecoursesAdapter;

    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRemoveCoursesAdminBinding.inflate(getLayoutInflater());
        getSupportActionBar().setTitle("Department Head");
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        binding.removecoursesprogress.setVisibility(View.VISIBLE);
        binding.removecoursesrecycler.setLayoutManager(new LinearLayoutManager(this));

        binding.removecoursesrecycler.setHasFixedSize(true);



        coursesArrayList = new ArrayList<User>();

        removecoursesAdapter = new RemovecoursesAdapter( coursesArrayList, getApplicationContext(), firestore);
        binding.removecoursesrecycler.setAdapter(removecoursesAdapter);

//        RetrieveDataFirestore();
    }

    private void RetrieveDataFirestore() {
        String id = getIntent().getStringExtra("message");

        firestore.collection("User").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
           if (task.isSuccessful()){
               DocumentSnapshot documentSnapshot = task.getResult();
               if (documentSnapshot.exists()){
                   List<String> users = documentSnapshot.toObject(User.class).subs;

               }
           }
            }
        });

//        firestore.collection("User").whereEqualTo("id", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    if (binding.removecoursesprogress.isShown()) {
//                        binding.removecoursesprogress.setVisibility(View.GONE);
//                    }
//                    Log.d("Fire Store Error", error.getMessage());
//                    return;
//                }
//                for (DocumentChange documentChange : value.getDocumentChanges()) {
//                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
//
//                        coursesArrayList.add(documentChange.getDocument().toObject(User.class));
//                        Log.d("onEvent22:", "onEvent: " + coursesArrayList.get(0).getUserName());
//                    }
//                    removecoursesAdapter.notifyDataSetChanged();
//                    if (binding.removecoursesprogress.isShown()) {
//                        binding.removecoursesprogress.setVisibility(View.GONE);
//                    }
//
//                }
//            }
//        });


    }
}