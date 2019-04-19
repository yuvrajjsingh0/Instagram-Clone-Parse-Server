package com.gohibo.album.NextSoc;

/**
 * Created by yuvi on 14-02-2018.
 */

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gohibo.album.MainActivity;
import com.gohibo.album.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class UsernameNextSocial extends Fragment {

    Fragment thisFragment;

    public static com.gohibo.album.NextSoc.UsernameNextSocial newInstance() {
        com.gohibo.album.NextSoc.UsernameNextSocial fragment = new com.gohibo.album.NextSoc.UsernameNextSocial();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.next_social_username, container, false);
        final EditText usernameField = (EditText)view.findViewById(R.id.input_username_soc);
        String username = usernameField.getText().toString();
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(username);
        user.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    // everything worked..
                    Snackbar.make(getActivity().findViewById(R.id.activity_next_social), "Username Uddated Successfully.",
                            Snackbar.LENGTH_LONG)
                            .show();
                    destruct();
                }else{
                    usernameField.setError(e.getMessage());
                }
            }
        });
        return view;
    }
    private void destruct(){

    }
}