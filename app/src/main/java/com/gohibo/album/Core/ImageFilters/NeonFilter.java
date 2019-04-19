package com.gohibo.album.Core.ImageFilters;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class NeonFilter {
	
	static {
		System.loadLibrary("GohiboImageFilter");
	}
	

	public static Bitmap changeToNeon(Bitmap bitmap, int r, int g, int b) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		
		
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		
		int[] returnPixels = NativeFilterFunc.neonFilter(pixels, width, height, r, g, b);
		Bitmap returnBitmap = Bitmap.createBitmap(returnPixels, width, height, Bitmap.Config.ARGB_8888);
		
		return returnBitmap;
	}
}
