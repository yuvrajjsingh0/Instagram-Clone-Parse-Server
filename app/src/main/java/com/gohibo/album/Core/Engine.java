package com.gohibo.album.Core;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by yuvi on 11-02-2018.
 */

public class Engine {

    public String t_username;
    public String t_email;
    private boolean status = false;
    protected String temp;

    public void processFBUser(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try{
                    t_username = object.getString("name");
                    Log.d("USERNAME:", object.getString("name"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try{
                    t_email = object.getString("email");
                    Log.d("EMAIL:", object.getString("email"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                //saveNewUser();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
