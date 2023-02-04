package com.example.attendanceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.attendanceapp.adapter.CommentAdapter;
import com.example.attendanceapp.adapter.NewStudentAdapter;
import com.example.attendanceapp.databinding.ActivityAddcoursesBinding;
import com.example.attendanceapp.model.Attendance;
import com.example.attendanceapp.model.Comment;
import com.example.attendanceapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddCoursesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityAddcoursesBinding binding;
    FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    LinearLayoutManager layoutManager;

    private NewStudentAdapter newStudentAdapterAdapter;
    private ArrayList<User> studentArrayList;

    Date d = new Date();
    SimpleDateFormat enDate = new SimpleDateFormat("MMMM d, yyyy ", new Locale("en"));
    String en = enDate.format(d);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddcoursesBinding.inflate(getLayoutInflater());
        getSupportActionBar().setTitle("Department Head");
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        String id = getIntent().getStringExtra("message");


        binding.addSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSubject();
            }
        });

        binding.addStd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudent();
            }
        });


        firestore.collection("User").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    List<String> group = (List<String>) document.get("subs");


                    if (!group.isEmpty()) {
                        ArrayAdapter ad = new ArrayAdapter(AddCoursesActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                group);
                        ad.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

                        binding.courseadminspinner.setAdapter(ad);
                        Log.d("ssTAG", "onComplete: " + group + "\n" + binding.courseadminspinner.getSelectedItem().toString());
                        binding.courseadminspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                Log.d("ssssTAG", "onComplete: " + "\n" + adapterView.getItemAtPosition(i).toString());
                                initRecycler();
                                RetrieveNewsData();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }

                    if (!group.isEmpty()){


                    }




                }
            }
        });





    }

    private void initRecycler() {
        studentArrayList = new ArrayList<User>();
        layoutManager = new LinearLayoutManager(this);
        binding.stdcourseadminrecycler.setLayoutManager(layoutManager);
        binding.stdcourseadminrecycler.setHasFixedSize(true);


        newStudentAdapterAdapter = new NewStudentAdapter(studentArrayList, getApplicationContext());
        binding.stdcourseadminrecycler.setAdapter(newStudentAdapterAdapter);
    }

    private void RetrieveNewsData() {
        String id = getIntent().getStringExtra("message");

        if (binding.courseadminspinner != null && binding.courseadminspinner.getSelectedItem() != null) {
            firestore.collection("User")
                    .document(id)
                    .collection(binding.courseadminspinner.getSelectedItem().toString())
                    .document(binding.courseadminspinner.getSelectedItem().toString())
                    .collection("Students")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {

                                Log.d("fireStore Error", error.getMessage().toString());

                                return;
                            }
                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                    studentArrayList.add(documentChange.getDocument().
                                            toObject(User.class));


                                }

                                newStudentAdapterAdapter.notifyDataSetChanged();


                            }


                        }
                    });
        } else {
            firestore.collection("User").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> group = (List<String>) task.getResult().get("subs");

                        firestore.collection("User")
                                .document(id)
                                .collection(binding.courseadminspinner.getSelectedItem().toString())
                                .document(binding.courseadminspinner.getSelectedItem().toString())
                                .collection("Students")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {

                                            Log.d("fireStore Error", error.getMessage().toString());

                                            return;
                                        }
                                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                                            if (documentChange.getType() == DocumentChange.Type.ADDED) {

                                                studentArrayList.add(documentChange.getDocument().
                                                        toObject(User.class));


                                            }

                                            newStudentAdapterAdapter.notifyDataSetChanged();


                                        }


                                    }
                                });

                    }
                }
            });
        }


    }


    private void addSubject() {
        String ss = binding.subedit.getText().toString().trim();
        String id = getIntent().getStringExtra("message");


        Attendance attendance = new Attendance("null", en);

        if (!ss.isEmpty()) {

            firestore.collection("User").document(id).collection(ss).document(ss).set(attendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firestore.collection("User").document(id).update("subs", FieldValue.arrayUnion(ss));
                        Toast.makeText(AddCoursesActivity.this, "Collection Added", Toast.LENGTH_SHORT).show();
                        binding.subedit.getText().clear();

                    }
                }
            });
            firestore.collection("User").document(mAuth.getCurrentUser().getUid()).update("subs", FieldValue.arrayUnion(ss));


        }
    }

    private void addStudent() {
        String stdemail = binding.stdedit.getText().toString().trim();
        String id = getIntent().getStringExtra("message");

        binding.addStd.setEnabled(false);


        firestore.collection("User").whereEqualTo("email", stdemail).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Log.d("fireStore Error", error.getMessage().toString());

                    return;
                }
                if (value.getDocumentChanges().get(0).getDocument().getString("id") != null) {

                    User user = new User(value.getDocumentChanges().get(0).getDocument().getString("mUserName"), stdemail, value.getDocumentChanges().get(0).getDocument().getString("id"));
                    firestore
                            .collection("User")
                            .document(id)
                            .collection(binding.courseadminspinner.getSelectedItem().toString())
                            .document(binding.courseadminspinner.getSelectedItem().toString())
                            .collection("Students")
                            .document()
                            .set(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(AddCoursesActivity.this, "Student add to " + binding.courseadminspinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                                    binding.addStd.setEnabled(true);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddCoursesActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    binding.addStd.setEnabled(true);
                                }
                            });
                } else {

                    Toast.makeText(AddCoursesActivity.this, "This student is not exict", Toast.LENGTH_SHORT).show();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}