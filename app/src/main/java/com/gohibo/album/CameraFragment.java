package com.gohibo.album;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.support.v7.widget.AppCompatImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.gohibo.album.helper.ImageSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.media.MediaRecorder.AudioSource.CAMCORDER;
import static android.media.MediaRecorder.VideoSource.CAMERA;

public class CameraFragment extends Fragment {

    private String TAG = "CameraFragment";

    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;
    private Bitmap[] finalImages = new Bitmap[10];
    private int selCam = 0;
    private String picture;
    private final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    private int takingVid = 0;
    private int i = 0;
    private View view;
    private boolean isTakingVideo = false;
    private boolean safeToTakePicture = true;
    private MediaRecorder mMediaRecorder;
    private String videoFile;
    private AppCompatImageButton captureButton;
    private RelativeLayout relLayout;
    private int mDisplayOrientation;

    private FrameLayout cameraPreviewLayout;

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);

        captureButton = view.findViewById(R.id.buttonCapture);
        final AppCompatImageButton takeVideo = view.findViewById(R.id.buttonTakeVideo);
        final AppCompatImageButton changeCam = view.findViewById(R.id.buttonChangeCamera);

        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoFile = getOutputVideoFile().getPath();
                if(takingVid == 0){
                    takingVid = 1;
                    captureButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_record));
                }else{
                    takingVid = 0;
                    if(mMediaRecorder != null){
                        if(isTakingVideo){
                            mMediaRecorder.stop();
                            Toast.makeText(getContext(), "Video Cancelled!", Toast.LENGTH_LONG).show();
                        }
                        mMediaRecorder.release();
                        camera.lock();
                    }
                    captureButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_capture));
                }
            }
        });
        changeCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selCam == 0){
                    selCam = 1;
                    mImageSurfaceView = null;
                    cameraPreviewLayout.removeAllViews();
                    camera = checkDeviceCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    //camera.setDisplayOrientation(90);
                    mImageSurfaceView = new ImageSurfaceView(getContext(), camera);
                    mImageSurfaceView.setView(relLayout);
                    cameraPreviewLayout.addView(mImageSurfaceView);
                }else{
                    selCam = 0;
                    mImageSurfaceView = null;
                    cameraPreviewLayout.removeAllViews();
                    camera = checkDeviceCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                    //camera.setDisplayOrientation(90);
                    mImageSurfaceView = new ImageSurfaceView(getContext(), camera);
                    mImageSurfaceView.setView(relLayout);
                    cameraPreviewLayout.addView(mImageSurfaceView);
                }
            }
        });


        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(takingVid == 1){
                    if(isTakingVideo){
                        isTakingVideo = false;
                        mMediaRecorder.stop();
                        showDialog(null, videoFile);
                    }else{
                        if(prepareVideoRecorder()){
                            isTakingVideo = true;
                            mMediaRecorder.start();
                        }else {
                            Toast.makeText(getContext(), "An error occurred.", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    if(safeToTakePicture){
                        camera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {

                                safeToTakePicture = false;
                                File pictureFile = getOutputMediaFile();
                                camera.startPreview();

                                if (pictureFile == null) {
                                    //no path to picture, return
                                    safeToTakePicture = true;
                                    return;
                                }

                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                if(selCam == 1){
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(-1, 1, 0, 0);
                                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                }

                                try {
                                    FileOutputStream fos = new FileOutputStream(pictureFile);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    fos.close();
                                } catch (FileNotFoundException e1) {
                                    e1.printStackTrace();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }

                                if(bitmap==null){
                                    Toast.makeText(getContext(), "Captured image is empty", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(i == 10){
                                    Toast.makeText(getContext(), "Could not capture! You can take only ten pictures in one post.", Toast.LENGTH_LONG).show();
                                    safeToTakePicture = true;
                                }else{
                                    finalImages[i] = bitmap;
                                    Toast.makeText(getContext(), "Captured", Toast.LENGTH_LONG).show();
                                    showDialog(finalImages[i], null);
                                    i = i+1;
                                    safeToTakePicture = true;
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "Could not capture!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    private boolean prepareVideoRecorder(){
        takingVid = 1;
        mMediaRecorder = new MediaRecorder();
        camera.unlock();
        boolean prepared = false;
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setAudioSource(CAMCORDER);
        mMediaRecorder.setVideoSource(CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        mMediaRecorder.setPreviewDisplay(mImageSurfaceView.getHolder().getSurface());
        mMediaRecorder.setMaxDuration(30000);
        mMediaRecorder.setOutputFile(videoFile);
        mMediaRecorder.setOrientationHint(90);
        try {
            mMediaRecorder.prepare();
            prepared = true;
        } catch (IOException e) {
            e.printStackTrace();
            prepared = false;
        }
        return prepared;
    }

    private File getOutputMediaFile() {

        /* yyyy-MM-dd'T'HH:mm:ss.SSSZ */
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
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

        picture = mediaFile.getPath();

        return mediaFile;

    }
    private File getOutputVideoFile() {

        /* yyyy-MM-dd'T'HH:mm:ss.SSSZ */
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(new Date());
        File mediaStorageDir = getContext().getDir("Stories Album", Context.MODE_WORLD_WRITEABLE);
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdir()){
                return null;
            }
        }
        // file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "StoriesAlbum_vid_" + timeStamp + ".mp4");

        Log.d("CameraFragment", mediaFile.getPath());
        return mediaFile;
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageSurfaceView = null;
        cameraPreviewLayout.removeAllViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(camera != null){
            mImageSurfaceView = null;
            cameraPreviewLayout.removeAllViews();
            camera = checkDeviceCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            //camera.setDisplayOrientation(90);
            mImageSurfaceView = new ImageSurfaceView(getContext(), camera);
            mImageSurfaceView.setView(relLayout);
            cameraPreviewLayout.addView(mImageSurfaceView);
            Log.d(getContext().toString(), finalImages.toString());
        }else{
            relLayout = view.findViewById(R.id.fragment_camera);
            cameraPreviewLayout = view.findViewById(R.id.camera_preview);
            camera = checkDeviceCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            //camera.setDisplayOrientation(90);
            mImageSurfaceView = new ImageSurfaceView(getContext(), camera);
            mImageSurfaceView.setView(relLayout);
            cameraPreviewLayout.addView(mImageSurfaceView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraPreviewLayout.removeAllViews();
        mImageSurfaceView = null;
    }

    private Camera checkDeviceCamera(int camId){
        DisplayOrientationDetector displayOrientationDetector = new DisplayOrientationDetector(getActivity()) {
            @Override
            public void onDisplayOrientationChanged(int displayOrientation) {
                mDisplayOrientation = displayOrientation;
                Log.d(TAG, displayOrientation + "Camera Got by T");
            }
        };
        Display display = ((WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        displayOrientationDetector.enable(display);

        mDisplayOrientation = display.getRotation();

        Camera mCamera = null;
        try {
            mCamera = Camera.open(camId);
            Log.d(TAG, "default orientation:"+cameraInfo.orientation+"");
            Camera.Parameters params = mCamera.getParameters();
            params.setExposureCompensation(params.getMaxExposureCompensation());
            if(params.isAutoExposureLockSupported()){
                params.setAutoExposureLock(false);
            }
            params.setPictureSize(1024, 1024);
            //params.setPreviewSize(512,512);
            params.setRotation(calcCameraRotation(270));
            params.setAutoWhiteBalanceLock(true);
            params.setJpegQuality(100);
            mCamera.setParameters(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCamera.startFaceDetection();
        mCamera.setFaceDetectionListener(new MyFaceDetectionListener());
        mCamera.setDisplayOrientation(calcDisplayOrientation(270));
        return mCamera;
    }

    private void showDialog(Bitmap bmp, String videoFile){
        final Dialog dialog = new Dialog(getContext());
        final boolean isVideo;
        if(videoFile == null){
            isVideo = false;
        }else {
            isVideo = true;
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(isVideo){
            dialog.setContentView(R.layout.dialog_selected_video);
        }else {
            dialog.setContentView(R.layout.dialog_captured_image);
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        AppCompatImageView imageView;
        AppCompatButton delete;
        AppCompatButton take;
        AppCompatButton takeNew;
        VideoView videoView = null;
        if(isVideo){
            videoView = dialog.findViewById(R.id.selected_vid);
            final MediaController mediaController= new MediaController(getContext());
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(videoFile));
            videoView.start();
            delete = dialog.findViewById(R.id.button_delete_img);
            take = dialog.findViewById(R.id.button_take_img);
            delete.setText(getResources().getString(R.string.cancel));
        }else {
            imageView = dialog.findViewById(R.id.captured_image);
            delete = dialog.findViewById(R.id.button_delete_img);
            take = dialog.findViewById(R.id.button_take_img);
            takeNew = dialog.findViewById(R.id.button_take_another_img);
            takeNew.setVisibility(View.GONE);
            imageView.setImageBitmap(bmp);
        }
        final VideoView videoView1 = videoView;

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVideo){
                    i = i - 1;
                }
                dialog.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.image_removed), Toast.LENGTH_LONG).show();
            }
        });

        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getContext(), "take clicked!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), EditPhotoActivity.class);
                intent.putExtra("Bitmap", picture);
                startActivity(intent);
            }
        });

        /*takeNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(getContext(), "Take your new picture!", Toast.LENGTH_LONG).show();
            }
        });*/
    }


    private static Bitmap rotateImage(Bitmap bitmap, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private int calcDisplayOrientation(int screenOrientationDegrees) {
        if (selCam == 1) {
            return (360 - (cameraInfo.orientation + screenOrientationDegrees) % 360) % 360;
        } else {  // back-facing
            return (cameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
    }

    private int calcCameraRotation(int screenOrientationDegrees) {
        if (selCam == 1) {
            return (cameraInfo.orientation + screenOrientationDegrees) % 360;
        } else {  // back-facing
            final int landscapeFlip = isLandscape(screenOrientationDegrees) ? 180 : 0;
            return (cameraInfo.orientation + screenOrientationDegrees + landscapeFlip) % 360;
        }
    }

    private boolean isLandscape(int orientationDegrees) {
        return (orientationDegrees == Constants.LANDSCAPE_90 ||
                orientationDegrees == Constants.LANDSCAPE_270);
    }

    class MyFaceDetectionListener implements Camera.FaceDetectionListener{

        public List<Rect> faceRects = new ArrayList<Rect>();

        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if(faces.length > 0){
                Log.d("FaceDetection", "Face detected:" + faces.length + ". "+ faces.toString());
                for (Camera.Face face : faces) {
                    int left = face.rect.left;
                    int right = face.rect.right;
                    int top = face.rect.top;
                    int bottom = face.rect.bottom;

                    Rect uRect = new Rect(left, top, right, bottom);
                    RectangleSurfaceView mImageSurfaceView1 = new RectangleSurfaceView(getContext(), left, top, right, bottom);

                    SurfaceHolder rect = mImageSurfaceView1.getHolder();
                    rect.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                    faceRects.add(uRect);
                    mImageSurfaceView1.setZOrderMediaOverlay(true);
                    cameraPreviewLayout.addView(mImageSurfaceView1);
                }
            }
        }
    }


    class RectangleSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

        SurfaceHolder surfaceHolder;

        float left;
        float top;
        float right;
        float bottom;

        public RectangleSurfaceView(Context context, float leftf, float topf, float rightf, float bottomf) {
            super(context);
            surfaceHolder = getHolder();
            left = leftf;
            top = topf;
            right = rightf;
            bottom = bottomf;
        }


        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            surfaceHolder = holder;
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(android.R.color.holo_green_dark));
            paint.setStrokeWidth(3);
            canvas.drawRect(left, top, right, bottom, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            surfaceHolder = holder;
            Canvas canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(android.R.color.holo_green_dark));
            paint.setStrokeWidth(3);
            canvas.drawRect(left, top, right, bottom, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            surfaceHolder = null;
        }

    }

}