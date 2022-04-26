package com.example.samplephotopicker.cameraview.FilterRecyclerView;

import jp.co.cyberagent.android.gpuimage.GPUImage;

public class GPUImageItem {
    public GPUImage gpuImage;
    public String description;

    public GPUImageItem(GPUImage gpuImage, String description){
        this.gpuImage = gpuImage;
        this.description = description;
    }
}
