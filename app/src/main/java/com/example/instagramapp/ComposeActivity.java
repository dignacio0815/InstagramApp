package com.example.instagramapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.instagramapp.fragments.ComposeFragment;
import com.example.instagramapp.fragments.PostsFragment;
import com.example.instagramapp.fragments.ProfileFragment;
import com.jaeger.library.StatusBarUtil;
import com.parse.ParseClassName;

@ParseClassName("Post")

public class ComposeActivity extends AppCompatActivity {
    public final String APP_TAG = "ComposeActivity";

    ComposeFragment composeFragment;
    // create a reference to the navigation bar
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        // transparent status bar
        StatusBarUtil.setTransparent(ComposeActivity.this);

        // toolbar wiring to UI element
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment homeFragment = new PostsFragment();
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        homeFragment = new PostsFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, homeFragment).commit();
                        break;
                    case R.id.action_compose:
                        composeFragment = new ComposeFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, composeFragment).commit();
                        break;
                    case R.id.action_profile:
                        Fragment profileFragment = new ProfileFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, profileFragment).commit();
                        break;
                    default:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, homeFragment).commit();
                        return true;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}