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
import com.example.craftiloworld.Fragements.UserProfileFragment;
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.Model.Notification;
import com.example.craftiloworld.Model.Users;
import com.example.craftiloworld.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notification notification = mNotification.get(position);

        getUser(holder.imageProfile, holder.username, notification.getUserid());
        holder.comment.setText(notification.getText());

        if (notification.isPost()) {
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostid());

        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isPost()){
                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", notification.getPostid()).apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                } else {
                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", notification.getUserid()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserProfileFragment()).commit();
                }
            }
        });

    }

    private void getPostImage(final ImageView imageView, String postId) {

        FirebaseDatabase.getInstance().getReference().child("NewsUploads").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NewsUploads uploads = snapshot.getValue(NewsUploads.class);
                Picasso.get().load(uploads.getImageUrl()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUser(final ImageView imageView, final TextView textView, String userId) {

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                if (users.getImageurl().equals("default")){
                    imageView.setImageResource(R.drawable.ic_profile_circle);
                } else {
                    Picasso.get().load(users.getImageurl()).into(imageView);
                }
                textView.setText(users.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile;
        public ImageView postImage;
        public TextView username;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.user_name);
            comment = itemView.findViewById(R.id.comment);

        }
    }

}
