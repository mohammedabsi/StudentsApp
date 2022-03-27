package com.example.touristapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toolbar;

import com.example.touristapplication.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {
Toolbar toolbar2;

ActivityMain2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar2 = findViewById(R.id.toolbar2);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar2.toolBar);
        getSupportActionBar().setTitle(R.string.placeDetails);



        getSupportFragmentManager().beginTransaction().replace(R.id.main2_container ,new DetailsFragment()).commit();
    }



    @Override public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main2_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }
}