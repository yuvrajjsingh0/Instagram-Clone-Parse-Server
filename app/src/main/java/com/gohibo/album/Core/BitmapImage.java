package com.gohibo.album.Core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;



public class BitmapImage {

    static Bitmap bitmp;
    static Context c;

    @SuppressLint("StaticFieldLeak")
    void getFromURI(final Context context, final Uri uri){
        c = context;
                    new AsyncTask<FutureTarget<Bitmap>, Void, Bitmap>() {
                        @Override protected Bitmap doInBackground(FutureTarget<Bitmap>... params) {
                            Log.d("C:", "Working3");
                            try {
                                bitmp = Glide.with(c).load(uri.toString()).asBitmap().into(500, 500).get();
                                Log.d("C:", "Working2");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            try {
                                return params[0].get();
                            } catch (Exception ex) {
                                return null;
                            }
                        }
                        @Override protected void onPostExecute(Bitmap bitmap) {
                            if(bitmap == null) return;
                            try {
                                bitmp = Glide.with(c).load(uri.toString()).asBitmap().into(500, 500).get();
                                Log.d("C:", "Working2");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            Log.d("C:", "Working1");
                            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,   "Art_"+System.currentTimeMillis(), "Beschrijving");
                        }
                    }.execute();

    }
    public Bitmap getBitmap(){
        //Log.d("C:", "Working");
        return bitmp;
    }

    public static String createNewImage(){
        /* yyyy-MM-dd'T'HH:mm:ss.SSSZ */
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Stories Album");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "StoriesAlbum_img_" + timeStamp + ".jpg");

        return mediaFile.getPath();
    }

}
