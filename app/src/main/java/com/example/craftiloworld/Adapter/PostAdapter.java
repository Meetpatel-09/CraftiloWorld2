package com.example.craftiloworld.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.craftiloworld.Fragements.GroupProfileFragment;
import com.example.craftiloworld.Fragements.PostDetailFragment;
import com.example.craftiloworld.Model.Groups;
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<NewsUploads> mUploads;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<NewsUploads> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final NewsUploads uploads = mUploads.get(position);

        Picasso.get().load(uploads.getImageUrl()).into(holder.postImage);
        holder.postHeading.setText(uploads.getHeading());
        holder.postDescription.setText(uploads.getDescription());
        holder.date.setText(uploads.getDate());

        FirebaseDatabase.getInstance().getReference().child("Groups").child(uploads.getGroupId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Groups groups = snapshot.getValue(Groups.class);

                if (groups.getImageurl().equals("default")) {
                    holder.groupProfile.setImageResource(R.drawable.ic_group);
                } else {
                    Picasso.get().load(groups.getImageurl()).into(holder.groupProfile);
                }
                holder.groupName.setText(groups.getGroupname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isSaves(uploads.getPostId(), holder.save);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(uploads.getPostId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(uploads.getPostId()).removeValue();
                }
            }
        });

        holder.groupProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("GROUP", Context.MODE_PRIVATE).edit().putString("groupID", uploads.getGroupId()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupProfileFragment()).commit();
            }
        });

        holder.groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("GROUP", Context.MODE_PRIVATE).edit().putString("groupID", uploads.getGroupId()).apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupProfileFragment()).commit();
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", uploads.getPostId()).apply();
                mContext.getSharedPreferences("GROUP", Context.MODE_PRIVATE).edit().putString("groupID", uploads.getGroupId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        holder.postHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", uploads.getPostId()).apply();
                mContext.getSharedPreferences("GROUP", Context.MODE_PRIVATE).edit().putString("groupID", uploads.getGroupId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        holder.postDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", uploads.getPostId()).apply();
                mContext.getSharedPreferences("GROUP", Context.MODE_PRIVATE).edit().putString("groupID", uploads.getGroupId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });
    }

    private void isSaves(final String postid, final ImageView image) {

        FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()) {
                    image.setImageResource(R.drawable.ic_saved);
                    image.setTag("saved");
                } else {
                    image.setImageResource(R.drawable.ic_save);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;
        public ImageView save;
        public ImageView groupProfile;

        public TextView groupName;
        public TextView postHeading;
        public TextView postDescription;
        public TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupName = itemView.findViewById(R.id.group_name);
            postHeading = itemView.findViewById(R.id.post_heading);
            postDescription = itemView.findViewById(R.id.post_description);
            date = itemView.findViewById(R.id.date);

            postImage = itemView.findViewById(R.id.post_image);
            save = itemView.findViewById(R.id.save);
            groupProfile = itemView.findViewById(R.id.profile_img);

        }
    }

}
