package com.gohibo.album;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Parse.enableLocalDatastore(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("671b9097b50b89735d849e54144c09fd9841f6d8")
                .clientKey("58c1d6385c0b771f14e6535a69b89d6043076a37")
                .server("http://52.14.89.53:80/parse/")
                .build()
        );
        ParseFacebookUtils.initialize(this);

    }


    @org.jetbrains.annotations.Contract(pure = true)
    public static Context getAppContext(){
        return context;
    }
}