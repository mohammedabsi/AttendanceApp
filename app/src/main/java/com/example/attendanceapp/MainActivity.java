package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView infbottomNavigationView;
    private Fragment selectedFragment = null;
    private TextView welcome_tv;
    private ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        logout = findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SigninActivity.class));
                finish();
            }
        });
        infbottomNavigationView = findViewById(R.id.bottomNavigationView);
        infbottomNavigationView.setBackground(null);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    new MainFragment()).commit();
        }
        infbottomNavigationView.setOnNavigationItemSelectedListener(navListener);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.navigation_home:
                            selectedFragment = new MainFragment();
                            break;
                        case R.id.navigation_news:
                            selectedFragment = new NewsFragment();

                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();

                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.container,
                            selectedFragment).commit();

                    return true;
                }
            };

}