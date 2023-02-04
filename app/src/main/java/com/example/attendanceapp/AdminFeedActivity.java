package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.attendanceapp.adapter.FeedsAdapter;
import com.example.attendanceapp.adapter.RequestsAdapter;
import com.example.attendanceapp.databinding.ActivityAdminFeedBinding;
import com.example.attendanceapp.model.FeedBack;
import com.example.attendanceapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminFeedActivity extends AppCompatActivity implements RecyclerViewInterface {

    private ActivityAdminFeedBinding binding;
    ArrayList<FeedBack> feedUserArrayList;
    FeedsAdapter feedsAdapter;

    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminFeedBinding.inflate(getLayoutInflater());
        getSupportActionBar().setTitle("Department Head");
        setContentView(binding.getRoot());
        setContentView(binding.getRoot());

        binding.feedadminprogres.setVisibility(View.VISIBLE);
        binding.adminrecycler.setLayoutManager(new LinearLayoutManager(this));

        binding.adminrecycler.setHasFixedSize(true);




        firestore = FirebaseFirestore.getInstance();
        feedUserArrayList = new ArrayList<FeedBack>();

        feedsAdapter = new FeedsAdapter(this, feedUserArrayList, firestore, getApplicationContext());
        binding.adminrecycler.setAdapter(feedsAdapter);
       RetrieveDataFirestore();


    }

    private void RetrieveDataFirestore() {

        firestore.collection("Feedback").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (binding.feedadminprogres.isShown()) {
                        binding.feedadminprogres.setVisibility(View.GONE);
                    }
                    Log.d("Fire Store Error", error.getMessage());
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        feedUserArrayList.add(documentChange.getDocument().toObject(FeedBack.class));
                       // Log.d("onEvent:", "onEvent: " + requestUserArrayList.get(0).getUserName());
                    }
                    feedsAdapter.notifyDataSetChanged();
                    if (binding.feedadminprogres.isShown()) {
                        binding.feedadminprogres.setVisibility(View.GONE);
                    }

                }
            }
        });


    }

    @Override
    public void onItemClick(Integer position) {

    }

    @Override
    public void onDeleteClick(Integer position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_adminlogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SigninActivity.class));
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}