package com.example.touristapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.touristapplication.databinding.ActivityMainBinding;
import com.example.touristapplication.databinding.ActivityOwnerMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class OwnerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment fragment = null;
    ActionBarDrawerToggle mDrawerToggle;

    ActivityOwnerMainBinding binding ;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String current_user = firebaseAuth.getCurrentUser().getEmail();
    DocumentReference userprofilenode = FirebaseFirestore.getInstance().collection("User").document(current_user);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityOwnerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.ownerToolbar.toolBar);
        getSupportActionBar().setTitle(R.string.newpost);

        HeaderData();


        mDrawerToggle = new ActionBarDrawerToggle(this, binding.ownerdrawer, binding.ownerToolbar.toolBar,  R.string.open, R.string.close);
        binding.ownerdrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        binding.ownernavView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.ownerframe_layout,
                    new CreateFragment()).commit();
            HeaderData();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.newpost:
                fragment = new CreateFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.ownerframe_layout, fragment).commit();
                binding.ownerToolbar.toolBar.setTitle(R.string.newpost);

                break;
            case R.id.profile:
                fragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.ownerframe_layout, fragment).commit();
                binding.ownerToolbar.toolBar.setTitle(R.string.profile);
                break;

            case R.id.ownerlogout:
                FirebaseAuth.getInstance().signOut();


                Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this,AuthenticationAvtivity.class));
                finish();
                break;
        }
        binding.ownerdrawer.closeDrawer(GravityCompat.START);
        HeaderData();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.ownerdrawer.isDrawerOpen(GravityCompat.START)) {
            binding.ownerdrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void HeaderData() {
        View headerView = binding.ownernavView.getHeaderView(0);
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
                Toast.makeText(OwnerMainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}