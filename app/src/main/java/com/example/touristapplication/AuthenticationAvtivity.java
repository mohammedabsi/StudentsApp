package com.example.touristapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.touristapplication.databinding.ActivityAuthenticationBinding;
import com.google.android.material.tabs.TabLayout;

public class AuthenticationAvtivity extends AppCompatActivity {
//TabLayout authTL ;
//ViewPager2 authVP ;
    AuthFragmentAdapter adapter;
    ActivityAuthenticationBinding binding ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        FragmentManager fm = getSupportFragmentManager();
        adapter = new AuthFragmentAdapter(fm, getLifecycle());
        binding.authVP.setAdapter(adapter);

        binding.authTL.addTab(binding.authTL.newTab().setText(R.string.login));
        binding.authTL.addTab(binding.authTL.newTab().setText(R.string.register));



        binding.authTL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.authVP.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.authVP.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                binding.authTL.selectTab(binding.authTL.getTabAt(position));

            }
        });
    }




}