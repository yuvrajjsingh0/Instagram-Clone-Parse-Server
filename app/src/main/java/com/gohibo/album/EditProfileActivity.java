package com.gohibo.album;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class EditProfileActivity extends AppCompatActivity {

    protected EditText _name;
    protected EditText _website;
    protected EditText _bio;
    protected EditText _loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final ParseUser user = ParseUser.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar_edit_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _name = findViewById(R.id.input_name_edit);
        _website = findViewById(R.id.input_website_edit);
        _bio = findViewById(R.id.input_desc_edit);
        _loc = findViewById(R.id.input_current_city_edit);
        final String name = user.getString("name");
        final String website = user.getString("website");
        final String bio = user.getString("desc");
        final String loc = user.getString("location");
        _name.setText(name);
        _website.setText(website);
        _bio.setText(bio);
        _loc.setText(loc);
        _loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_save:
                ParseUser user = ParseUser.getCurrentUser();
                user.put("name", _name.getText().toString());
                user.put("website", _website.getText().toString());
                user.put("desc", _bio.getText().toString());
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(getApplicationContext(), "Updated Successfully.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("frag", "ProfileFragment");
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
                break;
            default:
                break;
        }
        return true;
    }
}