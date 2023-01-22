package com.example.attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;


public class SignupActivity extends AppCompatActivity {

    private EditText confirmPasswordEdt, userNameEdt, emailEdt, passwordEdt, phone;
    private ProgressBar reg_progressBar;
    private EditText stdid;
    private LinearLayout reglinear ;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private RadioButton std_RadioButton, tch_RadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_fragment);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        userNameEdt = findViewById(R.id.userNameEdt);
        emailEdt = findViewById(R.id.emailEdt);
        passwordEdt = findViewById(R.id.passwordreg);
        reglinear = findViewById(R.id.reglinear);
        confirmPasswordEdt = findViewById(R.id.confirmPasswordEdt);
        phone = findViewById(R.id.phone);
        reg_progressBar = findViewById(R.id.reg_progressBar);
        stdid = findViewById(R.id.studid);

        std_RadioButton = findViewById(R.id.std_RadioButton);
        tch_RadioButton = findViewById(R.id.tch_RadioButton);

 std_RadioButton.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
        stdid.setVisibility(View.VISIBLE);
     }
 });
        tch_RadioButton.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
        stdid.setVisibility(View.INVISIBLE);
     }
 });


    }

    public void returnLogin(View view) {
        Intent intent = new Intent(this, SigninActivity.class);
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

    public void register2() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


    public void Register(View view) {
        closeKeyboard();
        String user_name = userNameEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().toLowerCase().trim();
        String password = passwordEdt.getText().toString().trim();
        String confirm_password = confirmPasswordEdt.getText().toString().trim();


        String phones = phone.getText().toString().trim();
        String studentid = stdid.getText().toString().trim();

        boolean tchrad = tch_RadioButton.isChecked();
        boolean stdrad = std_RadioButton.isChecked();


        reg_progressBar.setVisibility(View.VISIBLE);


        if (!user_name.isEmpty()) {
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!password.isEmpty() && password.length() > 6) {
                    if (!confirm_password.isEmpty()) {
                        if (password.equalsIgnoreCase(confirm_password)) {
                            if (stdrad == true || tchrad == true) {
                                // User user = new User(user_name, email, password, phones, uuid.toString());


                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            if (stdrad) {
                                                User user = new User(user_name, email, password, phones, studentid, 0, mAuth.getCurrentUser().getUid(),null);
                                                firestore.collection("User").document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            Snackbar.make(reglinear, "Register Success ", Snackbar.LENGTH_LONG)
                                                                    .show();
                                                            reg_progressBar.setVisibility(View.INVISIBLE);
                                                            register2();

                                                        } else {
                                                            Snackbar.make(reglinear, "Register Failed !!", Snackbar.LENGTH_LONG)
                                                                    .show();
                                                            reg_progressBar.setVisibility(View.INVISIBLE);

                                                        }
                                                    }
                                                });

                                            } else if (tchrad) {
                                                User user = new User(user_name, email, password, phones, "null", 2, mAuth.getCurrentUser().getUid() , null);
                                                firestore.collection("User").document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

//                                                            Toast.makeText(SignupActivity.this, "Register success ", Toast.LENGTH_SHORT).show();
//                                                            register2();
                                                            reg_progressBar.setVisibility(View.INVISIBLE);

                                                            Snackbar.make(reglinear, "Register Success, please wait till Verification", Snackbar.LENGTH_LONG)
                                                                    .show();

                                                        } else {
                                                            Snackbar.make(reglinear, "Register Failed !!", Snackbar.LENGTH_LONG)
                                                                    .show();
                                                            reg_progressBar.setVisibility(View.INVISIBLE);

                                                        }
                                                    }
                                                });
                                            }


                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(this, "Choose account type", Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            confirmPasswordEdt.setError("Passwords Doesnt match , retry again  !!");
                            confirmPasswordEdt.requestFocus();
                            reg_progressBar.setVisibility(View.INVISIBLE);


                        }
                    } else {
                        confirmPasswordEdt.setError("Re type your password correctly !!");
                        confirmPasswordEdt.requestFocus();
                        reg_progressBar.setVisibility(View.INVISIBLE);

                    }

                } else {
                    passwordEdt.setError("The password should contain more than 6 symbols");
                    passwordEdt.requestFocus();
                    reg_progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

            } else {
                emailEdt.setError("Please provide valid email");
                emailEdt.requestFocus();
                reg_progressBar.setVisibility(View.INVISIBLE);
                return;
            }
        } else {
            userNameEdt.setError("Empty Field");
            userNameEdt.requestFocus();
            reg_progressBar.setVisibility(View.INVISIBLE);
            return;
        }


    }
}