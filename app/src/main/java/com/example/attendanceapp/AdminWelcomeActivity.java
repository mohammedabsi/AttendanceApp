package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.attendanceapp.databinding.ActivityAdminWelcome2Binding;
import com.google.firebase.auth.FirebaseAuth;

public class AdminWelcomeActivity extends AppCompatActivity {

    private ActivityAdminWelcome2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminWelcome2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        binding.reqTeachersLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminWelcomeActivity.this, AdminActivity.class));
            }
        });
        binding.reqExcusesLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminWelcomeActivity.this, AdminFeedActivity.class));
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


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}