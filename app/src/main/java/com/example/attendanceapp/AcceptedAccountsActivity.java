package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.attendanceapp.adapter.AcceptedRequestAdapter;
import com.example.attendanceapp.adapter.RequestsAdapter;
import com.example.attendanceapp.databinding.ActivityAcceptedAccountsBinding;
import com.example.attendanceapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AcceptedAccountsActivity extends AppCompatActivity implements RecyclerViewInterface {

    private ActivityAcceptedAccountsBinding binding;

    ArrayList<User> requestUserArrayList;
    AcceptedRequestAdapter acceptedRequestAdapter;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAcceptedAccountsBinding.inflate(getLayoutInflater());
       getSupportActionBar().setTitle("Department Head");
        setContentView(binding.getRoot());


        binding.AcceptedReqsprogressBar.setVisibility(View.VISIBLE);
        binding.acceptedaccountsreycler.setLayoutManager(new LinearLayoutManager(this));

        binding.acceptedaccountsreycler.setHasFixedSize(true);


        firestore = FirebaseFirestore.getInstance();
        requestUserArrayList = new ArrayList<User>();

        acceptedRequestAdapter = new AcceptedRequestAdapter(this, requestUserArrayList, firestore, getApplicationContext());
        binding.acceptedaccountsreycler.setAdapter(acceptedRequestAdapter);
        RetrieveDataFirestore();



    }
    private void RetrieveDataFirestore() {

        firestore.collection("User").whereEqualTo("type", 1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (binding.AcceptedReqsprogressBar.isShown()) {
                        binding.AcceptedReqsprogressBar.setVisibility(View.GONE);
                    }
                    Log.d("Fire Store Error", error.getMessage());
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        requestUserArrayList.add(documentChange.getDocument().toObject(User.class));
                        Log.d("onEvent:", "onEvent: " + requestUserArrayList.get(0).getUserName());
                    }
                    acceptedRequestAdapter.notifyDataSetChanged();
                    if (binding.AcceptedReqsprogressBar.isShown()) {
                        binding.AcceptedReqsprogressBar.setVisibility(View.GONE);
                    }

                }
            }
        });


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
            case R.id.navigation_news:

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(Integer position) {

    }

    @Override
    public void onDeleteClick(Integer position) {

    }

    @Override
    public void onItemLongClick(int position) {

//        Intent intent = new Intent(AcceptedAccountsActivity.this, RemoveCoursesAdminActivity.class);
//        intent.putExtra("message", requestUserArrayList.get(position).getId());
//        startActivity(intent);

    }
}