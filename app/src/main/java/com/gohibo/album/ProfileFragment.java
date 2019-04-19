package com.gohibo.album;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    protected Toolbar toolbar;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshProfile);
        TextView userName = view.findViewById(R.id.user_name);
        ParseUser user = ParseUser.getCurrentUser();
        final TextView name = view.findViewById(R.id.name_u);
        final TextView userLocation = view.findViewById(R.id.user_loc);
        final TextView userWebsite = view.findViewById(R.id.user_website);
        final TextView userDesc = view.findViewById(R.id.user_desc);
        final ImageView userVerificationBadge = view.findViewById(R.id.verification_badge);
        toolbar = view.findViewById(R.id.profileToolbar);

        userWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userWebsite.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                String url = userWebsite.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        ((AppCompatActivity)getActivity()).setSupportActionBar(this.toolbar);
        userName.setText(ParseUser.getCurrentUser().getUsername());
        toolbar.setTitle(ParseUser.getCurrentUser().getUsername());

        name.setText(user.getString("name"));
        userDesc.setText(user.getString("desc"));

        String website1 = null;
        String loc1 = null;

        if(user.getString("website") != null) {
            final SpannableString website = new SpannableString(user.getString("website"));
            website.setSpan(new UnderlineSpan(), 0, website.length(), 0);
            userWebsite.setText(website);
            final SpannableString loc = new SpannableString(user.getString("location"));
            loc.setSpan(new UnderlineSpan(), 0, loc.length(), 0);
            userLocation.setText(loc);
        }else{
            userWebsite.setText(website1);
            userLocation.setText(loc1);
        }
        Boolean verified = user.getBoolean("Verified");

        if(verified){
            userVerificationBadge.setImageResource(R.drawable.ic_verified);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ParseUser.getCurrentUser().saveEventually();

                ParseUser user = ParseUser.getCurrentUser();

                name.setText(user.getString("name"));
                userDesc.setText(user.getString("desc"));

                if(user.getString("website") != null) {
                    final SpannableString website = new SpannableString(user.getString("website"));
                    website.setSpan(new UnderlineSpan(), 0, website.length(), 0);
                    userWebsite.setText(website);
                    final SpannableString loc = new SpannableString(user.getString("location"));
                    loc.setSpan(new UnderlineSpan(), 0, loc.length(), 0);
                    userLocation.setText(loc);
                }

                Boolean verified = user.getBoolean("Verified");

                if(verified){
                    userVerificationBadge.setImageResource(R.drawable.ic_verified);
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_nav, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(getContext(), "Settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.action_logout:
                ParseUser user = ParseUser.getCurrentUser();
                user.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Intent intent = new Intent(getContext(), SplashActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }else{
                            Log.d("e", e.getMessage());
                            Toast.makeText(getContext(), "Could not log out. Try again later.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.action_edit:
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.action_make_private:
                Toast.makeText(getContext(), "Thanks for showing interest in this feature. This feature would be available in next updates.", Toast.LENGTH_LONG).show();
                break;

            default:
                Toast.makeText(getContext(), "Thanks for showing interest in this feature. This feature would be available in next updates.", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }
}
