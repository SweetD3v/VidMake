package com.dev4life.vidmake.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dev4life.vidmake.R;
import com.dev4life.vidmake.databinding.ActivityMainBinding;
import com.dev4life.vidmake.ui.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().
                beginTransaction().replace(R.id.container, new HomeFragment())
                .commit();

    }
}