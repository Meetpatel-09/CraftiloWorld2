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
import com.example.craftiloworld.Model.NewsUploads;
import com.example.craftiloworld.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{

    private Context mContext;
    private List<NewsUploads> mUploads;

    public PhotoAdapter(Context mContext, List<NewsUploads> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_itme, parent, false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final NewsUploads uploads = mUploads.get(position);

        Picasso.get().load(uploads.getImageUrl()).into(holder.postImage);
        holder.heading.setText(uploads.getHeading());

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", uploads.getPostId()).apply();
                mContext.getSharedPreferences("GROUP", Context.MODE_PRIVATE).edit().putString("groupID", uploads.getGroupId()).apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage;
        public TextView heading;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.post_image);
            heading = itemView.findViewById(R.id.heading);
        }
    }

}
