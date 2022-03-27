package com.example.touristapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.touristapplication.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.net.IDN;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragment = null;
    ActionBarDrawerToggle mDrawerToggle;

    ActivityMainBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String current_user = firebaseAuth.getCurrentUser().getEmail();
    DocumentReference userprofilenode = FirebaseFirestore.getInstance().collection("User").document(current_user);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.toolBar);
        getSupportActionBar().setTitle(R.string.menu_home);
        HeaderData();


        mDrawerToggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolbar.toolBar, R.string.open, R.string.close);
        binding.drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        binding.navView.setNavigationItemSelectedListener(this);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                    new HomeFragment()).commit();
            HeaderData();

        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.home:
                fragment = new HomeFragment();


                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
                binding.toolbar.toolBar.setTitle(R.string.menu_home);

                break;
            case R.id.profile:
                fragment = new ProfileFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
                binding.toolbar.toolBar.setTitle(R.string.profile);

                break;
            case R.id.settings:
                fragment = new SettingsFragment();

                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
                binding.toolbar.toolBar.setTitle(R.string.action_settings);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();

                Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AuthenticationAvtivity.class));
                finish();
                break;
        }
        binding.drawer.closeDrawer(GravityCompat.START);
        HeaderData();

        return true;
    }


    @Override
    public void onBackPressed() {

        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    private void HeaderData() {
        View headerView = binding.navView.getHeaderView(0);
        CircularImageView profile_photo = headerView.findViewById(R.id.profile_photo_header);
        TextView v2 = headerView.findViewById(R.id.usernameheader);
        TextView v3 = headerView.findViewById(R.id.email_header);
        userprofilenode.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                v2.setText(documentSnapshot.getString("userName"));
                v3.setText(documentSnapshot.getString("email"));

                if (documentSnapshot.getString("imageUrl") != null) {
                    Picasso.get().load(documentSnapshot.getString("imageUrl")).into(profile_photo);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


}