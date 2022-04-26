package com.example.samplephotopicker.cameraview.FilterRecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.samplephotopicker.R;

public class FilterViewHolder extends RecyclerView.ViewHolder{
    public ImageView filterPreviewImage;
    public TextView filterPreviewTextView;

    public FilterViewHolder(View itemView) {
        super(itemView);
        filterPreviewImage = itemView.findViewById(R.id.filterPreviewImage);
        filterPreviewTextView = itemView.findViewById(R.id.filterPreviewTextView);
    }
}
