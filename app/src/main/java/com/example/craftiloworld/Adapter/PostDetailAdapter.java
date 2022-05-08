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

import com.example.craftiloworld.Fragements.PostDetailFragment;
import com.example.craftiloworld.Fragements.ProfileFragment;
import com.example.craftiloworld.Fragements.UserProfileFragment;
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.Model.Users;
import com.example.craftiloworld.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PostDetailAdapter extends RecyclerView.Adapter<PostDetailAdapter.ViewHolder>{

    private Context mContext;
    private List<NewsUploads> mUploads;

    private FirebaseUser firebaseUser;

    public PostDetailAdapter(Context mContext, List<NewsUploads> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_detail_item, parent, false);
        return new PostDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final NewsUploads uploads = mUploads.get(position);

        Picasso.get().load(uploads.getImageUrl()).into(holder.postImage);
        holder.heading.setText(uploads.getHeading());
        holder.newsDescription.setText(uploads.getDescription());
        holder.date.setText(uploads.getDate());

        FirebaseDatabase.getInstance().getReference().child("Users").child(uploads.getReporterId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if (users.getImageurl().equals("default")) {
                    holder.reporterImg.setImageResource(R.drawable.ic_profile_circle);
                } else {
                    Picasso.get().load(users.getImageurl()).into(holder.reporterImg);
                }
                holder.reporterName.setText(users.getName());
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

        holder.reporterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", uploads.getReporterId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserProfileFragment()).commit();
            }
        });

        holder.reporterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", uploads.getReporterId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserProfileFragment()).commit();
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

        public TextView heading;
        public TextView reporterName;
        public TextView date;
        public TextView newsDescription;

        public ImageView postImage;
        public ImageView reporterImg;
        public ImageView save;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.post_heading);
            reporterName = itemView.findViewById(R.id.reporter_name);
            date = itemView.findViewById(R.id.date);
            newsDescription = itemView.findViewById(R.id.post_description);

            postImage = itemView.findViewById(R.id.post_image);
            reporterImg = itemView.findViewById(R.id._reporter_profile_img);
            save = itemView.findViewById(R.id.save);
        }
    }

}
