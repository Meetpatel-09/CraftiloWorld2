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
import android.widget.TextView;
import android.widget.Toast;

import com.example.craftiloworld.Model.Groups;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddGroupIconActivity extends AppCompatActivity {

    private CircleImageView image;
    private TextView textView;

    private Button create;
    private Button close;

    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    private FirebaseUser fUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_icon);

        image = findViewById(R.id.image);
        textView = findViewById(R.id.tv_icon);

        create = findViewById(R.id.bt_done);
        close= findViewById(R.id.bt_cancel);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        storageRef = FirebaseStorage.getInstance().getReference().child("Groupicon");


        FirebaseDatabase.getInstance().getReference().child("Groups").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Groups group = snapshot.getValue(Groups.class);

                if (group.getImageurl() == "default") {
                    image.setImageResource(R.drawable.ic_add);
                } else {
                    Picasso.get().load(group.getImageurl()).into(image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddGroupIconActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddGroupIconActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(AddGroupIconActivity.this);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(AddGroupIconActivity.this);
            }
        });


    }

    private void uploadImage() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (mImageUri != null) {

            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpeg");

            uploadTask = fileRef.putFile(mImageUri);
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

                        FirebaseDatabase.getInstance().getReference().child("Groups").child(fUser.getUid()).child("imageurl").setValue(url);

                        pd.dismiss();
                        Toast.makeText(AddGroupIconActivity.this, "Group Icon Added", Toast.LENGTH_SHORT).show();
                    } else {
                        pd.dismiss();
                        Toast.makeText(AddGroupIconActivity.this, "Upload Fail!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } else {
            pd.dismiss();
            Toast.makeText(this, "No Image was selected.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            mImageUri = result.getUri();

            uploadImage();
        } else {
            Toast.makeText(this, "Something Went Wrong!!!", Toast.LENGTH_SHORT).show();
        }
    }

}