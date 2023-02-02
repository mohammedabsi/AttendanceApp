package com.example.attendanceapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.attendanceapp.adapter.CommentAdapter;
import com.example.attendanceapp.databinding.FragmentPostContentBinding;
import com.example.attendanceapp.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostContentFragment extends Fragment {

    private FragmentPostContentBinding binding ;




    LinearLayoutManager layoutManager ;

    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentArrayList;
    private FirebaseFirestore mFirebaseFirestore;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostContentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostContentFragment newInstance(String param1, String param2) {
        PostContentFragment fragment = new PostContentFragment();
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

        binding = FragmentPostContentBinding.inflate(inflater , container , false);
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        String x = getArguments().getString("postid");

        mFirebaseFirestore.collection("Post").document(x).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
           if (task.isSuccessful()){
               Picasso.get().load(task.getResult().getString("mImageurl")).into(binding.userPostImg);

               binding.userPostName.setText(task.getResult().getString("postname"));

               binding.userpostDesc.setText(task.getResult().getString("postdesc"));
           }
            }
        });
        binding.addComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
        initRecycler();
        RetrieveNewsData();
        return binding.getRoot();
    }

    private void initRecycler() {
        commentArrayList = new ArrayList<Comment>();
        layoutManager = new LinearLayoutManager(getActivity());
        binding.commentsRv.setLayoutManager(layoutManager);
        binding.commentsRv.setHasFixedSize(true);


        commentAdapter = new CommentAdapter(commentArrayList, getActivity().getApplicationContext());
        binding.commentsRv.setAdapter(commentAdapter);
    }

    private void RetrieveNewsData() {
        String x = getArguments().getString("postid");
        mFirebaseFirestore.collection("Post").document(x).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Log.d("fireStore Error", error.getMessage().toString());

                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        commentArrayList.add(documentChange.getDocument().
                                toObject(Comment.class));



                    }

                    commentAdapter.notifyDataSetChanged();


                }


            }
        });

    }

    private void addComment() {
        String comment = binding.commenttxt.getText().toString().trim();

        String x = getArguments().getString("postid");

        DocumentReference currentUser = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DocumentReference currentPost = FirebaseFirestore.getInstance().collection("Post").document(x);


        if (!comment.isEmpty()){

            currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        Comment comment1 = new Comment( task.getResult().getString("mUserName") ,comment , FirebaseAuth.getInstance().getCurrentUser().getEmail() );


                        currentPost.collection("Comments").document().set(comment1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getActivity(), R.string.comment_added, Toast.LENGTH_SHORT).show();

                                    binding.commenttxt.getText().clear();
                                }



                            }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Error !"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });




        }else {
            Toast.makeText(getActivity(), R.string.comment_empty, Toast.LENGTH_SHORT).show();
        }
    }
}