package com.example.attendanceapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceapp.model.Post;
import com.example.attendanceapp.R;
import com.example.attendanceapp.RecyclerViewInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostsAdaptar extends RecyclerView.Adapter<PostsAdaptar.MainViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private ArrayList<Post> mainList;
    private Context mContext;

    public PostsAdaptar(ArrayList<Post> mainList, Context mContext, RecyclerViewInterface recyclerViewInterface) {
        this.mainList = mainList;
        this.mContext = mContext;
        this.recyclerViewInterface = recyclerViewInterface;
    }



    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);


        return new MainViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Post post = mainList.get(position);
        holder.postname.setText(post.getPostname());
        holder.postdesc.setText(post.getPostdesc());

        String imgUrl = post.getmImageurl();
        Picasso.get().load(imgUrl).into(holder.postimg);


    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    public void filterList(ArrayList<Post> filteredList) {
        mainList = filteredList;
        notifyDataSetChanged();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        TextView postname , postdesc;
        ImageView postimg;


        public MainViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            postname = itemView.findViewById(R.id.postname);
            postdesc = itemView.findViewById(R.id.postdesc);
            postimg = itemView.findViewById(R.id.postimg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
