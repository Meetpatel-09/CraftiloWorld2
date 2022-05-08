package com.example.craftiloworld.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.craftiloworld.Fragements.UserProfileFragment;
import com.example.craftiloworld.Model.Crew;
import com.example.craftiloworld.Model.Users;
import com.example.craftiloworld.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.VieewHolder> {

    private Context mContext;
    private List<Crew> mCrew;
    private boolean isFregment;

    private FirebaseUser firebaseUser;

    public CrewAdapter(Context mContext, List<Crew> mCrew, boolean isFregment) {
        this.mContext = mContext;
        this.mCrew = mCrew;
        this.isFregment = isFregment;
    }

    @NonNull
    @Override
    public VieewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.crew_item, parent, false);
        return new CrewAdapter.VieewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VieewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Crew crew = mCrew.get(position);

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", crew.getCrewId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserProfileFragment()).commit();
            }
        });

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", crew.getCrewId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserProfileFragment()).commit();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Users").child(crew.getCrewId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                holder.userName.setText(users.getName());

                if (users.getImageurl().equals("default")) {
                    holder.imgProfile.setImageResource(R.drawable.ic_profile_circle);
                } else {
                    Picasso.get().load(users.getImageurl()).into(holder.imgProfile);
                }

                if (crew.getGroupId().equals(users.getId())) {
                    holder.remove.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();

                alertDialog.setTitle("Do you want to remover member?");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("CrewMember").child(firebaseUser.getUid()).child(crew.getCrewId()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Crew").child(crew.getCrewId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "Crew Member Removed.", Toast.LENGTH_SHORT).show();
                                    addNotification(crew.getCrewId());
                                }
                            }
                        });

                    }
                });

                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCrew.size();
    }

    public class VieewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imgProfile;
        public TextView userName;
        public Button remove;

        public VieewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.crew_name);
            remove = itemView.findViewById(R.id.btn_remove);
        }
    }

    private void addNotification(String crewID) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", firebaseUser.getUid());
        map.put("text", "Removed you for crew.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(crewID).push().setValue(map);
    }
}
