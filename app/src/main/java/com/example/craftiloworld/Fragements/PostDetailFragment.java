package com.example.craftiloworld.Fragements;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.craftiloworld.Adapter.PostDetailAdapter;
import com.example.craftiloworld.Model.Groups;
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {

    private String postId;
    private String groupid;

    private RecyclerView recyclerView;
    private TextView groupName;
    private ImageView groupProfileImage;
    private ImageView close;

    private PostDetailAdapter postDetailAdapter;
    private List<NewsUploads> uploadsLists;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        postId = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postid", "none");
        groupid = getContext().getSharedPreferences("GROUP", Context.MODE_PRIVATE).getString("groupID", "none");

        groupName = view.findViewById(R.id.group_name);
        groupProfileImage = view.findViewById(R.id.group_image_profile);
        close = view.findViewById(R.id.close);

        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        uploadsLists = new ArrayList<>();
        postDetailAdapter = new PostDetailAdapter(getContext(), uploadsLists);
        recyclerView.setAdapter(postDetailAdapter);

        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Groups groups = snapshot.getValue(Groups.class);

                groupName.setText(groups.getGroupname());
                if (groups.getImageurl().equals("default")) {
                    groupProfileImage.setImageResource(R.drawable.ic_group);
                } else {
                    Picasso.get().load(groups.getImageurl()).into(groupProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("NewsUploads").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadsLists.clear();
                uploadsLists.add(snapshot.getValue(NewsUploads.class));

                postDetailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.replace(R.id.fragment_container, new HomeFragment());
                t.commit();
            }
        });

        groupProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().getSharedPreferences("GROUP", Context.MODE_PRIVATE).edit().putString("groupID", groupid).apply();
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupProfileFragment()).commit();
            }
        });

        groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().getSharedPreferences("GROUP", Context.MODE_PRIVATE).edit().putString("groupID", groupid).apply();
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupProfileFragment()).commit();
            }
        });

        return view;
    }
}