package com.example.touristapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.touristapplication.databinding.ActivityMainBinding;
import com.example.touristapplication.databinding.ActivityOwnerMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class OwnerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment fragment = null;
    ActionBarDrawerToggle mDrawerToggle;

    ActivityOwnerMainBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityOwnerMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.ownerToolbar.toolBar);
        getSupportActionBar().setTitle(R.string.newpost);


        mDrawerToggle = new ActionBarDrawerToggle(this, binding.ownerdrawer, binding.ownerToolbar.toolBar,  R.string.open, R.string.close);
        binding.ownerdrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        binding.ownernavView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.ownerframe_layout,
                    new CreateFragment()).commit();
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
}