package com.gohibo.album.helper;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gohibo.album.R;

import android.graphics.Rect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.TRANSPARENT;

@SuppressLint("ViewConstructor")
public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private RelativeLayout view;
    private Camera.Face[] faces;
    private RectDrawing rectDrawing;
    private boolean drawingViewSet;
    private DrawingView dView;

    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            camera.cancelAutoFocus();
        }
    };

    /*private List<Camera.Size> mSupportedPreviewSizes;
    private List<Camera.Size> mSupportedPictureSizes;

    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;*/


    public ImageSurfaceView(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.camera.setPreviewDisplay(holder);
            this.camera.startPreview();

            //startFaceDetection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            surfaceHolder = holder;
            camera.setPreviewDisplay(holder);
            camera.startPreview();

            startFaceDetection();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        view.removeView(dView);
        view.removeView(rectDrawing);
        dView = null;
        rectDrawing = null;
        this.camera.stopPreview();
        this.camera.release();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            float x = event.getX();
            float y = event.getY();
            Log.d("X and Y", x + y + "");
            Rect touchRect = new Rect(
                    (int)(x - 100),
                    (int)(y - 100),
                    (int)(x + 100),
                    (int)(y + 100)
            );
            final Rect targetFocusRect = new Rect(
                    (touchRect.left + 1000) * view.getWidth() / 2000,
                    (touchRect.top + 1000) * view.getHeight() / 2000,
                    (touchRect.right + 1000) * view.getWidth() / 2000,
                    (touchRect.bottom + 1000) * view.getHeight() / 2000
            );
            doTouchFocus(targetFocusRect);
            if(drawingViewSet){
                dView.setHaveTouch(true, touchRect);
                dView.invalidate();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dView.setHaveTouch(false, new Rect(0, 0, 0, 0));
                        dView.invalidate();
                    }
                }, 3000);
            }
        }
        return false;
    }

    public void doTouchFocus(final Rect target){
        try{
            List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(target, 1000);
            focusList.add(focusArea);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setFocusAreas(focusList);
            parameters.setMeteringAreas(focusList);
            camera.setParameters(parameters);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setView(RelativeLayout frameLayout) {
        view = frameLayout;
        if(rectDrawing == null && dView == null){
            dView = new DrawingView(getContext());
            rectDrawing = new RectDrawing(getContext());
        }

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if(rectDrawing.getParent() == null && dView.getParent() == null){
            view.addView(rectDrawing, layoutParams);
            view.addView(dView, layoutParams);
        }
        rectDrawing.invalidate();
    }

    public void startFaceDetection() {
        Log.d("New", view.toString());
        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getMaxNumDetectedFaces() > 0) {
            camera.startFaceDetection();
            camera.setFaceDetectionListener(new MyFaceDetectionListener());
        }
    }

    class MyFaceDetectionListener implements Camera.FaceDetectionListener {

        @Override
        public void onFaceDetection(Camera.Face[] faces1, Camera camera) {
            if (faces1.length > 0) {
                //Log.d("FaceDetection", "Face detected:" + faces1.length + ". "+ faces1.toString());
                faces = faces1;
                rectDrawing.setHasFace(true);
            } else {
                rectDrawing.setHasFace(false);
            }
           rectDrawing.invalidate();
        }
    }
    public class RectDrawing extends View {

        Paint paint;
        boolean hasFace;

        public RectDrawing(Context context) {
            this(context, null);
        }

        public RectDrawing(Context context, AttributeSet attrs){
            this(context, attrs, 0);
        }

        public RectDrawing(Context context, AttributeSet attrs, int defStyle){
            super(context, attrs, defStyle);
            hasFace = false;
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.white));
            paint.setStrokeWidth(5);
        }

        public void setHasFace(boolean f){
            hasFace = f;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawDFRect(canvas);
        }

        private void drawDFRect(@NonNull Canvas canvas){
            if(hasFace){
                int width = getWidth();
                int height = getHeight();
                for (Camera.Face face : faces) {
                    int l = face.rect.left;
                    int r = face.rect.right;
                    int t = face.rect.top;
                    int b = face.rect.bottom;
                    int left = (l + 1000) * width / 2000;
                    int right = (r + 1000) * width / 2000;
                    int top = (t + 1000) * height / 2000;
                    int bottom = (b + 1000) * height / 2000;
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }else {
                canvas.drawColor(TRANSPARENT);
            }
        }
    }

    class DrawingView extends View{
        private boolean haveTouch = false;
        private Rect touchArea;
        private Paint paint;

        public DrawingView(Context context){
            this(context, null);
        }

        public DrawingView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public DrawingView(Context context, AttributeSet attrs, int defStyle){
            super(context, attrs, defStyle);
            paint = new Paint();
            paint.setColor(getResources().getColor(R.color.white));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4);
            haveTouch = false;
        }

        public void setHaveTouch(boolean val, Rect rect){
            haveTouch = val;
            touchArea = rect;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if(haveTouch){
                canvas.drawRect(touchArea.left, touchArea.top, touchArea.right, touchArea.bottom, paint);
            }
        }
    }

}