package com.example.samplephotopicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.samplephotopicker.cameraview.CameraView;

public class CapturePhotoLayout extends LinearLayout {
    CameraView cameraView;
    View baseView;
    MainActivity baseActivity;

    ImageView reverse, screen, takePicture;

    void onSwitchCamera() {
        if (cameraView != null) {
            int facing = cameraView.getFacing();
            cameraView.setFacing(facing == CameraView.FACING_FRONT ? CameraView.FACING_BACK : CameraView.FACING_FRONT);
        }
    }

//    private CameraView.Callback mCallback = new CameraView.Callback() {
//
//        @Override
//        public void onCameraOpened(CameraView cameraView) {
//            Log.d("tag", "onCameraOpened");
//        }
//
//        @Override
//        public void onCameraClosed(CameraView cameraView) {
//            Log.d("tag", "onCameraClosed");
//        }
//
//        @Override
//        public void onPictureTaken(CameraView cameraView, final byte[] data) {
//            baseActivity.targetBitmap = ((TextureView)cameraView.getChildAt(0)).getBitmap();
//
//            // fullScreen이 아닐 경우엔 bitmap을 잘라준다
//            if(!baseActivity.isCameraFull){
//                int rectOneSide;
//                if(baseActivity.targetBitmap.getWidth() < baseActivity.targetBitmap.getHeight())
//                    rectOneSide = baseActivity.targetBitmap.getWidth();
//                else
//                    rectOneSide = baseActivity.targetBitmap.getHeight();
//                baseActivity.targetBitmap = Bitmap.createBitmap(baseActivity.targetBitmap, 0, 0, rectOneSide, rectOneSide);
//            }
//
//            baseActivity.targetFile = null;
//            baseActivity.tabLayout.setVisibility(View.GONE);
//            baseActivity.baseLinearLayout.removeAllViews();
//            baseActivity.baseLinearLayout.addView(new ImageCropLayout(baseActivity));
//            onStop();
//            onDestroy();
//        }
//    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CapturePhotoLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflateView();
    }

    public CapturePhotoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView();
    }

    public CapturePhotoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView();
    }

    public CapturePhotoLayout(Context context) {
        super(context);
        inflateView();
    }


    public void inflateView(){
        removeAllViews();
        if(null == baseActivity) baseActivity = (MainActivity)getContext();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        baseView = inflater.inflate(R.layout.capture_photo_layout, this, false);

        addView(baseView);

        cameraView = baseView.findViewById(R.id.cameraView);
        reverse = baseView.findViewById(R.id.reverse);
        screen = baseView.findViewById(R.id.screen);
        takePicture = baseView.findViewById(R.id.takePicture);

        final DisplayMetrics displayMetrics = baseActivity.getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams squareLayoutParams = cameraView.getLayoutParams();

        if(baseActivity.isCameraFull) {
            cameraView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            screen.setImageResource(R.drawable.miniscreen);
            takePicture.bringToFront();
        }
        else {
            cameraView.getLayoutParams().height = displayMetrics.widthPixels;
            screen.setImageResource(R.drawable.fullscreen);
        }
        cameraView.setLayoutParams(squareLayoutParams);

        if(baseActivity.isCameraFront) cameraView.setFacing(CameraView.FACING_FRONT);
        else cameraView.setFlash(CameraView.FACING_BACK);

        cameraView.start();
//        cameraView.addCallback(mCallback);

        reverse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSwitchCamera();
                if(cameraView.getFacing() == CameraView.FACING_FRONT) baseActivity.isCameraFront = true;
                else baseActivity.isCameraFront = false;
            }
        });

        screen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!baseActivity.isCameraFull){
                    cameraView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    cameraView.setLayoutParams(squareLayoutParams);
                    screen.setImageResource(R.drawable.miniscreen);
                    takePicture.bringToFront();
                    baseActivity.isCameraFull = true;
                }else{
                    cameraView.getLayoutParams().height = displayMetrics.widthPixels;
                    cameraView.setLayoutParams(squareLayoutParams);
                    screen.setImageResource(R.drawable.fullscreen);
                    baseActivity.isCameraFull = false;
                }
            }
        });

        takePicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                cameraView.takePicture();
                baseActivity.targetBitmap = ((TextureView)cameraView.getChildAt(0)).getBitmap();

                // fullScreen이 아닐 경우엔 bitmap을 잘라준다
                if(!baseActivity.isCameraFull){
                    int rectOneSide;
                    if(baseActivity.targetBitmap.getWidth() < baseActivity.targetBitmap.getHeight())
                        rectOneSide = baseActivity.targetBitmap.getWidth();
                    else
                        rectOneSide = baseActivity.targetBitmap.getHeight();
                    baseActivity.targetBitmap = Bitmap.createBitmap(baseActivity.targetBitmap, 0, 0, rectOneSide, rectOneSide);
                }

                baseActivity.targetFile = null;
                baseActivity.tabLayout.setVisibility(View.GONE);
                baseActivity.baseLinearLayout.removeAllViews();
                baseActivity.baseLinearLayout.addView(new ImageCropLayout(baseActivity));
                onStop();
                onDestroy();
            }
        });
    }

    public void onRestart(){
        if(null != cameraView && !cameraView.isCameraOpened()) inflateView();
    }

    public void onStop(){
        if(null != cameraView && cameraView.isCameraOpened()) cameraView.stop();
    }

    public void onDestroy(){
        cameraView = null;
        baseView = null;
        baseActivity = null;

        reverse = null; screen = null;
//        mCallback = null;

//        if(null != mBackgroundHandler){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) mBackgroundHandler.getLooper().quitSafely();
//            else mBackgroundHandler.getLooper().quit();
//            mBackgroundHandler = null;
//        }
    }

}
