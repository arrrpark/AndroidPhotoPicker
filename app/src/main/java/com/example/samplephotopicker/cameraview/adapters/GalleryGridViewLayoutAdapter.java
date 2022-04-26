package com.example.samplephotopicker.cameraview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.samplephotopicker.R;
import com.example.samplephotopicker.cameraview.gallery.GalleryGridViewLayout;

import java.util.List;


public class GalleryGridViewLayoutAdapter extends RecyclerView.Adapter<GalleryGridViewLayoutAdapter.ViewHolder>{
    Context context;
    private List<ImageExtended> mFiles;

    // Gif 파일일 경우 작은 gridview에서는 재생하지 않는다.
    RequestOptions imageItemOptions = new RequestOptions().override(350, 350).centerCrop().dontAnimate();

    public void setItems(List<ImageExtended> items) {
        if (items != null) {
            this.mFiles = items;
            notifyDataSetChanged();
        }
    }

    public GalleryGridViewLayoutAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_grid_view_layout_recyclerview_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){

        // 체크된 것은 흐리게, 아닌 것은 그대로 표시한다
        if(mFiles.get(position).getSelected()) holder.mImageView.setAlpha(0.4f);
        else holder.mImageView.setAlpha(1.0f);

        Glide.with(context)
                .load(GalleryGridViewLayout.getImageContentUriFromFile(context, mFiles.get(position).file))
                .apply(imageItemOptions)
                .into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        if(null != mFiles) return mFiles.size();
        else return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.mImageView);
        }
    }

}
