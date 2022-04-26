package com.example.samplephotopicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.samplephotopicker.cameraview.gallery.GalleryGridViewLayout;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ImageCropLayout extends LinearLayout {
    private View baseView;
    private MainActivity baseActivity;
    private CropImageView icv_cropImageView;
    private TextView goNextStep;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageCropLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflateView();
    }

    public ImageCropLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView();
    }

    public ImageCropLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView();
    }

    public ImageCropLayout(Context context) {
        super(context);
        inflateView();
    }

    private void inflateView(){
        removeAllViews();
        baseActivity = (MainActivity)getContext();
        LayoutInflater inflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        baseView = inflater.inflate(R.layout.image_crop_view, this, false);
        icv_cropImageView = baseView.findViewById(R.id.icv_cropImageView);
        goNextStep = baseView.findViewById(R.id.goNextStep);

        final DisplayMetrics displayMetrics = baseActivity.getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = icv_cropImageView.getLayoutParams();
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.heightPixels;

        addView(baseView);

        // 갤러리에서 이미지를 얻을 경우 content uri를 세팅해주고, 카메라일경우 텍스쳐뷰에서 얻은 bitmap을 세팅해준다
        if(null == baseActivity.targetBitmap) icv_cropImageView.setImageUriAsync(GalleryGridViewLayout.getImageContentUriFromFile(baseActivity, baseActivity.targetFile));
        else icv_cropImageView.setImageBitmap(baseActivity.targetBitmap);

        goNextStep.setOnClickListener(nextStepOnClickListener);
    }

    public void onDestroy(){
        baseView = null;
        baseActivity = null;
        icv_cropImageView = null;
        goNextStep = null;
    }

    private OnClickListener nextStepOnClickListener = new OnClickListener(){
        @Override
        public void onClick(View view) {
            baseActivity.removeAllViewsSafely();
            baseActivity.croppedBitmap = icv_cropImageView.getCroppedImage();
            baseActivity.baseLinearLayout.addView(new FilterLayout(baseActivity));
        }
    };

}
