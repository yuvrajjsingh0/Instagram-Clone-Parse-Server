package com.gohibo.album;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.internal.CollectionMapper;
import com.gohibo.album.Core.Engine;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.color.white;
import static android.view.View.X;
import static android.view.View.Y;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.firstLaunchProgressBar);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.primaryContainer);

        linearLayout.setVisibility(View.INVISIBLE);

        final ParseUser user = ParseUser.getCurrentUser();

        //Log.d("u", user.toString());
        if (user == null) {
            progressBar.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);

            Button loginButton = (Button) findViewById(R.id.loginButton);
            Button registerButton = (Button) findViewById(R.id.signupButton);
            Button facebookLogin = (Button) findViewById(R.id.fb_login_button);
            final List<String> permissions = Arrays.asList("public_profile", "email");
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });

            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reg();
                }
            });

            facebookLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.INVISIBLE);
                    ParseFacebookUtils.logInWithReadPermissionsInBackground(SplashActivity.this, permissions, new LogInCallback() {
                        @Override
                        public void done(final ParseUser user, ParseException err) {
                            Log.d("got", "res");
                            if (err != null) {
                                Log.d("Error:", err.getMessage());
                            }
                            if (user == null) {
                                Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                                Snackbar.make(findViewById(R.id.activity_splash), "Could not login. Try again later.",
                                        Snackbar.LENGTH_LONG)
                                        .show();
                            } else if (user.isNew()) {
                                Log.d("MyApp", "User signed up and logged in through Facebook!");
                                final Engine engine = new Engine();
                                engine.processFBUser();
                                final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this,
                                        R.style.AppTheme_DarkDialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Wait a few seconds...");
                                progressDialog.setCancelable(false);
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.show();
                                new java.util.Timer().schedule(
                                        new java.util.TimerTask() {
                                            @Override
                                            public void run() {
                                                String username = engine.t_username;
                                                String email = engine.t_email;
                                                Random random = new Random();
                                                int i = random.nextInt(254378) + 2375;
                                                username = username + Integer.toString(i);
                                                String temp = username.replaceAll("\\s+", "");
                                                username = temp;
                                                ParseUser user = ParseUser.getCurrentUser();
                                                user.setUsername(username);
                                                user.setEmail(email);
                                                user.saveEventually(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                        } else {
                                                            String username1 = engine.t_username;
                                                            switch (e.getCode()) {
                                                                case ParseException.USERNAME_TAKEN:
                                                                    Random random = new Random();
                                                                    int i = random.nextInt(254378) + 2375;
                                                                    username1 = username1 + Integer.toString(i);
                                                                    Log.d("USERNAME", username1);
                                                                    break;

                                                                case ParseException.EMAIL_TAKEN:
                                                                    progressDialog.dismiss();
                                                                    email_taken_issue(username1);
                                                                    break;

                                                                default:
                                                                    progressDialog.dismiss();
                                                                    ParseUser user = ParseUser.getCurrentUser();
                                                                    user.deleteEventually(new DeleteCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            Snackbar.make(findViewById(R.id.activity_splash), "Could not verify your identity. Try again later.",
                                                                                    Snackbar.LENGTH_LONG)
                                                                                    .show();
                                                                        }
                                                                    });
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                });
                                                Log.d("User:", "Saved");
                                            }
                                        },
                                        5000
                                );
                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }

            });
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    protected void login(){
        startActivity(new Intent(this, LoginActivity.class));
    }

    protected void reg(){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    protected void email_taken_issue(final String username){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reset);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        final Button reset = (Button) dialog.findViewById(R.id.btn_reset_pass);
        reset.setText("Submit");
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.reset_progress);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                final EditText inputEmail = (EditText) dialog.findViewById(R.id.input_reset_email);
                String resetEmail = inputEmail.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setBackgroundColor(getResources().getColor(white));
                progressBar.getIndeterminateDrawable().setColorFilter(getResources()
                        .getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                inputEmail.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.INVISIBLE);

                if(!resetEmail.isEmpty()){

                    //Validate email
                    String emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
                    // Compare the regex with the email address
                    Pattern pattern = Pattern.compile(emailRegEx);
                    Matcher matcher = pattern.matcher(resetEmail);
                    if (!matcher.find()) {
                        inputEmail.setError("Enter a valid email address.");
                    }else{
                        ParseUser user = ParseUser.getCurrentUser();
                        user.setEmail(resetEmail);
                        user.setUsername(username);
                        user.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    progressBar.setVisibility(View.GONE);
                                    inputEmail.setVisibility(View.VISIBLE);
                                    reset.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                    Snackbar.make(findViewById(R.id.activity_splash), "Verification email has been sent to your email.",
                                            Snackbar.LENGTH_LONG)
                                            .show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    inputEmail.setVisibility(View.VISIBLE);
                                    reset.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    inputEmail.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);
                    inputEmail.setError("Enter a valid email address.");
                }
            }
        });
    }

}
