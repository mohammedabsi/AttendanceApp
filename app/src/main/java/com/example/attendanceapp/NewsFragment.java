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

import com.example.attendanceapp.adapter.PostsAdaptar;
import com.example.attendanceapp.databinding.FragmentNewsBinding;
import com.example.attendanceapp.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements RecyclerViewInterface {

    private FragmentNewsBinding binding;
    private PostsAdaptar postsAdaptar;
    private ArrayList<Post> postsArrayList;
    ;
    LinearLayoutManager layoutManager;

    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
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
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        initRecycler();
        RetrieveNewsData();

        return binding.getRoot();
    }

    private void initRecycler() {
        postsArrayList = new ArrayList<Post>();
        layoutManager = new LinearLayoutManager(getActivity());
        binding.postrecycler.setLayoutManager(layoutManager);
        binding.postrecycler.setHasFixedSize(true);
        postsAdaptar = new PostsAdaptar(postsArrayList, getActivity(), this);
        binding.postrecycler.setAdapter(postsAdaptar);
    }

    public void RetrieveNewsData() {
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            FirebaseFirestore.getInstance()
                                    .collection("Post")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {

                                                Log.d("fireStore Error", error.getMessage().toString());

                                                return;
                                            }
                                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                                    Post post = documentChange.getDocument().toObject(Post.class);
                                                    postsArrayList.add(post);

                                                    Log.d("asTAG", "onEvent: " + documentChange.getDocument().getId());


                                                }

                                                postsAdaptar.notifyDataSetChanged();


                                            }


                                        }
                                    });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    public void onItemClick(Integer position) {
        String x = postsArrayList.get(position).getCourseid();

        Bundle bundle = new Bundle();
        bundle.putString("postid", x);

        PostContentFragment fragobj = new PostContentFragment();
        fragobj.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.container,
                fragobj).addToBackStack(null).commit();

    }

    @Override
    public void onDeleteClick(Integer position) {
        postsArrayList.remove(position);
        postsAdaptar.notifyItemRemoved(position);

    }

    @Override
    public void onItemLongClick(int position) {

    }
}