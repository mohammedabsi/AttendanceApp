package com.example.attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SigninActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private FirebaseFirestore mfirebaseFirestore;

    private EditText emailEdt, passwordEdt;
    private LinearLayout linear ;
    private ProgressBar log_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragment);

        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        emailEdt = findViewById(R.id.emailEdt);
        linear = findViewById(R.id.linear);
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
                    if (email.equalsIgnoreCase("admin@admin.com") && password.equalsIgnoreCase("admin123")) {
                        Snackbar.make(linear, "Register Success ", Snackbar.LENGTH_LONG)
                                .show();
                        startActivity(new Intent(SigninActivity.this, AdminWelcomeActivity.class));
                        finish();
                    }else {
mfirebaseFirestore.collection("User").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()){
            if (task.getResult().get("type").toString().equalsIgnoreCase("2")){
                Snackbar.make(linear, "Your Account is not verified yet , wait till Verification", Snackbar.LENGTH_LONG)
                        .show();
                log_progressBar.setVisibility(View.INVISIBLE);

            }else {
                Snackbar.make(linear, "Register Success ", Snackbar.LENGTH_LONG)
                        .show();
                startActivity(new Intent(SigninActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        Snackbar.make(linear, e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                .show();
        log_progressBar.setVisibility(View.GONE);
    }
});



                    }



                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(linear, e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                        .show();
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