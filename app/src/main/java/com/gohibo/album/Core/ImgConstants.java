package com.gohibo.album.Core;


import com.gohibo.album.helper.AspectRatio;

interface ImgConstants {

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

    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;
    public static final double PI = 3.14159d;
    public static final double FULL_CIRCLE_DEGREE = 360d;
    public static final double HALF_CIRCLE_DEGREE = 180d;
    public static final double RANGE = 256d;
    public static final int COLOR_MIN = 0x00;
    public static final int COLOR_MAX = 0xFF;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;
}