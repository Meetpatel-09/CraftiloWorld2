package com.example.craftiloworld.Fragements;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.craftiloworld.Adapter.PhotoAdapter;
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.Model.Users;
import com.example.craftiloworld.R;
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

public class UserProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<NewsUploads> myPhotoList;

    private CircleImageView imageProfile;
    private TextView fullName;
    private TextView description;
  //  private TextView following;

    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        imageProfile = view.findViewById(R.id.image_profile);

        fullName = view.findViewById(R.id.fullname);
        description = view.findViewById(R.id.userbio);
   //     following = view.findViewById(R.id.following);

        recyclerView = view.findViewById(R.id.recycle_uploads);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileID", "none");

        profileId = data;

        userInfo();
//        getFollowingCount();
        myPhotos();


        return view;
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
  /*
    private void getFollowingCount() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


   */
    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

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