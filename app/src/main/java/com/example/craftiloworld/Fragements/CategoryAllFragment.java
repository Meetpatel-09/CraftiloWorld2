package com.example.craftiloworld.Fragements;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class CategoryAllFragment extends Fragment {

    private TextView categoryName;

    private RecyclerView recyclerViewPost;
    private PostAdapter postAdapter;
    private List<NewsUploads> uploadsList;

    private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_all, container, false);

        String data = getContext().getSharedPreferences("CATEGORY", Context.MODE_PRIVATE).getString("categoryName", null);
        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();

        categoryName = view.findViewById(R.id.category_name);

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

        categoryName.setText(data);

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
                            String data = getContext().getSharedPreferences("CATEGORY", Context.MODE_PRIVATE).getString("categoryName", null);;
                            if (uploads.getCategory().equals(data))
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