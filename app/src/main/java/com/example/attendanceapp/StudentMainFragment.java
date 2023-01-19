package com.example.attendanceapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.attendanceapp.databinding.FragmentStudentMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentMainFragment extends Fragment {

    FragmentStudentMainBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StudentMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentMainFragment newInstance(String param1, String param2) {
        StudentMainFragment fragment = new StudentMainFragment();
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
        binding = FragmentStudentMainBinding.inflate(inflater, container, false);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        binding.stdmmainprg.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    binding.stdtxt.setText(task.getResult().getString("userName"));

                    try {
                        //BitMatrix class to encode entered text and set Width & Height
                        BitMatrix mMatrix = multiFormatWriter.encode(task.getResult().getString("userName")+"\n" +task.getResult().getString("email"), BarcodeFormat.QR_CODE, 400, 400);
                        BarcodeEncoder mEncoder = new BarcodeEncoder();
                        Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
                        binding.stdimageCode.setImageBitmap(mBitmap);//Setting generated QR code to imageView
                        // to hide the keyboard
                        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(binding.stdtxt.getApplicationWindowToken(), 0);

                        binding.stdmmainprg.setVisibility(View.INVISIBLE);

                    } catch (WriterException e) {
                        e.printStackTrace();
                        binding.stdmmainprg.setVisibility(View.INVISIBLE);

                    }
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                binding.stdmmainprg.setVisibility(View.INVISIBLE);

            }
        });


        return binding.getRoot();
    }
}