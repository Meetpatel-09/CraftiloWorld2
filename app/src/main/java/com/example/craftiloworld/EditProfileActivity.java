package com.example.craftiloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.craftiloworld.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView close;
    private CircleImageView imageProfile;

    private Button save;
    private Button cancel;

    private TextView changePhoto;
    private MaterialEditText fullName;
    private MaterialEditText bio;

    private FirebaseUser fUser;

    private Uri mImaeUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.close);
        imageProfile = findViewById(R.id.image_profile);

        changePhoto = findViewById(R.id.change_photo);
        fullName = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);

        save = findViewById(R.id.bt_save);
        cancel = findViewById(R.id.bt_cancel);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child("Uploads");

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users users = snapshot.getValue(Users.class);

                fullName.setText(users.getName());
                bio.setText(users.getBio());
                if (users.getImageurl().equals("default")) {
                    imageProfile.setImageResource(R.drawable.ic_profile_circle);
                } else {
                    Picasso.get().load(users.getImageurl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", fullName.getText().toString());
        map.put("bio", bio.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).updateChildren(map);
        Toast.makeText(this, "Profile Updated.", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void uploadImage() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (mImaeUri != null){

            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpeg");

            uploadTask = fileRef.putFile(mImaeUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("imageurl").setValue(url);
                        pd.dismiss();
                    } else {
                        pd.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Upload Failed!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            pd.dismiss();
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImaeUri = result.getUri();
            uploadImage();
        } else {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}