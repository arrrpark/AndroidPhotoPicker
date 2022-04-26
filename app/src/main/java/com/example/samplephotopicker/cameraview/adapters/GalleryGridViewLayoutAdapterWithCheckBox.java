package com.example.samplephotopicker.cameraview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.samplephotopicker.R;

import java.util.List;


public class GalleryGridViewLayoutAdapterWithCheckBox extends
        RecyclerView.Adapter<GalleryGridViewLayoutAdapterWithCheckBox.ViewHolder>{

    Context context;
    private List<ImageExtended> mFiles;
    RequestOptions imageItemOptions = new RequestOptions().override(350, 350).centerCrop();

    public void setItems(List<ImageExtended> items) {
        if (items != null) {
            this.mFiles = items;
            notifyDataSetChanged();
        }
    }

    public GalleryGridViewLayoutAdapterWithCheckBox(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_grid_view_layout_recyclerview_item_with_checkbox, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){

        holder.mCheckBox.setChecked(mFiles.get(position).getSelected());

        Glide.with(context).load(mFiles.get(position).file).apply(imageItemOptions).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if(null != mFiles) return mFiles.size();
        else return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public CheckBox mCheckBox;

        public ViewHolder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.mImageView);
            mCheckBox = v.findViewById(R.id.mCheckBox);
        }
    }


}
