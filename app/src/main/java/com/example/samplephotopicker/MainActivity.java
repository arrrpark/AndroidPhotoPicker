package com.example.samplephotopicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.samplephotopicker.cameraview.CapturePhotoLayout;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    public GalleryPickerLayout galleryPickerLayout;
    public CapturePhotoLayout capturePhotoLayout;

    public LinearLayout baseLinearLayout;
    public LinearLayout tabLayout;
    public TextView galleryTab, cameraTab;
    public File targetFile;

    public Bitmap targetBitmap;
    public Bitmap croppedBitmap;
    public boolean isCameraFull = false;
    public boolean isCameraFront = true;

    RelativeLayout permission_layout;
    Button permission_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        baseLinearLayout = findViewById(R.id.baseLinearLayout);
        tabLayout = findViewById(R.id.tabLayout);
        cameraTab = findViewById(R.id.cameraTab);
        galleryTab = findViewById(R.id.galleryTab);
        permission_layout = findViewById(R.id.permission_layout);
        permission_button = findViewById(R.id.permission_button);

        cameraTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 마시멜로 이상일때 권한 체크
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCodes.CapturePhotoLayout);
                        return;
                    }
                }

                boolean isCapturePhotoIncluded = false;
                for(int i=0; i<baseLinearLayout.getChildCount(); i++){
                    if (baseLinearLayout.getChildAt(i) instanceof  CapturePhotoLayout)
                        isCapturePhotoIncluded = true;
                }

                if(!isCapturePhotoIncluded){
                    removeAllViewsSafely();
                    capturePhotoLayout = new CapturePhotoLayout(MainActivity.this);
                    baseLinearLayout.addView(capturePhotoLayout);
                    galleryTab.setTypeface(Typeface.DEFAULT);
                    cameraTab.setTypeface(Typeface.DEFAULT_BOLD);
                }

                tabLayout.setVisibility(View.VISIBLE);
            }
        });

        galleryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 마시멜로 이상일때 권한체크
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, RequestCodes.GalleryPickerLayout);
                        return;
                    }
                }

                boolean isGalleryIncluded = false;
                for(int i=0; i<baseLinearLayout.getChildCount(); i++) {
                    if (baseLinearLayout.getChildAt(i) instanceof GalleryPickerLayout)
                        isGalleryIncluded = true;
                }

                if(!isGalleryIncluded){
                    removeAllViewsSafely();
                    galleryPickerLayout = new GalleryPickerLayout(MainActivity.this);
                    baseLinearLayout.addView(galleryPickerLayout);
                    galleryTab.setTypeface(Typeface.DEFAULT_BOLD);
                    cameraTab.setTypeface(Typeface.DEFAULT);
                }

                tabLayout.setVisibility(View.VISIBLE);

            }
        });

        permission_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
                permission_layout.setVisibility(View.GONE);
            }
        });

        cameraTab.performClick();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(null != capturePhotoLayout) capturePhotoLayout.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            removeAllViewsSafely();
            cameraTab.performClick();
            return;
        }

        else if(baseLinearLayout.getChildAt(0) instanceof ImageCropLayout || baseLinearLayout.getChildAt(0) instanceof FilterLayout){
            if(null == targetBitmap || null == croppedBitmap){
                cameraTab.performClick();
                tabLayout.setVisibility(View.VISIBLE);
            }
        }

        else{
            if(null != capturePhotoLayout) capturePhotoLayout.onRestart();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != capturePhotoLayout) capturePhotoLayout.onDestroy();
        if(null != galleryPickerLayout) galleryPickerLayout.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(baseLinearLayout.getChildAt(0) instanceof GalleryPickerLayout ||
                baseLinearLayout.getChildAt(0) instanceof CapturePhotoLayout){
            finish();
        }
        else if(baseLinearLayout.getChildAt(0) instanceof ImageCropLayout){
            tabLayout.setVisibility(View.VISIBLE);
            ((ImageCropLayout) baseLinearLayout.getChildAt(0)).onDestroy();
            if(null != targetFile) galleryTab.performClick();
            else cameraTab.performClick();
        }
        else if(baseLinearLayout.getChildAt(0) instanceof FilterLayout) {
            ((FilterLayout) baseLinearLayout.getChildAt(0)).onDestroy();
            removeAllViewsSafely();
            baseLinearLayout.addView(new ImageCropLayout(MainActivity.this));
        }
    }

    public void removeAllViewsSafely(){
        if(null != capturePhotoLayout) {
            capturePhotoLayout.onStop();
            capturePhotoLayout.onDestroy();
        }

        baseLinearLayout.removeAllViews();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!ArrayUtils.contains(grantResults, PackageManager.PERMISSION_DENIED)){
            if(RequestCodes.CapturePhotoLayout == requestCode){
                cameraTab.performClick();
            }

            if(RequestCodes.GalleryPickerLayout == requestCode){
                galleryTab.performClick();
            }
        }

        else{
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (! shouldShowRequestPermissionRationale( permission )) {
                        permission_layout.setVisibility(View.VISIBLE);
                        permission_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permission_layout.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }
    }
}
