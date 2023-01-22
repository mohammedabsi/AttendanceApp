package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView infbottomNavigationView;
    private Fragment selectedFragment = null;
    private TextView welcome_tv;
//    private ImageView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        infbottomNavigationView = findViewById(R.id.bottomNavigationView);
        infbottomNavigationView.setBackground(null);

        if (savedInstanceState == null) {
            FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().get("type").toString().equalsIgnoreCase("0")) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    new StudentMainFragment()).commit();
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                    new TeacherMainFragment()).commit();
                        }
                    }
                }
            });


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

                            FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().get("type").toString().equalsIgnoreCase("0")) {
                                            selectedFragment = new StudentMainFragment();
                                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                                    selectedFragment).commit();
                                        } else {
                                            selectedFragment = new TeacherMainFragment();
                                            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                                                    selectedFragment).commit();
                                        }
                                    }

                                }

                            });
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