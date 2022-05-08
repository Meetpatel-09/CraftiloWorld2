package com.example.craftiloworld.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.craftiloworld.MainActivity;
import com.example.craftiloworld.Model.Groups;
import com.example.craftiloworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewwHolder>{

    private Context mContext;
    private List<Groups> mGroup;
    private boolean isFregment;

    private FirebaseUser firebaseUser;

    public GroupAdapter(Context mContext, List<Groups> mGroup, boolean isFregment) {
        this.mContext = mContext;
        this.mGroup = mGroup;
        this.isFregment = isFregment;
    }

    @NonNull
    @Override
    public ViewwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.ViewwHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewwHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Groups groups = mGroup.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);

        holder.groupName.setText(groups.getGroupname());

        Picasso.get().load(groups.getImageurl()).placeholder(R.drawable.ic_group).into(holder.imageProfile);

        idfollowed(groups.getGroupid(), holder.btnFollow);

        if (groups.getGroupid().equals(firebaseUser.getUid())) {
            holder.btnFollow.setVisibility(View.GONE);
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnFollow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(groups.getGroupid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(groups.getGroupid()).child("follower").child(firebaseUser.getUid()).setValue(true);

                    addNotification(groups.getGroupid());

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(groups.getGroupid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(groups.getGroupid()).child("follower").child(firebaseUser.getUid()).removeValue();

                    addNotification2(groups.getGroupid());
                }
            }
        });


        holder.groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("groupID", groups.getGroupid());
                mContext.startActivity(intent);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("groupID", groups.getGroupid());
                mContext.startActivity(intent);
            }
        });

    }

    private void idfollowed(final String groupid, final Button btnFollow) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(groupid).exists()) {
                    btnFollow.setText("Following");
                } else {
                    btnFollow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mGroup.size();
    }

    public class ViewwHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfile;
        public TextView groupName;

        public Button btnFollow;

        public ViewwHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            groupName = itemView.findViewById(R.id.group_name);

            btnFollow = itemView.findViewById(R.id.btn_follow);


        }
    }

    private void addNotification(String groupID) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", firebaseUser.getUid());
        map.put("text", "Started following your Group.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(groupID).push().setValue(map);
    }


    private void addNotification2(String groupID) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", firebaseUser.getUid());
        map.put("text", "Unfollowed your Group.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(groupID).push().setValue(map);
    }

}
