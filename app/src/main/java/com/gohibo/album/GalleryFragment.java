package com.gohibo.album;

import com.bumptech.glide.Glide;
import com.gohibo.album.helper.GalleryFunc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GalleryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private GridView galleryGridView;
    private ArrayList<HashMap<String, String >> albumList = new ArrayList<HashMap<String, String>>();
    private AppCompatSpinner spinnerFolders;
    private List<String> albums = new ArrayList<String>();

    private Img userImg;

    private ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    private String album_name = "";

    private RecyclerView selectedImagesView;

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        spinnerFolders = view.findViewById(R.id.albums_chooser);
        spinnerFolders.setOnItemSelectedListener(this);

        galleryGridView = view.findViewById(R.id.images_container_gallery);
        selectedImagesView = view.findViewById(R.id.selected_images_container);
        selectedImagesView.setHasFixedSize(true);

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        selectedImagesView.setLayoutManager(MyLayoutManager);

        userImg = new Img();

        selectedImagesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadAlbum loadAlbum = new LoadAlbum();
        loadAlbum.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        album_name = parent.getItemAtPosition(position).toString();

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = GalleryFunc.convertDpToPixel(dp, getContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        LoadAlbumImages loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean isVideo(String uri){
        String mimeType = URLConnection.guessContentTypeFromName(uri);
        return mimeType != null && mimeType.startsWith("video");
    }

    class LoadAlbum extends AsyncTask<String, Void, String >{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            String xml = "";
            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;

            Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_MODIFIED};
            Cursor cursorExternal = getActivity()
                    .getContentResolver()
                    .query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null);

            Cursor cursorInternal = getActivity()
                    .getContentResolver()
                    .query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null);

            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});
            int i =0;
            while (cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                countPhoto = GalleryFunc.getCount(getContext(), album);
                albums.add(i, album);
                albumList.add(GalleryFunc.mappingInbox(album, path, timestamp, GalleryFunc.converToTime(timestamp), countPhoto));
                i++;
            }
            albums.add(i, "Videos");
            cursor.close();
            //Collections.sort(albumList, new MapComparator(GalleryFunc.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, albums);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerFolders.setAdapter(dataAdapter);
            spinnerFolders.setSelection(0, true);
        }
    }

    class LoadAlbumImages extends AsyncTask<String, Void, String> {

        private Boolean soundClicked = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;

            if(!album_name.equals("Videos")){
                Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

                String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

                Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
                Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
                Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});
                while (cursor.moveToNext()) {

                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                    album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));

                    imageList.add(GalleryFunc.mappingInbox(album, path, timestamp, GalleryFunc.converToTime(timestamp), null));
                }
                cursor.close();
                Collections.sort(imageList, new MapComparator(GalleryFunc.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            }else{
                Uri uriExternal = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                Uri uriInternal = android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI;

                String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

                Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null);
                Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name", null, null);
                Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});
                while (cursor.moveToNext()) {

                    path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                    album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));

                    imageList.add(GalleryFunc.mappingInbox(album, path, timestamp, GalleryFunc.converToTime(timestamp), null));
                }
                cursor.close();
                Collections.sort(imageList, new MapComparator(GalleryFunc.KEY_TIMESTAMP, "dsc"));
            }

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            SingleAlbumAdapter adapter = new SingleAlbumAdapter(getActivity(), imageList);
            galleryGridView.setAdapter(adapter);

            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if(userImg.getCount() <= 10){
                        if(!userImg.checkIfAlreadyExists(imageList.get(+position).get(GalleryFunc.KEY_PATH))){
                            userImg.addImg(imageList.get(+position).get(GalleryFunc.KEY_PATH));
                            showDialog(imageList.get(+position).get(GalleryFunc.KEY_PATH));
                        }else {
                            Toast.makeText(getContext(), "Image Already Selected!", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getContext(), "You can only select and upload ten photos at a time!", Toast.LENGTH_LONG).show();
                    }
                }
            });

            galleryGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if(userImg.getCount() <= 10){
                        if(!userImg.checkIfAlreadyExists(imageList.get(+position).get(GalleryFunc.KEY_PATH))){
                            userImg.addImg(imageList.get(+position).get(GalleryFunc.KEY_PATH));
                        }else {
                            Toast.makeText(getContext(), "Image Already Selected!", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getContext(), "You can only select and upload ten photos at a time!", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });

        }

        private void showDialog(final String file){
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            if(!isVideo(file)){
                dialog.setContentView(R.layout.dialog_captured_image);
            }else{
                dialog.setContentView(R.layout.dialog_selected_video);
            }

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            if(!isVideo(file)){

                AppCompatTextView tv = dialog.findViewById(R.id.captured_img_tV);
                AppCompatImageView imageView = dialog.findViewById(R.id.captured_image);
                AppCompatButton delete = dialog.findViewById(R.id.button_delete_img);
                AppCompatButton take = dialog.findViewById(R.id.button_take_img);
                AppCompatButton takeNew = dialog.findViewById(R.id.button_take_another_img);

                delete.setText(getResources().getString(R.string.remove));
                tv.setText(getResources().getString(R.string.selected_media));
                if(!isVideo(file)){
                    Glide.with(getContext())
                            .load(new File(file)) // Uri of the picture
                            .into(imageView);
                }else{
                    imageView.setVisibility(View.GONE);
                }

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        userImg.removeImg(userImg.getPosition(file));
                        Toast.makeText(getContext(), getResources().getString(R.string.image_removed), Toast.LENGTH_LONG).show();
                    }
                });

                take.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(getContext(), EditPhotoActivity.class);
                        intent.putExtra("Bitmap", userImg.getLastInsertedFile());
                        startActivity(intent);
                        dialog.dismiss();
                        //Toast.makeText(getContext(), "take clicked!", Toast.LENGTH_LONG).show();
                    }
                });

                takeNew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(userImg.getCount() == 10){
                            Toast.makeText(getContext(), "Hey, You can't add more than 10 photos!", Toast.LENGTH_LONG).show();
                        }else{
                            dialog.dismiss();
                        }
                        //Toast.makeText(getContext(), "Take your new picture!", Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                AppCompatTextView tv = dialog.findViewById(R.id.captured_img_tV);
                final VideoView videoView = dialog.findViewById(R.id.selected_vid);
                AppCompatButton delete = dialog.findViewById(R.id.button_delete_img);
                AppCompatButton take = dialog.findViewById(R.id.button_take_img);
                final AppCompatImageButton switcher = dialog.findViewById(R.id.switch_mute_sound);

                final MediaController mediaController= new MediaController(getContext());
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(Uri.parse(file));
                videoView.requestFocus();
                videoView.start();

                switcher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!soundClicked){
                            switcher.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
                        }else{
                            switcher.setImageDrawable(getResources().getDrawable(R.drawable.ic_sound));
                        }
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        userImg.removeImg(userImg.getPosition(file));
                        Toast.makeText(getContext(), getResources().getString(R.string.image_removed), Toast.LENGTH_LONG).show();
                    }
                });

                take.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), EditVideoActivity.class);
                        intent.putExtra("files", userImg.getLastInsertedFile());
                        startActivity(intent);
                        userImg.clear();
                    }
                });
            }
        }
    }

    private class Img{

        ArrayList<SelectedImagesHelper> listFiles = new ArrayList<SelectedImagesHelper>();
        private int filesCount = 0;
        private SelectedImagesAdapter adapter;
        private Boolean init = false;

        private Img(){

        }

        private void addImg(String uri){
            filesCount = filesCount + 1;
            if(!init){
                listFiles.clear();
                SelectedImagesHelper item = new SelectedImagesHelper();
                item.setImageResourceId(uri);
                item.setIsturned(0);
                listFiles.add(item);
                adapter = new SelectedImagesAdapter(listFiles, getContext());
                selectedImagesView.setAdapter(adapter);
                selectedImagesView.invalidate();
                init = true;
            }else{
                SelectedImagesHelper item = new SelectedImagesHelper();
                item.setImageResourceId(uri);
                item.setIsturned(0);
                listFiles.add(item);
                adapter = new SelectedImagesAdapter(listFiles, getContext());
                selectedImagesView.setAdapter(adapter);
                selectedImagesView.invalidate();
            }
        }

        private void removeImg(int position){
            listFiles.remove(position);

            adapter = new SelectedImagesAdapter(listFiles, getContext());
            selectedImagesView.setAdapter(adapter);
            selectedImagesView.invalidate();
            filesCount = filesCount - 1;
        }

        private int getPosition(String uri){
            int position = 10;
            SelectedImagesHelper item = new SelectedImagesHelper();
            for(int i = 0; i < listFiles.size(); i++ ) {
                item = listFiles.get(i);
                if(item.getImageResourceId() != null && item.getImageResourceId().equals(uri)){
                    position = i;
                }
            }
            return position;
        }

        private String getLastInsertedFile(){
            SelectedImagesHelper item = new SelectedImagesHelper();
            item = listFiles.get(listFiles.size() - 1);
            return item.getImageResourceId();
        }

        private String[] getFiles(){
            String[] files = new String[10];
            SelectedImagesHelper item = new SelectedImagesHelper();
            for(int i = 0; i <= 9; i++ ) {
                item = listFiles.get(i);
                files[i] = item.getImageResourceId();
            }
            return files;
        }

        private int getCount(){
            return filesCount;
        }

        private void clear(){
            init = false;
            filesCount = 0;
            listFiles.clear();
            adapter = new SelectedImagesAdapter(listFiles, getContext());
            selectedImagesView.setAdapter(adapter);
            selectedImagesView.invalidate();
        }

        private Boolean checkIfAlreadyExists(String uri){
            SelectedImagesHelper item = new SelectedImagesHelper();
            Boolean val = false;
            for(int i = 0; i < listFiles.size(); i++ ) {
                item = listFiles.get(i);
                if(item.getImageResourceId() != null && item.getImageResourceId().equals(uri)){
                    val = true;
                }
            }
            return val;
        }
    }
}

class SingleAlbumAdapter extends BaseAdapter {
    private FragmentActivity activity;
    private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

    public SingleAlbumAdapter(FragmentActivity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
    }

    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SingleAlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new SingleAlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.single_album_row, parent, false);

            holder.galleryImage = convertView.findViewById(R.id.galleryImage);

            convertView.setTag(holder);
        } else {
            holder = (SingleAlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {
            Glide.with(activity)
                    .load(new File(song.get(GalleryFunc.KEY_PATH))) // Uri of the picture
                    .into(holder.galleryImage);
        } catch (Exception e) {
            Toast.makeText(activity, "Error occurred, Please restart your phone!", Toast.LENGTH_LONG).show();
        }
        return convertView;
    }
}


class SingleAlbumViewHolder {
    ImageView galleryImage;
}

class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesViewHolder> {
    private ArrayList<SelectedImagesHelper> list;

    private Context context;

    //save the context recievied via constructor in a local variable

    public SelectedImagesAdapter(ArrayList<SelectedImagesHelper> data, Context context) {
        list = data;
        this.context = context;
    }

    @Override
    public SelectedImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_selected_photo, parent, false);
        SelectedImagesViewHolder holder = new SelectedImagesViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final SelectedImagesViewHolder holder, int position) {

        ImageView imageView = holder.coverImageView;

        if(list.get(position) != null){
            if(!GalleryFunc.isVideo(list.get(position).getImageResourceId())){
                Glide.with(context)
                        .load(list.get(position).getImageResourceId())
                        .into(imageView);
            }else{

            }
            //holder.coverImageView.setTag(list.get(position).getImageResourceId());
        }else{
            notifyItemRemoved(position);
        }
        //holder.coverImageView.setImageResource(list.get(position).getImageResourceId());

    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}

class SelectedImagesViewHolder extends RecyclerView.ViewHolder {

    ImageView coverImageView;

    SelectedImagesViewHolder(View v) {
        super(v);
        coverImageView = v.findViewById(R.id.selected_img);
    }
}

class SelectedImagesHelper {
    String imageResourceId;
    int isturned;

    int getIsturned() {
        return isturned;
    }
    void setIsturned(int isturned) {
        this.isturned = isturned;
    }
    String getImageResourceId() {
        return imageResourceId;
    }
    void setImageResourceId(String imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
