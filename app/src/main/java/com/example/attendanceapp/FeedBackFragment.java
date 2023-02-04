package com.example.attendanceapp;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.attendanceapp.adapter.FeedsAdapter;
import com.example.attendanceapp.databinding.FragmentFeedBackBinding;
import com.example.attendanceapp.model.FeedBack;
import com.example.attendanceapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedBackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedBackFragment extends Fragment implements RecyclerViewInterface {

    private FragmentFeedBackBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private FirebaseAuth mAuth;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Images");
    private StorageTask mUploadTask;

    ArrayList<FeedBack> feedUserArrayList;
    FeedsAdapter feedsAdapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedBackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedBackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedBackFragment newInstance(String param1, String param2) {
        FeedBackFragment fragment = new FeedBackFragment();
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
        binding = FragmentFeedBackBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        binding.feedbackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        binding.feedupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFeed();
            }
        });

        initRecycler();
        RetrieveDataFirestore();

        return binding.getRoot();
    }

   private void initRecycler(){

       binding.feedprogres.setVisibility(View.VISIBLE);
       binding.feedbackrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


       binding.feedbackrecycler.setHasFixedSize(true);

       firestore = FirebaseFirestore.getInstance();
       feedUserArrayList = new ArrayList<FeedBack>();

       feedsAdapter = new FeedsAdapter(this, feedUserArrayList, firestore, getActivity().getApplicationContext());
       binding.feedbackrecycler.setAdapter(feedsAdapter);
    }

    private void RetrieveDataFirestore() {

        firestore.collection("Feedback").whereEqualTo("feedemail",FirebaseAuth.getInstance().getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (binding.feedprogres.isShown()) {
                        binding.feedprogres.setVisibility(View.GONE);
                    }
                    Log.d("Fire Store Error", error.getMessage());
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        feedUserArrayList.add(documentChange.getDocument().toObject(FeedBack.class));
                    }
                    feedsAdapter.notifyDataSetChanged();
                    if (binding.feedprogres.isShown()) {
                        binding.feedprogres.setVisibility(View.GONE);
                    }

                }
            }
        });


    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadFeed() {


        binding.feedprogres.setVisibility(View.VISIBLE);
        binding.feedupload.setEnabled(false);
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

                            firestore.collection("User").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String uniqueID = UUID.randomUUID().toString();
                                                String imagePostUrl = uri.toString();
                                                FeedBack feedBack = new FeedBack(uniqueID,task.getResult().getString("mUserName"), task.getResult().getString("email"), imagePostUrl, "Pending");
                                                firestore.collection("Feedback")
                                                        .document(uniqueID)
                                                        .set(feedBack)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getActivity(), "Upload new Post success", Toast.LENGTH_SHORT).show();
                                                                binding.feedprogres.setVisibility(View.INVISIBLE);
                                                                binding.feedupload.setEnabled(true);

                                                                Picasso.get().load(R.drawable.ic_addimg).into(binding.feedbackImage);
                                                                binding.feedbackImage.setBackgroundResource(R.drawable.ic_addimg);


                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                binding.feedprogres.setVisibility(View.INVISIBLE);
                                                                binding.feedupload.setEnabled(true);


                                                            }
                                                        });

                                            }
                                        });

                                    }
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            binding.feedprogres.setVisibility(View.INVISIBLE);
                            binding.feedupload.setEnabled(true);
                        }
                    });


        } else {
            binding.feedprogres.setVisibility(View.INVISIBLE);
            binding.feedupload.setEnabled(true);

            Toast.makeText(getActivity(), "Please Choose image first", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(binding.feedbackImage);
        }

    }

    @Override
    public void onItemClick(Integer position) {

    }

    @Override
    public void onDeleteClick(Integer position) {

    }
}