package com.example.craftiloworld.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.craftiloworld.Fragements.UserProfileFragment;
import com.example.craftiloworld.Model.Request;
import com.example.craftiloworld.Model.Users;
import com.example.craftiloworld.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{

    private Context mContext;
    private List<Request> mRequest;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public RequestAdapter(Context mContext, List<Request> mRequest, boolean isFragment) {
        this.mContext = mContext;
        this.mRequest = mRequest;
        this.isFragment = isFragment;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.request_item, parent, false);
        return new RequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Request request = mRequest.get(position);

        FirebaseDatabase.getInstance().getReference().child("Users").child(request.getCrewId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);

                holder.userName.setText(user.getName());

                if (user.getImageurl().equals("default")){
                    holder.imgProfile.setImageResource(R.drawable.ic_profile_circle);
                } else {
                    Picasso.get().load(user.getImageurl()).into(holder.imgProfile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", request.getCrewId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserProfileFragment()).commit();
            }
        });

        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", request.getCrewId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserProfileFragment()).commit();
            }
        });

        holder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();

                alertDialog.setTitle("Do you want to reject request?");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Requested").child(firebaseUser.getUid()).child(request.getCrewId()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Request").child(request.getCrewId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "Request Denied.", Toast.LENGTH_SHORT).show();
                                    addNotification2(request.getCrewId());
                                }
                            }
                        });

                    }
                });

                alertDialog.show();
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();

                alertDialog.setTitle("Add as Crew Member?");

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Crew");
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("CrewId", request.getCrewId());
                        map1.put("GroupId", firebaseUser.getUid());
                        reference.child(request.getCrewId()).setValue(map1);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CrewMember");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("CrewId", request.getCrewId());
                        hashMap.put("GroupId", firebaseUser.getUid());
                        databaseReference.child(firebaseUser.getUid()).child(request.getCrewId()).setValue(hashMap);
                        FirebaseDatabase.getInstance().getReference().child("Requested").child(firebaseUser.getUid()).child(request.getCrewId()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Request").child(request.getCrewId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "New crew member added successfully.", Toast.LENGTH_SHORT).show();
                                    addNotification(request.getCrewId());
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
        return mRequest.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imgProfile;
        public TextView userName;
        public Button btnAccept;
        public ImageView deny;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.user_name);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            deny = itemView.findViewById(R.id.deny);

        }
    }

    private void addNotification(String crewID) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", firebaseUser.getUid());
        map.put("text", "Accepted you request for Crew member.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(crewID).push().setValue(map);
    }


    private void addNotification2(String crewID) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", firebaseUser.getUid());
        map.put("text", "Denied your request for crew member.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notification").child(crewID).push().setValue(map);
    }
}
