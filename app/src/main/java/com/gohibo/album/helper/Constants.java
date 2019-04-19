package com.gohibo.album.helper;


interface Constants {

    AspectRatio DEFAULT_ASPECT_RATIO = AspectRatio.of(4, 3);

    int FACING_BACK = 0;
    int FACING_FRONT = 1;

    int FLASH_OFF = 0;
    int FLASH_ON = 1;
    int FLASH_TORCH = 2;
    int FLASH_AUTO = 3;
    int FLASH_RED_EYE = 4;

    int LANDSCAPE_90 = 90;
    int LANDSCAPE_270 = 270;
	 
	String[] styles = {"GrayScale", "Relief", "Average Blur", "Oil Painting", "Neon", "Pixelate", "Old TV", 
	                                 "Invert Color", "Block", "Aged photo", "Sharpen", "Light", "Lomo", "HDR", "Gaussian Blur",
	                                 "Soft Glow", "Sketch", "Motion Blur", "Gotham"};
}