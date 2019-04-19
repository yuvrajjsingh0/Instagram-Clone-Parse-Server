package com.gohibo.album;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.gohibo.album.Core.ImgCrop;
import com.gohibo.album.Core.TAB;
import com.gohibo.album.helper.EditPhotoActivityHelper;
import com.gohibo.album.helper.Filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;

import static com.gohibo.album.Core.BitmapImage.createNewImage;

public class EditPhotoActivity extends AppCompatActivity {

    Bitmap bitmap;
    String croppedImage;
    Bitmap pix;
    EditPhotoActivityHelper helper;
    int xDim, yDim;
    AppCompatImageView imageView;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);

        Toolbar toolbar = findViewById(R.id.toolbar_edit_picture_single);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Getting things ready..");
        mProgressDialog.setCancelable(false);

        Intent intent = getIntent();
        if(intent.getStringExtra("Bitmap") != null){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap = BitmapFactory.decodeFile(intent.getStringExtra("Bitmap"));
            if(bitmap.getWidth() > 1000 && bitmap.getHeight() > 1000){
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
            }else if (bitmap.getWidth() > 3000 && bitmap.getHeight() > 3000){
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 3, bitmap.getHeight() / 3, true);
            }else if (bitmap.getWidth() > 5000 && bitmap.getHeight() > 5000){
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 7, bitmap.getHeight() / 7, true);
            }
            Log.d("EditPhotoActivity", intent.getStringExtra("Bitmap"));
            Log.d("EditPhotoActivity", Uri.parse(intent.getStringExtra("Bitmap")).toString());
            croppedImage = createNewImage();
            ImgCrop.of(Uri.fromFile(new File(intent.getStringExtra("Bitmap"))), Uri.fromFile(new File(croppedImage))).withMaxResultSize(1280, 1280).withAspectRatio(1280f, 1280f).start(this, ImgCrop.REQUEST_CROP);
            pix = bitmap;
            pix.compress(Bitmap.CompressFormat.JPEG, 30, out);
            pix = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        }else{
            finish();
        }

        imageView = findViewById(R.id.pic_to_edit);
        imageView.setImageBitmap(bitmap);

        helper = new EditPhotoActivityHelper(this);

        helper.setBaseFilterBitmap(pix);

        TabLayout tabLayout = findViewById(R.id.tabsEditPic);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        helper.setSelected(TAB.TAB_FILTERS);
                        break;
                    case 1:
                        helper.setSelected(TAB.TAB_OPTIONS);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                helper.deSelected();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                helper.reSelected();
            }
        });
        final RecyclerView recyclerView = findViewById(R.id.editing_opts);
        final LinearLayout filterSeekContainer = findViewById(R.id.seek_filter);
        Button closeSeekLayout = findViewById(R.id.close_seek_layout);
        closeSeekLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterSeekContainer.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        AppCompatSeekBar seekBar = findViewById(R.id.filter_seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                helper.onFilterSeekChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_photo, menu);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        xDim = imageView.getWidth();
        yDim = imageView.getHeight();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_pic_save:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == ImgCrop.REQUEST_CROP){
            Uri resultUri = ImgCrop.getOutput(data);
            Log.d("Got:::::::", "result"+resultUri);
            File file = new File(URI.create(resultUri.toString()));
            bitmap = BitmapFactory.decodeFile(file.getPath());
            helper.setOriginBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
            mProgressDialog.show();
            InitAsync initAsync = new InitAsync();
            initAsync.execute();
        }else if(resultCode == ImgCrop.RESULT_ERROR){
            Log.d("EditPhotoActivity", ImgCrop.getError(data).toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    class InitAsync extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            Filters filters = new Filters(getApplicationContext(), pix);
            helper.setPreFilters(filters.processFilters());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            helper.setSelected(TAB.TAB_FILTERS);
            mProgressDialog.dismiss();
        }
    }
}
