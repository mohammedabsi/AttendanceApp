package com.example.attendanceapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapp.databinding.ActivityAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminBinding binding;

//    private RecyclerView accept_requests_recycler;
//    ProgressBar AcceptReqsprogressBar;
    ArrayList<User> requestUserArrayList;
    RequestsAdapter requestsAdapter;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());

        binding.AcceptReqsprogressBar.setVisibility(View.VISIBLE);
        binding.acceptRequestsRecycler.setLayoutManager(new LinearLayoutManager(this));

        binding.acceptRequestsRecycler.setHasFixedSize(true);


        firestore = FirebaseFirestore.getInstance();
        requestUserArrayList = new ArrayList<User>();

        requestsAdapter = new RequestsAdapter(this, requestUserArrayList, firestore);
        binding.acceptRequestsRecycler.setAdapter(requestsAdapter);
        RetrieveDataFirestore();




        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




    }

    private void RetrieveDataFirestore() {

        firestore.collection("User").whereNotEqualTo("type", 0).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (binding.AcceptReqsprogressBar.isShown()) {
                        binding.AcceptReqsprogressBar.setVisibility(View.GONE);
                    }
                    Log.d("Fire Store Error", error.getMessage());
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        requestUserArrayList.add(documentChange.getDocument().toObject(User.class));
                        Log.d("onEvent:", "onEvent: "+requestUserArrayList.get(0).getUserName());
                    }
                    requestsAdapter.notifyDataSetChanged();
                    if (binding.AcceptReqsprogressBar.isShown()) {
                        binding.AcceptReqsprogressBar.setVisibility(View.GONE);
                    }

                }
            }
        });



    }

    @Override
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
                startActivity(new Intent(this,SigninActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}