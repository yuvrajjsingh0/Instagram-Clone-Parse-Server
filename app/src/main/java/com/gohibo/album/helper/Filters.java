package com.gohibo.album.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.gohibo.album.Core.BitmapFilter;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

public class Filters {

    Context context;
    Bitmap bmp;

    public Filters(Context c, Bitmap bitmap){
        bmp = bitmap;
        context = c;
    }

    public ArrayList<EditPhotoFiltersHelper> processFilters(){

        ArrayList<EditPhotoFiltersHelper> filters = new ArrayList<EditPhotoFiltersHelper>();

        filters.add(0, filter("Original", bmp));
        filters.add(1, filter("Blur", applyStyle(BitmapFilter.AVERAGE_BLUR_STYLE, bmp)));
        filters.add(2, filter("Glow", applyStyle(BitmapFilter.SOFT_GLOW_STYLE, bmp)));
        filters.add(3, filter("Lomo", applyStyle(BitmapFilter.LOMO_STYLE, bmp)));
        filters.add(4, filter("Neon", applyStyle(BitmapFilter.NEON_STYLE, bmp)));
        filters.add(5, filter("Pixelate", applyStyle(BitmapFilter.PIXELATE_STYLE, bmp)));
        filters.add(6, filter("Light", applyStyle(BitmapFilter.LIGHT_STYLE, bmp)));
        filters.add(7, filter("MotionBlur", applyStyle(BitmapFilter.MOTION_BLUR_STYLE, bmp)));
        filters.add(8, filter("TV", applyStyle(BitmapFilter.TV_STYLE, bmp)));
        filters.add(9, filter("HDR", applyStyle(BitmapFilter.HDR_STYLE, bmp)));
        filters.add(10, filter("Oil", applyStyle(BitmapFilter.OIL_STYLE, bmp)));
        filters.add(11, filter("Sepia", applyStyle(BitmapFilter.SEPIA, bmp)));
        //filters.add(9, filter("Invert", applyStyle(BitmapFilter.INVERT_STYLE, bmp)));

        return filters;
    }
    public ArrayList<EditPhotoOptionsHelper> processOptions(){
        ArrayList<EditPhotoOptionsHelper> options = new ArrayList<EditPhotoOptionsHelper>();
        options.add(0, option("Sharpen", new IconicsDrawable(context).icon(FontAwesome.Icon.faw_tint).toBitmap()));
        return options;
    }

    private EditPhotoFiltersHelper filter(String name, Bitmap bmp){
        EditPhotoFiltersHelper helper = new EditPhotoFiltersHelper();
        helper.setfilterName(name);
        helper.setImageResource(bmp);
        return helper;
    }

    private EditPhotoOptionsHelper option(String name, Bitmap bmp){
        EditPhotoOptionsHelper helper = new EditPhotoOptionsHelper();
        helper.setImageResource(bmp);
        helper.setOptionName(name);
        return helper;
    }

    public static Bitmap applyStyle(int styleNo, Bitmap bitmap, Object... objects) {
        Bitmap originBitmap = bitmap;
        Bitmap changeBitmap = null;
        switch (styleNo) {
            case BitmapFilter.AVERAGE_BLUR_STYLE:
                if(objects.length >= 1 && objects[0] != null){
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.AVERAGE_BLUR_STYLE, (Integer)objects[0]); // maskSize, must odd
                }else{
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.AVERAGE_BLUR_STYLE, 20); // maskSize, must odd
                }
                break;
            case BitmapFilter.GAUSSIAN_BLUR_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.GAUSSIAN_BLUR_STYLE, 1.2); // sigma
                break;
            case BitmapFilter.SOFT_GLOW_STYLE:
                if(objects.length >= 1 && objects[0] != null){
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.SOFT_GLOW_STYLE, (Double)objects[0]/10);
                }else{
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.SOFT_GLOW_STYLE, 0.6);
                }
                break;
            case BitmapFilter.LIGHT_STYLE:
                int width = originBitmap.getWidth();
                int height = originBitmap.getHeight();
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.LIGHT_STYLE, width / 3, height / 2, width / 2);
                break;
            case BitmapFilter.LOMO_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.LOMO_STYLE,
                        (originBitmap.getWidth() / 2.0) * 95 / 100.0);
                break;
            case BitmapFilter.NEON_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.NEON_STYLE, 200, 100, 50);
                break;
            case BitmapFilter.PIXELATE_STYLE:
                if(objects.length >= 1 && objects[0] != null){
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.PIXELATE_STYLE, (Integer)objects[0]);
                }else{
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.PIXELATE_STYLE, 10);
                }
                break;
            case BitmapFilter.MOTION_BLUR_STYLE:
                if(objects.length >= 1 && objects[0] != null){
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.MOTION_BLUR_STYLE, (Integer)objects[0], 1);
                }else{
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.MOTION_BLUR_STYLE, 10, 1);
                }
                break;
            case BitmapFilter.OIL_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.OIL_STYLE, 5);
                break;
            case BitmapFilter.GOTHAM_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.GOTHAM_STYLE);
                break;
            case BitmapFilter.TV_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.TV_STYLE);
                break;
            case BitmapFilter.INVERT_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.INVERT_STYLE);
                break;
            case BitmapFilter.OLD_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.OLD_STYLE);
                break;
            case BitmapFilter.HDR_STYLE:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.HDR_STYLE);
                break;
            case BitmapFilter.SEPIA:
                if(objects.length >= 1 && objects[0] != null){
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.SEPIA, (Integer)objects[0], 1);
                }else{
                    changeBitmap = BitmapFilter.changeStyle(originBitmap, BitmapFilter.SEPIA, 10, 1);
                }
                break;
            default:
                changeBitmap = BitmapFilter.changeStyle(originBitmap, styleNo);
                break;
        }
        return changeBitmap;
    }
}
