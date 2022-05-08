package com.example.craftiloworld.Fragements;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craftiloworld.Adapter.CrewAdapter;
import com.example.craftiloworld.Adapter.RequestAdapter;
import com.example.craftiloworld.EditGroupProfileActivity;
import com.example.craftiloworld.MainActivity;
import com.example.craftiloworld.Model.Crew;
import com.example.craftiloworld.Model.Groups;
import com.example.craftiloworld.Model.Request;
import com.example.craftiloworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageGroupFragment extends Fragment {

    private RecyclerView recyclerViewCrew;
    private CrewAdapter crewAdapter;
    private List<Crew> crewList;

    private RecyclerView recyclerViewRequest;
    private RequestAdapter requestAdapter;
    private List<Request> requestList;

    private CircleImageView imageProfile;
    private TextView groupName;

    private ImageView crew;
    private ImageView request;

    private Button editProfile;
    private Button viewProfile;

    private FirebaseUser fUser;

    String groupProfileId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_group, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        groupProfileId = fUser.getUid();

        imageProfile = view.findViewById(R.id.image_profile);
        groupName = view.findViewById(R.id.group_name);

        crew = view.findViewById(R.id.crew);
        request = view.findViewById(R.id.request);
        editProfile = view.findViewById(R.id.edit_profile);
        viewProfile = view.findViewById(R.id.view_profile);

        recyclerViewRequest = view.findViewById(R.id.recycle_view_request);
        recyclerViewRequest.setHasFixedSize(true);
        recyclerViewRequest.setLayoutManager(new LinearLayoutManager(getContext()));
        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(getContext(), requestList, true);
        recyclerViewRequest.setAdapter(requestAdapter);

        recyclerViewCrew = view.findViewById(R.id.recycle_view_crew);
        recyclerViewCrew.setHasFixedSize(true);
        recyclerViewCrew.setLayoutManager(new LinearLayoutManager(getContext()));
        crewList = new ArrayList<>();
        crewAdapter = new CrewAdapter(getContext(), crewList, true);
        recyclerViewCrew.setAdapter(crewAdapter);

        check();

        recyclerViewCrew.setVisibility(View.VISIBLE);
        recyclerViewRequest.setVisibility(View.GONE);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditGroupProfileActivity.class));
            }
        });

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("groupID", fUser.getUid());
                getContext().startActivity(intent);
            }
        });

        crew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewCrew.setVisibility(View.VISIBLE);
                recyclerViewRequest.setVisibility(View.GONE);
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewCrew.setVisibility(View.GONE);
                recyclerViewRequest.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void check() {

        FirebaseDatabase.getInstance().getReference().child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(fUser.getUid()).exists()) {
                    groupInfo();
                    myRequest();
                    myCrew();
                } else {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    Toast.makeText(getActivity(), "You are not an admin of any group.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void myCrew() {

        FirebaseDatabase.getInstance().getReference().child("CrewMember").child(groupProfileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                crewList.clear();
                for (DataSnapshot dataSnapshot :snapshot.getChildren()) {
                    Crew crew = dataSnapshot.getValue(Crew.class);
                    crewList.add(crew);
                }
                crewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void myRequest() {

        FirebaseDatabase.getInstance().getReference().child("Requested").child(groupProfileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Request request = dataSnapshot.getValue(Request.class);
                        requestList.add(request);
                }
                requestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void groupInfo() {

        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupProfileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Groups groups = snapshot.getValue(Groups.class);
                String img = groups.getImageurl();
                if (img.equals("default"))
                {
                    imageProfile.setImageResource(R.drawable.ic_group);
                } else {
                    Picasso.get().load(groups.getImageurl()).into(imageProfile);
                }
                groupName.setText(groups.getGroupname());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}