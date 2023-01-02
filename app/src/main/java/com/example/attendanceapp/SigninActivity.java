package com.example.attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class SigninActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private FirebaseFirestore mfirebaseFirestore;

    private EditText emailEdt, passwordEdt;
    private ProgressBar log_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragment);

        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        log_progressBar = findViewById(R.id.log_progressBar);
    }

    public void goHome(View view) {
        closeKeyboard();
        log_progressBar.setVisibility(View.VISIBLE);
        mfirebaseFirestore = FirebaseFirestore.getInstance();


        String email = emailEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();

        if (email.isEmpty()) {
            log_progressBar.setVisibility(View.INVISIBLE);
            emailEdt.setError("Enter e-mail");
            emailEdt.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            log_progressBar.setVisibility(View.INVISIBLE);
            passwordEdt.setError("Enter e-mail");
            passwordEdt.setError("Enter your password");
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                        Toast.makeText(SigninActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SigninActivity.this, MainActivity.class));
                        finish();
//                    if (email.equalsIgnoreCase("admin@admin.com") && password.equalsIgnoreCase("admin123")) {
//                    }else {
//                        mfirebaseFirestore.collection("User").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if (task.isSuccessful()){
//                                    if (task.getResult().get("type").toString().equalsIgnoreCase("0")){
//                                        log_progressBar.setVisibility(View.INVISIBLE);
//                                        Toast.makeText(SigninActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
//                                        startActivity(new Intent(SigninActivity.this, UserMainActivity.class));
//                                        finish();
//                                    }else {
//                                        log_progressBar.setVisibility(View.INVISIBLE);
//                                        startActivity(new Intent(SignInActivity.this, InfluencerMainActivity.class));
//                                        finish();
//                                    }
//                                }
//                            }
//                        });
//
//                    }



                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SigninActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                log_progressBar.setVisibility(View.GONE);
            }
        });


    }



    public void returnRegister(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}