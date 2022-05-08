package com.example.craftiloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText name;
    private EditText description;


    private Button next;
    private Button close;

    private ProgressDialog pd;

    private FirebaseUser fUser;

    String userid;

    String exists;

    String isCrew;

    String requested;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        close = findViewById(R.id.bt_cancel);
        next = findViewById(R.id.bt_next);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        userid = fUser.getUid();


        pd = new ProgressDialog(this);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateGroupActivity.this, MainActivity.class));
                finish();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()) {
                    exists = "yes";
                } else {
                    exists = "no";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Crew").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()) {
                    isCrew = "yes";
                } else {
                    isCrew = "no";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).exists()) {
                    requested = "yes";
                } else {
                    requested = "no";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String gname = name.getText().toString();
                final String gdescription = description.getText().toString();

                if(isEmpty(gname)) {

                    name.setError("You must enter group name");

                } else if(isEmpty(gdescription)) {

                    description.setError("Group description is required");

                } else {
                    if (exists.equals("no")) {
                        if (isCrew.equals("no")) {
                            if (requested.equals("no")) {
                                pd.dismiss();
                                create(userid, gname, gdescription);
                            }
                            if (requested.equals("yes")) {
                                pd.dismiss();
                                startActivity(new Intent(CreateGroupActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                Toast.makeText(CreateGroupActivity.this, "You have applied to another group.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (isCrew.equals("yes")){
                            pd.dismiss();
                            startActivity(new Intent(CreateGroupActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            Toast.makeText(CreateGroupActivity.this, "You are a crew member.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (exists.equals("yes")) {
                        pd.dismiss();
                        startActivity(new Intent(CreateGroupActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        Toast.makeText(CreateGroupActivity.this, "You can create only one Group.", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

    }

    private void create(String uid, String name, String description) {

        pd.setMessage("Please Wait!");
        pd.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");

        HashMap<String, Object> map = new HashMap<>();
        map.put("groupname", name);
        map.put("description", description);
        map.put("admin", uid);
        map.put("groupid", uid);
        map.put("imageurl", "default");
        ref.child(uid).setValue(map);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Crew");
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("CrewId", fUser.getUid());
        map1.put("GroupId", fUser.getUid());
        reference.child(uid).setValue(map1);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CrewMember");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CrewId", fUser.getUid());
        hashMap.put("GroupId", fUser.getUid());
        databaseReference.child(uid).child(uid).setValue(hashMap);

        startActivity(new Intent(CreateGroupActivity.this, AddGroupIconActivity.class));
        Toast.makeText(CreateGroupActivity.this, "Group Created Successfully", Toast.LENGTH_SHORT).show();

        pd.dismiss();

    }


}