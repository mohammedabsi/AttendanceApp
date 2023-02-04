package com.example.attendanceapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.attendanceapp.adapter.RequestsAdapter;
import com.example.attendanceapp.model.User;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.attendanceapp.databinding.ActivityAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements RecyclerViewInterface {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAdminBinding binding;


    ArrayList<User> requestUserArrayList;
    RequestsAdapter requestsAdapter;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        binding.toolbar.setTitle("Department Head");
        setSupportActionBar(binding.toolbar);

        setContentView(binding.getRoot());

        binding.AcceptReqsprogressBar.setVisibility(View.VISIBLE);
        binding.acceptRequestsRecycler.setLayoutManager(new LinearLayoutManager(this));

        binding.acceptRequestsRecycler.setHasFixedSize(true);


        firestore = FirebaseFirestore.getInstance();
        requestUserArrayList = new ArrayList<User>();

        requestsAdapter = new RequestsAdapter(this, requestUserArrayList, firestore, getApplicationContext());
        binding.acceptRequestsRecycler.setAdapter(requestsAdapter);
        RetrieveDataFirestore();

        binding.acceptedTeachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this,AcceptedAccountsActivity.class));
            }
        });


        binding.adminfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, AdminFeedActivity.class));
            }
        });


    }

    private void RetrieveDataFirestore() {

        firestore.collection("User").whereEqualTo("type", 2).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        Log.d("onEvent:", "onEvent: " + requestUserArrayList.get(0).getUserName());
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
                startActivity(new Intent(this, SigninActivity.class));
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(Integer position) {

        Intent intent = new Intent(AdminActivity.this, AddCoursesActivity.class);
        intent.putExtra("message", requestUserArrayList.get(position).getId());
        startActivity(intent);

    }

    @Override
    public void onDeleteClick(Integer position) {

    }

    @Override
    public void onItemLongClick(int position) {



    }
}