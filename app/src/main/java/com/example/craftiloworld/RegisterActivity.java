package com.example.craftiloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText cpassword;
    private Button btnRegister;
    private TextView tvLogin;


    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.con_password);
        btnRegister = findViewById(R.id.bt_reg);
        tvLogin = findViewById(R.id.tv_login);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uname = name.getText().toString();
                String uemail = email.getText().toString();
                String upassword = password.getText().toString();
                String ucpassword = cpassword.getText().toString();


                if(isEmpty(uname)) {

                    name.setError("You must enter your name to register");

                } else if(isEmpty(uemail)) {

                    email.setError("Email is required");

                } else if(isEmpty(upassword)) {

                    password.setError("Enter your password.");

                } else if(upassword.length() < 6) {

                    Toast.makeText(RegisterActivity.this, "Password too short.", Toast.LENGTH_SHORT).show();

                } else if (!ucpassword.equals(upassword)) {
                    Toast.makeText(RegisterActivity.this, "Password do nat match.", Toast.LENGTH_SHORT).show();
                } else {

                    registation(uname, uemail, upassword);

                }
            }
        });

    }

    private void registation(final String name, final String email, final String password) {

        pd.setMessage("Please Wait!");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("password", password);
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("imageurl", "default");

                mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "Update your profile for better experience.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}