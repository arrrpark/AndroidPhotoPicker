package com.example.samplephotopicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.samplephotopicker.cameraview.Utils.ScreenConverter;
import com.example.samplephotopicker.cameraview.gallery.GalleryGridViewLayout;

import java.util.ArrayList;

public class GalleryPickerLayout extends LinearLayout {
    private View baseView;
    private MainActivity baseActivity;
    private int notSelectedItemColor = Color.parseColor("#606569");

    private LinearLayout currentDirectoryLayout;
    private TextView currentDirectoryText;
    private ArrayList<String> directoryList;
    private LinearLayout directoryLayout;

    public GalleryGridViewLayout galleryGridViewLayout;
    public TextView goNextStep;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GalleryPickerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflateView();
    }

    public GalleryPickerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView();
    }

    public GalleryPickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView();
    }

    public GalleryPickerLayout(Context context) {
        super(context);
        inflateView();
    }

    public void inflateView(){

        removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        baseView = inflater.inflate(R.layout.gallery_picker_layout, this, false);
        baseActivity = (MainActivity)getContext();

        galleryGridViewLayout = baseView.findViewById(R.id.galleryGridViewLayout);
        currentDirectoryLayout = baseView.findViewById(R.id.currentDirectoryLayout);
        currentDirectoryText = baseView.findViewById(R.id.currentDirectoryText);
        directoryLayout = baseView.findViewById(R.id.directoryLayout);
        goNextStep = baseView.findViewById(R.id.goNextStep);

        addView(baseView);

        directoryList = galleryGridViewLayout.getAllDirectories();
        currentDirectoryText.setText("ALL");
        currentDirectoryLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(View.GONE == directoryLayout.getVisibility()) {
                    directoryLayout.setVisibility(View.VISIBLE);
                    directoryLayout.bringToFront();
                }else{
                    directoryLayout.setVisibility(View.GONE);
                }
            }
        });

        galleryGridViewLayout.initView(GalleryGridViewLayout.WITHOUT_CHECKBOX, 3, GalleryGridViewLayout.FROM_ALL, 20);
        galleryGridViewLayout.addMargin(2);
        TypedValue typedValue = new TypedValue();
        baseActivity.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);

        for(int i=0; i<directoryList.size()+1; i++){
            final TextView spinnerItemAutoSetText = new TextView(baseActivity);
            spinnerItemAutoSetText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            spinnerItemAutoSetText.setTextColor(Color.parseColor("#242424"));
            spinnerItemAutoSetText.setMaxLines(1);
            spinnerItemAutoSetText.setMaxWidth(ScreenConverter.DpToPx(180, baseActivity));
            spinnerItemAutoSetText.setEllipsize(TextUtils.TruncateAt.END);
            spinnerItemAutoSetText.setPadding(ScreenConverter.DpToPx(16.5f, baseActivity), ScreenConverter.DpToPx(16.5f, baseActivity),
                    ScreenConverter.DpToPx(16.5f, baseActivity), ScreenConverter.DpToPx(16.5f, baseActivity));
            spinnerItemAutoSetText.setBackgroundResource(typedValue.resourceId);
            spinnerItemAutoSetText.setTag(i);

            if(0 == i){
                spinnerItemAutoSetText.setText("ALL");
                spinnerItemAutoSetText.setTextColor(notSelectedItemColor);
                directoryLayout.addView(spinnerItemAutoSetText);
            }else{
                spinnerItemAutoSetText.setText(directoryList.get(i-1));
                directoryLayout.addView(spinnerItemAutoSetText);
            }

            spinnerItemAutoSetText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i=0; i<directoryLayout.getChildCount(); i++){
                        ((TextView)directoryLayout.getChildAt(i)).setTextColor(Color.parseColor("#242424"));
                    }
                    currentDirectoryText.setText(spinnerItemAutoSetText.getText().toString());
                    spinnerItemAutoSetText.setTextColor(notSelectedItemColor);
                    directoryLayout.setVisibility(View.GONE);

                    if(0 == (int)spinnerItemAutoSetText.getTag()){
                        galleryGridViewLayout.initView(galleryGridViewLayout.WITHOUT_CHECKBOX, 3,
                                galleryGridViewLayout.FROM_ALL, 20);
                        galleryGridViewLayout.addMargin(2);
                    }else{
                        galleryGridViewLayout.initView(GalleryGridViewLayout.WITHOUT_CHECKBOX, 3,
                                spinnerItemAutoSetText.getText().toString(), 20);
                        galleryGridViewLayout.addMargin(2);
                    }
                }
            });
        }

        goNextStep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.targetFile = galleryGridViewLayout.targetFile;
                baseActivity.targetBitmap = null;
                baseActivity.removeAllViewsSafely();
                baseActivity.baseLinearLayout.addView(new ImageCropLayout(baseActivity));
                baseActivity.tabLayout.setVisibility(View.GONE);
            }
        });
    }


    public void onDestroy(){
        baseView = null;
        baseActivity = null;

        currentDirectoryText = null;
        currentDirectoryLayout = null;
        directoryList = null;
        directoryLayout = null;
        goNextStep = null;
        galleryGridViewLayout = null;
    }
}
