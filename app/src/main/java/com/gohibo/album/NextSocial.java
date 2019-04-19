package com.gohibo.album;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseAnalytics;

public class NextSocial extends AppCompatActivity {

    public int selectedFragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_social);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        Toolbar toolbar = (Toolbar)findViewById(R.id.soc_toolbar);
        setSupportActionBar(toolbar);
        setScreen(com.gohibo.album.NextSoc.UsernameNextSocial.newInstance());
        Button button = (Button)findViewById(R.id.btn_soc_us);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFragment = selectedFragment + 1;
                navigate();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.next_social, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_next:
                selectedFragment = selectedFragment + 1;
                navigate();
                break;
        }
        return true;
    }
    private void navigate(){

        Fragment selectedFrag = null;
        switch (selectedFragment) {
            case 0:
                selectedFrag = com.gohibo.album.NextSoc.UsernameNextSocial.newInstance();
                break;
            case 1:
                selectedFrag = ExploreFragment.newInstance();
                break;
            case 2:
                selectedFrag = LikesFragment.newInstance();
                break;
            case 3:
                selectedFrag = ProfileFragment.newInstance();
                break;
        }
        setScreen(selectedFrag);
    }

    private void setScreen(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.soc_frame_layout, fragment);
        transaction.commit();
    }
}
