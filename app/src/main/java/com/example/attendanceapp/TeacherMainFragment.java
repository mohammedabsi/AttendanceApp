package com.example.attendanceapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.attendanceapp.databinding.FragmentTeacherMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherMainFragment extends Fragment {

    FragmentTeacherMainBinding binding;
    FirebaseAuth mAuth ;
    Date d = new Date();
    SimpleDateFormat enDate = new SimpleDateFormat("MMMM d, yyyy ", new Locale("en"));
    String en = enDate.format(d);
    CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
    private  FirebaseFirestore firestore;

    BottomSheetDialog dialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TeacherMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherMainFragment newInstance(String param1, String param2) {
        TeacherMainFragment fragment = new TeacherMainFragment();
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
        binding = FragmentTeacherMainBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();





        binding.scantea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        dialog = new BottomSheetDialog(getActivity());
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();

            }


        });

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return binding.getRoot();
    }

    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_teacher, null, false);

        Button add = view.findViewById(R.id.add_sub);
        EditText subEdit = view.findViewById(R.id.subedit);

        Attendance attendance = new Attendance("null", en);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ss= subEdit.getText().toString();
                User user2 = new User(Collections.singletonList(ss));
//dialog.dismiss();
                if (!ss.isEmpty()){

                    firestore.collection("User").document(mAuth.getCurrentUser().getUid()).collection(ss).document(ss).set(attendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getActivity(), "Collection Added", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
                    firestore.collection("User").document(mAuth.getCurrentUser().getUid()).update("subs", FieldValue.arrayUnion(ss));



                }

            }
        });

        dialog.setContentView(view);
        dialog.show();

    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlauncher.launch(options);

    }

    public void RetrieveNewsData2() {

        DocumentReference deliveredeDoc = firestore.collection("User").document(mAuth.getCurrentUser().getUid());


        deliveredeDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            }
        });


    }

    ActivityResultLauncher<ScanOptions> barlauncher = registerForActivityResult(new ScanContract(), result ->
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (result.getContents()!= null){

            builder.setTitle(result.getContents());
            Attendance attendance2 = new Attendance(result.getContents(),en);
            firestore.collection("User").document(mAuth.getCurrentUser().getUid()).collection("sub1").document("sub1").update(en,FieldValue.arrayUnion(attendance2)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()){
                   Toast.makeText(getActivity(), "student Added", Toast.LENGTH_SHORT).show();
               }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            builder.setTitle("No user been Added");
        }

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    });


}