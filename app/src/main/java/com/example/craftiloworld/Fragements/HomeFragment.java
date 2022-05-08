package com.example.craftiloworld.Fragements;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.craftiloworld.Adapter.PostAdapter;
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ImageView search;

    private RecyclerView recyclerViewPost;
    private PostAdapter postAdapter;
    private List<NewsUploads> uploadsList;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        search = view.findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.replace(R.id.fragment_container, new SearchFragment());
                t.commit();
            }
        });

        recyclerViewPost = view.findViewById(R.id.recycler_view_post);
        recyclerViewPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPost.setLayoutManager(linearLayoutManager);
        uploadsList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), uploadsList);
        recyclerViewPost.setAdapter(postAdapter);
        followingList = new ArrayList<>();

        checkFollowingUUsers();

        return view;
    }

    private void checkFollowingUUsers() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    followingList.add(dataSnapshot.getKey());
                }
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                readPost();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readPost() {

        FirebaseDatabase.getInstance().getReference().child("NewsUploads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadsList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NewsUploads uploads = dataSnapshot.getValue(NewsUploads.class);

                    for (String id : followingList) {
                        if (uploads.getGroupId().equals(id)) {
                            uploadsList.add(uploads);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}