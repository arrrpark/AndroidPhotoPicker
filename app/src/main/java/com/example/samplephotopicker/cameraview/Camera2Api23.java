package com.example.samplephotopicker.cameraview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.params.StreamConfigurationMap;

import com.example.samplephotopicker.cameraview.base.PreviewImpl;
import com.example.samplephotopicker.cameraview.base.Size;
import com.example.samplephotopicker.cameraview.base.SizeMap;


@TargetApi(23)
public class Camera2Api23 extends Camera2 {

    public int getDisplayOrientation() {
        return mDisplayOrientation;
    }

    public Camera2Api23(Callback callback, PreviewImpl preview, Context context) {
        super(callback, preview, context);
    }

    @Override
    protected void collectPictureSizes(SizeMap sizes, StreamConfigurationMap map) {
        // Try to get hi-res output sizes
        android.util.Size[] outputSizes = map.getHighResolutionOutputSizes(ImageFormat.JPEG);
        if (outputSizes != null) {
            for (android.util.Size size : map.getHighResolutionOutputSizes(ImageFormat.JPEG)) {
                sizes.add(new Size(size.getWidth(), size.getHeight()));
            }
        }
        if (sizes.isEmpty()) {
            super.collectPictureSizes(sizes, map);
        }
    }

}
