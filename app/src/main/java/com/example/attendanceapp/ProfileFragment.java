package com.example.attendanceapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String currentid = user.getUid();
    DocumentReference userprofilenode = FirebaseFirestore.getInstance().collection("User").document(currentid);

    private Dialog dialog;


    private DocumentReference reference1 = firestore.collection("User").document(user.getUid());

    private ConstraintLayout logout , edtpass;

    TextView usernamepro, emailpro, phonepro ,accounttypepro;
    TextInputLayout accounttypeprolayout ;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        usernamepro = v.findViewById(R.id.usernamepro);
        emailpro = v.findViewById(R.id.emailpro);
        phonepro = v.findViewById(R.id.phonepro);
        accounttypepro = v.findViewById(R.id.accounttypepro);
        logout = v.findViewById(R.id.logout);
        edtpass = v.findViewById(R.id.edtpass);
        accounttypeprolayout = v.findViewById(R.id.accounttypeprolayout);

        dialog = new Dialog(getActivity());
        UpdatePass();
//     //   final Snackbar snackbar = Snackbar
//                .make(drawerLayout, "Coming On next Version", Snackbar.LENGTH_SHORT).setTextColor(getResources().getColor(R.color.secondry_color)).setBackgroundTint(getResources().getColor(R.color.secondry_color));
        edtpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SigninActivity.class));
                getActivity().finish();
            }
        });

        reference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {


                    usernamepro.setText(task.getResult().getString("mUserName"));
                    emailpro.setText(task.getResult().getString("email"));
                    phonepro.setText(task.getResult().getString("phone"));

                    if (task.getResult().get("type").toString().equalsIgnoreCase("0")){

                        accounttypepro.setText(task.getResult().getString("stdid"));
                        accounttypeprolayout.setHelperText("Student");
                    }else{
                        accounttypepro.setText("Teacher");
                       // accounttypeprolayout.setHelperText("Teacher");
                    }


                }
            }
        });


        return v;
    }

    private void UpdatePass(){
        dialog.setContentView(R.layout.update_password_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.white_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button updatePassBtn = dialog.findViewById(R.id.updatePassBtn);
        Button cancelUpdate = dialog.findViewById(R.id.cancelUpdate);
        EditText oldPass = dialog.findViewById(R.id.oldPass);
        EditText newPass = dialog.findViewById(R.id.newPass);

        updatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Wait A Second ", Toast.LENGTH_SHORT).show();
                String email = user.getEmail();
                String old_Pass = oldPass.getText().toString().trim();
                String new_Pass = newPass.getText().toString().trim();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, old_Pass);



                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(new_Pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                userprofilenode.update("password",new_Pass);
                                                oldPass.getText().clear();
                                                newPass.getText().clear();
                                                Toast.makeText(getActivity(), "Password Changed ", Toast.LENGTH_SHORT).show();
                                                Log.d("pas", "Password updated");
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(getActivity(), "Error password not Changed", Toast.LENGTH_SHORT).show();
                                                Log.d("pas", "Error password not updated");
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "Error auth failed", Toast.LENGTH_SHORT).show();
                                    Log.d("pas", "Error auth failed");
                                    dialog.dismiss();

                                }
                            }
                        });

            }
        });
        cancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }
}