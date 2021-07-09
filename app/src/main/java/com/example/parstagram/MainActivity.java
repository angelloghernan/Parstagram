package com.example.parstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.HomeFragment;
import com.example.parstagram.fragments.MyProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment compose_fragment = new ComposeFragment();
        final Fragment home_fragment = new HomeFragment();
        final Fragment my_profile_fragment = new MyProfileFragment();

        fragmentManager.beginTransaction().replace(R.id.flContainer, home_fragment).commit();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // When a tab is pressed (bottom navigation view), check which one is pressed and select that fragment to show.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = home_fragment;
                        break;
                    case R.id.action_post:
                        fragment = compose_fragment;
                        break;
                    case R.id.action_myprofile:
                        fragment = my_profile_fragment;
                        break;
                    default:
                        return true;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

    }

}