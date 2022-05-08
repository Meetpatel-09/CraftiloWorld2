package com.example.craftiloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.craftiloworld.Model.Crew;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Uri imageUri;
    private String imageUrl;

    private EditText heading;
    private EditText description;

    private ImageView image_added;

    private Button upload;
    private Button cancel;

    private FirebaseUser fUser;

    String cat;

    String groupId;

    String isCrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Spinner spinner = findViewById(R.id.category);
        spinner.setOnItemSelectedListener(this);

        heading = findViewById(R.id.heading);
        description = findViewById(R.id.description);
        image_added = findViewById(R.id.image_added);
        upload = findViewById(R.id.upload);
        cancel = findViewById(R.id.cancel);

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseDatabase.getInstance().getReference().child("Crew").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(fUser.getUid()).exists()) {
                    isCrew = "yes";
                } else {
                    isCrew = "no";
                    Toast.makeText(UploadActivity.this, "You are not a crew member of any group.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UploadActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadActivity.this, MainActivity.class));
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child("Crew").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Crew crew = snapshot.getValue(Crew.class);

                        groupId = crew.getGroupId();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                uploadNews();
            }
        });

        CropImage.activity().start(UploadActivity.this);

    }

    private void uploadNews() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference("Post").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("NewsUploads");
                    String uploadId = reference.push().getKey();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    String currentDate = sdf.format(new Date());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("postId", uploadId);
                    map.put("imageUrl", imageUrl);
                    map.put("heading", heading.getText().toString());
                    map.put("description", description.getText().toString());
                    map.put("reporterId", fUser.getUid());
                    map.put("date", currentDate);
                    map.put("groupId", groupId);
                    map.put("category", cat);

                    reference.child(uploadId).setValue(map);
                    pd.dismiss();
                    startActivity(new Intent(UploadActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(UploadActivity.this, "News Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            pd.dismiss();
        Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
    }

    }

    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            image_added.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Try Again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UploadActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cat = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}