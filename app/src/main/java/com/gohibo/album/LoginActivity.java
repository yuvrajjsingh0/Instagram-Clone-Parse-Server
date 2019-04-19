package com.gohibo.album;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.color.white;


public class LoginActivity extends AppCompatActivity implements View.OnKeyListener {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    protected @BindView(R.id.input_email) EditText _emailText;
    protected @BindView(R.id.input_password) EditText _passwordText;
    protected @BindView(R.id.btn_login) Button _loginButton;
    protected @BindView(R.id.link_signup) TextView _signupLink;
    protected @BindView(R.id.link_reset) TextView _resetLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _resetLink.setText("Forgot Password? Retrieve now");

        _resetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPass();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent){

        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            login();
        }

        return false;
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_DarkDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                    if(e != null){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        _loginButton.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }
            }
        });

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        /* On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();

                        progressDialog.dismiss();
                    }
                }, 3000);
        */
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("enter a valid username");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 12) {
            _passwordText.setError("between 4 and 12 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void resetPass() {
        Log.d(TAG, "PwdReset");
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
                        ParseUser.requestPasswordResetInBackground(resetEmail, new RequestPasswordResetCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    progressBar.setVisibility(View.GONE);
                                    inputEmail.setVisibility(View.VISIBLE);
                                    reset.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                    Snackbar.make(findViewById(R.id.activity_login), "Verification email has been sent to your email.",
                                            Snackbar.LENGTH_LONG)
                                            .show();
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