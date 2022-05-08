package com.example.craftiloworld.Fragements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.craftiloworld.Adapter.PhotoAdapter;
import com.example.craftiloworld.EditProfileActivity;
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.Model.Users;
import com.example.craftiloworld.R;
import com.example.craftiloworld.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewSaved;
    private PhotoAdapter uploadAdapterSaved;
    private List<NewsUploads> myPhotoListSaved;

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<NewsUploads> myPhotoList;

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView fullName;
    private TextView description;
    private TextView following;

    private ImageView saveImages;
    private ImageView postImages;

    private Button editProfile;

    private FirebaseUser fUser;

    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageProfile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        fullName = view.findViewById(R.id.fullname);
        description = view.findViewById(R.id.bio);
        following = view.findViewById(R.id.following);
        saveImages = view.findViewById(R.id.my_saved_pictures);
        postImages = view.findViewById(R.id.my_pictures);
        editProfile = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recycle_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewSaved = view.findViewById(R.id.recycle_view_saved);
        recyclerViewSaved.setHasFixedSize(true);
        recyclerViewSaved.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoListSaved = new ArrayList<>();
        uploadAdapterSaved =new PhotoAdapter(getContext(), myPhotoListSaved);
        recyclerViewSaved.setAdapter(uploadAdapterSaved);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        profileId = fUser.getUid();

        userInfo();
        getFollowingCount();
        myPhotos();
        getSavedPost();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

            }
        });

        recyclerViewSaved.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        saveImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerViewSaved.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

            }
        });

        postImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerViewSaved.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }
        });

        return view;
    }

    private void getSavedPost() {

        final List<String> savedIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    savedIds.add(dataSnapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("NewsUploads").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myPhotoListSaved.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            NewsUploads uploads = dataSnapshot.getValue(NewsUploads.class);

                            for (String id : savedIds) {
                                if (uploads.getPostId().equals(id)) {
                                    myPhotoListSaved.add(uploads);
                                }
                            }
                        }

                        uploadAdapterSaved.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void myPhotos() {

        FirebaseDatabase.getInstance().getReference().child("NewsUploads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    NewsUploads uploads = dataSnapshot.getValue(NewsUploads.class);

                    if (uploads.getReporterId() != null) {
                        if (uploads.getReporterId().equals(profileId)) {
                            myPhotoList.add(uploads);
                        }
                    }
                }

                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowingCount() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                assert users != null;
                if (users.getImageurl().equals("default")){
                    imageProfile.setImageResource(R.drawable.ic_profile_circle);
                }else {
                    Picasso.get().load(users.getImageurl()).into(imageProfile);
                }
                fullName.setText(users.getName());

                description.setText(users.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}