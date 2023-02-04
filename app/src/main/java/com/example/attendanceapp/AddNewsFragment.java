package com.example.attendanceapp;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.attendanceapp.databinding.FragmentAddNewsBinding;
import com.example.attendanceapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewsFragment extends Fragment {


    private FragmentAddNewsBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private FirebaseAuth mAuth;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Images");
    private StorageTask mUploadTask;
    String[] arrayList = {"sub1", "sub2", "sub3"};


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddNewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNewsFragment newInstance(String param1, String param2) {
        AddNewsFragment fragment = new AddNewsFragment();
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

        binding = FragmentAddNewsBinding.inflate(inflater, container, false);

        binding.iconaddimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        binding.createpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

//        firestore.collection("User").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//
//
//
//                    DocumentSnapshot document = task.getResult();
//                    List<String> group = (List<String>) document.get("subs");
//
//                    ArrayAdapter ad = new ArrayAdapter(getActivity(),
//                            android.R.layout.simple_spinner_dropdown_item,
//                            group);
//
//                   // binding.coursesspinner.setAdapter(ad);
//
//                }
//            }
//        });

        mAuth = FirebaseAuth.getInstance();



        return binding.getRoot();

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(binding.iconaddimg);
        }

    }

    private void uploadImage() {

        String college = getActivity().getIntent().getStringExtra("colTags");
        String club = getActivity().getIntent().getStringExtra("clubTags");
        binding.progress.setVisibility(View.VISIBLE);
        binding.createpost.setEnabled(false);
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(mImageUri.getLastPathSegment());

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                }
                            }, 500);
                            Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String uniqueID = UUID.randomUUID().toString();
                                    String imagePostUrl = uri.toString();
                                    Post post = new Post(binding.postName.getText().toString(), binding.postDesc.getText().toString(), imagePostUrl , uniqueID,FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    firestore.collection("Post")
                                            .document(uniqueID)
                                            .set(post)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getActivity(), "Upload new Post success", Toast.LENGTH_SHORT).show();
                                                    binding.progress.setVisibility(View.INVISIBLE);
                                                    binding.createpost.setEnabled(true);

                                                    Picasso.get().load(R.drawable.ic_addimg).into(binding.iconaddimg);
                                                    binding.iconaddimg.setBackgroundResource(R.drawable.ic_addimg);
                                                    binding.postName.getText().clear();
                                                    binding.postDesc.getText().clear();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    binding.progress.setVisibility(View.INVISIBLE);
                                                    binding.createpost.setEnabled(true);


                                                }
                                            });

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            binding.progress.setVisibility(View.INVISIBLE);
                            binding.createpost.setEnabled(true);
                        }
                    });


        } else {
            binding.progress.setVisibility(View.INVISIBLE);
            binding.createpost.setEnabled(true);

            Toast.makeText(getActivity(), "Please Choose image first", Toast.LENGTH_SHORT).show();
        }

    }

//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//        firestore.collection("User").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//
//                    DocumentSnapshot document = task.getResult();
//                    List<String> group = (List<String>) document.get("subs");
//
//                    Toast.makeText(getActivity(), ""+group.get(i), Toast.LENGTH_SHORT).show();
//
//
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }
}