package com.gohibo.album.Core;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.gohibo.album.App;
import com.gohibo.album.MainActivity;
import com.gohibo.album.helper.NotificationUtils;
import com.parse.ParsePushBroadcastReceiver;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PushReceiver extends ParsePushBroadcastReceiver {
    private final String TAG = PushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;

    private Intent parseIntent;
    private Context context;

    public PushReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        this.context = context;

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push received: " + json);

            parseIntent = intent;

            parsePushJson(context, json);

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    /**
     * Parses the push notification json
     *
     * @param context
     * @param json
     */
    private void parsePushJson(Context context, JSONObject json) {
        try {
            boolean isBackground = json.getBoolean("is_background");
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String img = data.getString("img");
            String uImg = data.getString("uimg");
            String message = data.getString("message");

            if (!isBackground) {
                Intent resultIntent = new Intent(context, MainActivity.class);
                resultIntent.putExtra("frag", "LikesFragment");
                showNotificationMessage(context, title, message, img, uImg, resultIntent);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }


    /**
     * Shows the notification message in the notification bar
     * If the app is in background, launches the app
     *
     * @param context
     * @param title
     * @param message
     * @param intent
     */
    private void showNotificationMessage(Context context, String title, String message, String img, String uImg, Intent intent) {

        notificationUtils = new NotificationUtils(context);
        intent.putExtras(parseIntent.getExtras());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if(img != null && uImg != null) {
            final Uri imageL= Uri.parse(img);
            final Uri uImageL = Uri.parse(uImg);
            // downloading imgs
            final BitmapImage imageI = new BitmapImage();
            imageI.getFromURI(context, imageL);
            final BitmapImage uImageI = new BitmapImage();
            uImageI.getFromURI(context, uImageL);

            final String titleL = title;
            final String messageL = message;
            final Intent intentL = intent;

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Bitmap image = imageI.getBitmap();
                            Bitmap uImage = uImageI.getBitmap();
                            notificationUtils.showNotificationMessage(titleL, messageL, image, uImage, intentL);
                        }
                    },
                    5000
            );
        }else{
            final String titleL = title;
            final String messageL = message;
            final Intent intentL = intent;
            final Bitmap image = null;
            final Bitmap uImage = null;
            notificationUtils.showNotificationMessage(titleL, messageL, image, uImage, intentL);
        }

    }
}
