package com.gohibo.album;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.gohibo.album.Core.PushReceiver;
import com.gohibo.album.helper.BottomNavHelper;
import com.parse.ParseAnalytics;

public class MainActivity extends AppCompatActivity {

    PushReceiver pushReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Intent intent = getIntent();
        String frag = "";
        Boolean uploading = false;
        String[] bmps = null;

        if(intent.getStringExtra("frag") != null){
            frag = intent.getStringExtra("frag");
            Log.d("Got", intent.getStringExtra("frag"));
        }else if(intent.getStringExtra("uploading") != null){
            uploading = intent.getBooleanExtra("uploading", false);
            Log.d("Got", intent.getStringExtra("frag"));

            if(intent.getStringArrayExtra("bmpLocations") != null){
                bmps = intent.getStringArrayExtra("bmplocations");
            }else{
                uploading = false;
            }
        }

        if(uploading){

        }

        // it's time to setup UI
        setupUI(frag);
        IntentFilter intentFilter = new IntentFilter("com.parse.push.intent.RECEIVE");
        registerReceiver(pushReceiver, intentFilter);

    }

    protected void setupUI(final String f){

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.action_explore:
                        selectedFragment = ExploreFragment.newInstance();
                        break;
                    case R.id.action_likes:
                        selectedFragment = LikesFragment.newInstance();
                        break;
                    case R.id.action_profile:
                        selectedFragment = ProfileFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        // show fragment on first launch
        if(f.isEmpty()) {
            bottomNavigationView.setSelectedItemId(R.id.action_home);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
            transaction.commit();
        }else{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment frag = null;
            switch (f){
                case "LikesFragment":
                    bottomNavigationView.setSelectedItemId(R.id.action_likes);
                    Log.d("Got", "Shit!!!!");
                    frag = LikesFragment.newInstance();
                    break;
                case "ProfileFragment":
                    bottomNavigationView.setSelectedItemId(R.id.action_profile);
                    frag = ProfileFragment.newInstance();
                    break;
                default:
                    frag = HomeFragment.newInstance();
                    break;
            }
            Log.d("Got", "Shit!!!!");
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.frame_layout, frag);
            transaction.commit();
        }

        // cursive font for header for API 1. I don't need that bcoz my app requires minimum sdk 15

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Snackbar.make(findViewById(R.id.action_home), "Please use your device in Portrait mode.",
                    Snackbar.LENGTH_LONG)
                    .show();
            Log.d("M:","Landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("M:","Portrait");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IntentFilter intentFilter = new IntentFilter("com.parse.push.intent.RECEIVE");
        registerReceiver(pushReceiver, intentFilter);
    }
}
