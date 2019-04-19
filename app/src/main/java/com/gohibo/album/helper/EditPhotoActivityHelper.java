package com.gohibo.album.helper;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.gohibo.album.Core.BitmapFilter;
import com.gohibo.album.Core.TAB;
import com.gohibo.album.R;

import java.util.ArrayList;


public class EditPhotoActivityHelper {

    private AppCompatActivity activity;
    private Bitmap bmp;
    private Bitmap bitmap;
    private Bitmap finalBitmap;
    private AppCompatImageView imageView;
    private RecyclerView recyclerView;
    private LinearLayout filterSeekContainer;
    private ArrayList<EditPhotoFiltersHelper> filtersHelper;
    private boolean isFiltersInitialized = false;
    private boolean isOptionsInitialized = false;
    private int SELECTED_TAB;
    private int SELECTED_FILTER;
    private static final int ORIGINAL = 0;
    private static final int BLUR = 1;
    private static final int GLOW = 2;
    private static final int LOMO = 3;
    private static final int NEON = 4;
    private static final int PIXELATE = 5;
    private static final int LIGHT = 6;
    private static final int MOTIONBLUR = 7;
    private static final int TV = 8;
    private static final int HDR = 9;
    private static final int OIL = 10;
    private static final int SEPIA = 11;
    private static final int INVERT = 12;

    public EditPhotoActivityHelper(AppCompatActivity appCompatActivity){
        this.activity = appCompatActivity;
    }

    private void onOptionsSelected() {
        if(!isOptionsInitialized){
            RecyclerView filterOpts = activity.findViewById(R.id.editing_opts);
            filterOpts.setVisibility(View.GONE);

            recyclerView = activity.findViewById(R.id.editing_options);
            recyclerView.setHasFixedSize(true);

            imageView = activity.findViewById(R.id.pic_to_edit);
            filterSeekContainer = activity.findViewById(R.id.seek_filter);
            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(activity);
            MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            recyclerView.setLayoutManager(MyLayoutManager);
            Filters filters = new Filters(activity, bmp);
            recyclerView.setAdapter(new EditPhotoOptionsAdapter(filters.processOptions(), activity, this));

            isFiltersInitialized = true;
        }
    }

    private void onFilterSelected() {
        if(!isFiltersInitialized){
            RecyclerView editingOptions = activity.findViewById(R.id.editing_options);
            editingOptions.setVisibility(View.GONE);

            recyclerView = activity.findViewById(R.id.editing_opts);
            recyclerView.setHasFixedSize(true);

            imageView = activity.findViewById(R.id.pic_to_edit);
            filterSeekContainer = activity.findViewById(R.id.seek_filter);
            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(activity);
            MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            recyclerView.setLayoutManager(MyLayoutManager);
            Filters filters = new Filters(activity, bmp);
            if(filtersHelper != null){
                recyclerView.setAdapter(new EditPhotoFiltersAdapter(filtersHelper, activity, this));
            }else{
                recyclerView.setAdapter(new EditPhotoFiltersAdapter(filters.processFilters(), activity, this));
            }

            isFiltersInitialized = true;
        }
    }

    public void setSelected(int i){
        switch (i){
            case TAB.TAB_FILTERS:
                SELECTED_TAB = TAB.TAB_FILTERS;
                onFilterSelected();
                break;
            case TAB.TAB_OPTIONS:
                SELECTED_TAB = TAB.TAB_OPTIONS;
                onOptionsSelected();
                break;
            default:
                break;
        }
    }

    public void deSelected(){

    }

    public void reSelected(){

    }

    public void setBaseFilterBitmap(Bitmap bitmap){
        this.bmp = bitmap;
    }

    public void onRecyclerFilterSelected(int position){
        switch (position){
            case ORIGINAL:
                imageView.setImageBitmap(bitmap);
                this.finalBitmap = bitmap;
                break;
            case BLUR:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.AVERAGE_BLUR_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case GLOW:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.SOFT_GLOW_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case LOMO:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.LOMO_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case NEON:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.NEON_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case PIXELATE:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.PIXELATE_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case OIL:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.OIL_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case LIGHT:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.LIGHT_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case MOTIONBLUR:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.MOTION_BLUR_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case INVERT:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.INVERT_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case TV:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.TV_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case HDR:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.HDR_STYLE, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            case SEPIA:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.SEPIA, bitmap);
                imageView.setImageBitmap(finalBitmap);
                break;
            default:
                break;
        }
    }

    public boolean onRecyclerFilterLongSelected(int position){
        AppCompatSeekBar seekBar = activity.findViewById(R.id.filter_seek);
        switch (position){
            case ORIGINAL:
                break;
            case BLUR:
                SELECTED_FILTER = BLUR;
                seekBar.setProgress(5);
                recyclerView.setVisibility(View.GONE);
                filterSeekContainer.setVisibility(View.VISIBLE);
                break;
            case GLOW:
                break;
            case LOMO:
                break;
            case NEON:
                break;
            case PIXELATE:
                SELECTED_FILTER = PIXELATE;
                seekBar.setProgress(10);
                recyclerView.setVisibility(View.GONE);
                filterSeekContainer.setVisibility(View.VISIBLE);
                break;
            case OIL:
                recyclerView.setVisibility(View.GONE);
                filterSeekContainer.setVisibility(View.VISIBLE);
                break;
            case LIGHT:
                break;
            case MOTIONBLUR:
                SELECTED_FILTER = MOTIONBLUR;
                seekBar.setProgress(10);
                recyclerView.setVisibility(View.GONE);
                filterSeekContainer.setVisibility(View.VISIBLE);
                break;
            case INVERT:
                break;
            case TV:
                break;
            case HDR:
                break;
            case SEPIA:
                SELECTED_FILTER = SEPIA;
                seekBar.setProgress(5);
                recyclerView.setVisibility(View.GONE);
                filterSeekContainer.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    public void setOriginBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
        this.finalBitmap = bitmap;
    }

    public Bitmap getFinalBitmap(){
        return finalBitmap;
    }

    private int getSelectedFilter(){
        return SELECTED_FILTER;
    }

    public void setPreFilters(ArrayList<EditPhotoFiltersHelper> helper){
        this.filtersHelper = helper;
    }

    public void onFilterSeekChanged(int progress){
        switch (getSelectedFilter()){
            case BLUR:
                Log.d("EditPhotoActivity", "blurring");
                progress = progress * 5;
                this.finalBitmap = Filters.applyStyle(BitmapFilter.AVERAGE_BLUR_STYLE, bitmap, progress);
                imageView.setImageBitmap(finalBitmap);
                break;
            case MOTIONBLUR:
                if(progress != 0){
                    this.finalBitmap = Filters.applyStyle(BitmapFilter.MOTION_BLUR_STYLE, bitmap, progress);
                    imageView.setImageBitmap(finalBitmap);
                }
                break;
            case GLOW:
                this.finalBitmap = Filters.applyStyle(BitmapFilter.SOFT_GLOW_STYLE, bitmap, Double.valueOf(progress));
                imageView.setImageBitmap(finalBitmap);
                break;
            case PIXELATE:
                if(progress != 0){
                    this.finalBitmap = Filters.applyStyle(BitmapFilter.PIXELATE_STYLE, bitmap, progress);
                    imageView.setImageBitmap(finalBitmap);
                }
                break;
            case SEPIA:
                if(progress != 0){
                    this.finalBitmap = Filters.applyStyle(BitmapFilter.SEPIA, bitmap, progress);
                    imageView.setImageBitmap(finalBitmap);
                }
                break;
            default:
                break;
        }
    }
}