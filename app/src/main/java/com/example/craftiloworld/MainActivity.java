package com.example.craftiloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.craftiloworld.Fragements.CategoryFragment;
import com.example.craftiloworld.Fragements.GroupProfileFragment;
import com.example.craftiloworld.Fragements.HomeFragment;
import com.example.craftiloworld.Fragements.ManageGroupFragment;
import com.example.craftiloworld.Fragements.NotificationFragment;
import com.example.craftiloworld.Fragements.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    private BottomNavigationView bottomNavigationView2;
    private Fragment selectorFragment2;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.menu_visible);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView2 = findViewById(R.id.bottom_navigation2);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_home :
                        bottomNavigationView2.setVisibility(View.GONE);
                        textView.setText("invisible");
                        bottomNavigationView2.getMenu().getItem(0).setChecked(true);
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_category :
                        bottomNavigationView2.setVisibility(View.GONE);
                        textView.setText("invisible");
                        bottomNavigationView2.getMenu().getItem(0).setChecked(true);
                        selectorFragment = new CategoryFragment();
                        break;

                    case R.id.nav_more_menu :
                        selectorFragment = null;

                        String tv = textView.getText().toString();

                        if (tv.equals("invisible")) {
                            bottomNavigationView2.setVisibility(View.VISIBLE);
                            bottomNavigationView2.getMenu().getItem(0).setVisible(false);
                            bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                                @Override
                                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                                    switch (menuItem.getItemId()) {

                                        case R.id.nav_manage_group:
                                            bottomNavigationView2.setVisibility(View.GONE);
                                            textView.setText("invisible");
                                            selectorFragment2 = new ManageGroupFragment();
                                            break;

                                        case R.id.nav_create_group:
                                            selectorFragment2 = null;
                                            startActivity(new Intent(MainActivity.this, CreateGroupActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            break;

                                        case R.id.nav_upload:
                                            bottomNavigationView2.setVisibility(View.GONE);
                                            textView.setText("invisible");
                                            selectorFragment2 = null;
                                            startActivity(new Intent(MainActivity.this, UploadActivity.class));
                                            break;
                                    }
                                    if (selectorFragment2 != null) {
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment2).commit();
                                    }

                                    return true;
                                }
                            });

                            textView.setText("visible");
                        } else {
                            bottomNavigationView2.setVisibility(View.GONE);
                            textView.setText("invisible");
                        }
                        break;

                    case R.id.nav_notification :
                        bottomNavigationView2.setVisibility(View.GONE);
                        textView.setText("invisible");
                        bottomNavigationView2.getMenu().getItem(0).setChecked(true);
                        selectorFragment = new NotificationFragment();
                        break;

                    case R.id.nav_profile :
                        bottomNavigationView2.setVisibility(View.GONE);
                        textView.setText("invisible");
                        bottomNavigationView2.getMenu().getItem(0).setChecked(true);
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if (selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }

                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String groupID = intent.getString("groupID");

            if (groupID != null) {
                getSharedPreferences("GROUP", MODE_PRIVATE).edit().putString("groupID", groupID).apply();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupProfileFragment()).commit();
            }
        } else {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.add(R.id.fragment_container, new HomeFragment());
            ft.commit();

            bottomNavigationView.setSelectedItemId(R.id.nav_home);

        }

    }
}