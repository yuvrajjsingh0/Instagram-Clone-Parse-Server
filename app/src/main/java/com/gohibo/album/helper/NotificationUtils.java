package com.gohibo.album.helper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.gohibo.album.R;
import java.util.List;
import java.util.Random;


public class NotificationUtils {


    private String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, Bitmap img, Bitmap uImg, Intent intent) {
            try {
                // Check for empty push message
                if (TextUtils.isEmpty(message))
                    return;


                if (img == null && uImg == null) {
                        // notification icon
                        int icon = R.mipmap.ic_launcher;

                        int mNotificationId = new Random(1596).nextInt();

                        PendingIntent resultPendingIntent =
                                PendingIntent.getActivity(
                                        mContext,
                                        0,
                                        intent,
                                        PendingIntent.FLAG_CANCEL_CURRENT
                                );

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                                mContext);
                        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                                .setAutoCancel(true)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setStyle(new NotificationCompat.InboxStyle()
                                        .setBigContentTitle(title)
                                        .setSummaryText(message))
                                .setContentIntent(resultPendingIntent)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .build();

                        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(mNotificationId, notification);

                }

                if (img !=null && uImg != null) {
                    // notification icon
                    int icon = R.mipmap.ic_launcher;

                    int mNotificationId = 100;

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    mContext,
                                    0,
                                    intent,
                                    PendingIntent.FLAG_CANCEL_CURRENT
                            );

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                            mContext);
                    Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    //This one is same as large icon but it wont show when its expanded that's why we again setting
                                    .bigLargeIcon(uImg)
                                    //This is Big Banner image
                                    .bigPicture(img)
                                    //When Notification expanded title and content text
                                    .setBigContentTitle(title)
                                    .setSummaryText(message))
                            .setContentIntent(resultPendingIntent)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .build();

                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(mNotificationId, notification);

                }
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
    }

    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return
     */
    private static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}