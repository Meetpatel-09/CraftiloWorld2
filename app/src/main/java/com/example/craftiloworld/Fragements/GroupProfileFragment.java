package com.example.craftiloworld.Fragements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.craftiloworld.Adapter.PhotoAdapter;
import com.example.craftiloworld.EditGroupProfileActivity;
import com.example.craftiloworld.Model.Groups;
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.R;
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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<NewsUploads> myPhotoList;

    private CircleImageView imageProfile;

    private TextView follower;
    private TextView post;
    private TextView reporter;

    private TextView groupName;
    private TextView description;

    private Button editProfile;
    private Button manage;

    private FirebaseUser fUser;

    String groupId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_profile, container, false);

        imageProfile = view.findViewById(R.id.image_profile);
        post = view.findViewById(R.id.posts);
        follower  = view.findViewById(R.id.followers);
        reporter  = view.findViewById(R.id.reporters);
        groupName = view.findViewById(R.id.group_name);
        description = view.findViewById(R.id.description);
        editProfile = view.findViewById(R.id.edit);
        manage = view.findViewById(R.id.mange);

        recyclerView = view.findViewById(R.id.uploaded_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        myPhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recyclerView.setAdapter(photoAdapter);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("GROUP", Context.MODE_PRIVATE).getString("groupID", null);

        if (data.equals("none")) {
            groupId = fUser.getUid();
        } else {
            groupId = data;
        }

        groupInfo();
        getFollowerCount();
        getReporterCount();
        getNewsCount();
        myPhotos();

        if (groupId.equals(fUser.getUid())) {
            editProfile.setText("Edit");
            manage.setText("Manage");
        } else {
            checkFollowingStatus(groupId, editProfile);
            checkIfCrew();
        }

        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btText = manage.getText().toString();

                if (btText.equals("Manage")) {
                    FragmentTransaction t = getFragmentManager().beginTransaction();
                    t.replace(R.id.fragment_container, new ManageGroupFragment());
                    t.commit();
                } else {
                    if (btText.equals("Apply")){

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Request");

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("CrewId", fUser.getUid());
                        map.put("GroupId", groupId);

                        ref.child(fUser.getUid()).setValue(map);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requested");

                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("CrewId", fUser.getUid());
                        map1.put("GroupId", groupId);

                        reference.child(groupId).child(fUser.getUid()).setValue(map1);

                        addNotificationRequestCrew(groupId);
                    }
                    if (btText.equals("Leave Crew")){
                        FirebaseDatabase.getInstance().getReference().child("Crew").child(fUser.getUid()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("CrewMember").child(groupId).child(fUser.getUid()).removeValue();

                        addNotificationLeaveCrew(groupId);
                    }
                    if (btText.equals("Cancel Request")){
                        FirebaseDatabase.getInstance().getReference().child("Request").child(fUser.getUid()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Requested").child(groupId).child(fUser.getUid()).removeValue();
                    }
                }
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();
                if (btnText.equals("Edit")) {
                    startActivity(new Intent(getContext(), EditGroupProfileActivity.class));
                } else {
                    if (btnText.equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(groupId).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(groupId).child("follower").child(fUser.getUid()).setValue(true);

                        addNotification(groupId);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").child(groupId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(groupId).child("follower").child(fUser.getUid()).removeValue();

                        addNotification2(groupId);
                    }
                }
            }
        });

        return view;
    }


    private void getNewsCount() {

        FirebaseDatabase.getInstance().getReference().child("NewsUploads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NewsUploads uploads = dataSnapshot.getValue(NewsUploads.class);

                    if (uploads.getGroupId().equals(groupId)) counter ++;
                }

                post.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getReporterCount() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("CrewMember").child(groupId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reporter.setText("" + snapshot.getChildrenCount());
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

                    if (uploads.getGroupId().equals(groupId)) {
                        myPhotoList.add(uploads);
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

    private void checkIfCrew() {

        FirebaseDatabase.getInstance().getReference().child("Crew").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fUser.getUid()).exists()) {
                    checkIfCrewOfThis();
                } else {
                    chechIfRequested();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkIfCrewOfThis() {

        FirebaseDatabase.getInstance().getReference().child("CrewMember").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fUser.getUid()).exists()) {
                    manage.setEnabled(true);
                    manage.setText("Leave Crew");
                } else {
                    manage.setText("Apply");
                    manage.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void chechIfRequested() {

        FirebaseDatabase.getInstance().getReference().child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fUser.getUid()).exists()) {
                    checkIfRequestedToThis();
                } else {
                    manage.setText("Apply");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkIfRequestedToThis() {

        FirebaseDatabase.getInstance().getReference().child("Requested").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fUser.getUid()).exists()) {
                    manage.setEnabled(true);
                    manage.setText("Cancel Request");
                } else {
                    manage.setText("Apply");
                    manage.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkFollowingStatus(String groupId, final Button editProfile) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(groupId).child("follower");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fUser.getUid()).exists()) {
                    editProfile.setText("Following");
                } else {
                    editProfile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void getFollowerCount() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(groupId);

        ref.child("follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                follower.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void groupInfo() {

        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Groups groups = snapshot.getValue(Groups.class);

                if (groups.getImageurl().equals("default")) {
                    imageProfile.setImageResource(R.drawable.ic_group);
                } else {
                    Picasso.get().load(groups.getImageurl()).into(imageProfile);
                }

                groupName.setText(groups.getGroupname());
                description.setText(groups.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void addNotificationLeaveCrew(String groupId) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", fUser.getUid());
        map.put("text", "Left your group crew.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(groupId).push().setValue(map);
    }

    private void addNotification(String groupID) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", fUser.getUid());
        map.put("text", "Started following your Group.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(groupID).push().setValue(map);
    }


    private void addNotification2(String groupID) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", fUser.getUid());
        map.put("text", "Unfollowed your Group.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(groupID).push().setValue(map);
    }

    private void addNotificationRequestCrew(String groupId) {

        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", fUser.getUid());
        map.put("text", "Requested to be a crew member.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(groupId).push().setValue(map);
    }
}