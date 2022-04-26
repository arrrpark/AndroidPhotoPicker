package com.example.samplephotopicker;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.samplephotopicker.cameraview.FilterRecyclerView.FilterPackItem;
import com.example.samplephotopicker.cameraview.FilterRecyclerView.GPUImageItem;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSmoothToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelThresholdFilter;

public class FilterLayout extends LinearLayout {

    private View baseView;
    private MainActivity baseActivity;
    private ImageFilterView preview;
    private RecyclerView filterRecyclerView;
    private TextView back, next;
    private ArrayList<Object> filters;
    private RequestOptions imageItemOptions = new RequestOptions().override(350, 350).centerCrop().dontAnimate();
    private RelativeLayout seekBarLayout;
    private AppCompatSeekBar contrastSeekBar, saturationSeekBar, warmthSeekBar;
    private RelativeLayout layout_progress;
    private RelativeLayout layout_finish;
    private LinearLayout adViewArea;
    private Button go_home, finish_activity;
    private Button reset;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflateView();
    }

    public FilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView();
    }

    public FilterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView();
    }

    public FilterLayout(Context context) {
        super(context);
        inflateView();
    }

    private void inflateView(){
        removeAllViews();
        baseActivity = (MainActivity)getContext();
        LayoutInflater inflater = (LayoutInflater) baseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        baseView = inflater.inflate(R.layout.image_filter_view, this, false);
        preview = baseView.findViewById(R.id.preview);
        back = baseView.findViewById(R.id.back);
        next = baseView.findViewById(R.id.next);
        filterRecyclerView = baseView.findViewById(R.id.filterRecyclerView);
        seekBarLayout = baseView.findViewById(R.id.seekBarLayout);
        contrastSeekBar = baseView.findViewById(R.id.contrastSeekBar);
        saturationSeekBar = baseView.findViewById(R.id.saturationSeekBar);
        warmthSeekBar = baseView.findViewById(R.id.warmthSeekBar);
        layout_progress = baseView.findViewById(R.id.layout_progress);
        layout_finish = baseView.findViewById(R.id.layout_finish);
        adViewArea = baseView.findViewById(R.id.adViewArea);
        go_home = baseView.findViewById(R.id.go_home);
        finish_activity = baseView.findViewById(R.id.finish_activity);
        reset = baseView.findViewById(R.id.reset);

        final DisplayMetrics displayMetrics = baseActivity.getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = preview.getLayoutParams();
        layoutParams.height = displayMetrics.widthPixels;

        preview.setLayoutParams(layoutParams);
        addView(baseView);

        Glide.with(baseActivity).load(baseActivity.croppedBitmap).into(preview);
        filters = getFilters();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(baseActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterRecyclerView.setAdapter(new filterAdapter(filters));
        filterRecyclerView.setLayoutManager(linearLayoutManager);
        filterRecyclerView.getAdapter().notifyDataSetChanged();

        // Contrast, Saturation, Warmth 조절
        contrastSeekBar.setMax(100); contrastSeekBar.setOnSeekBarChangeListener(seekBarChangeListener); contrastSeekBar.setProgress(50);
        saturationSeekBar.setMax(100); saturationSeekBar.setOnSeekBarChangeListener(seekBarChangeListener); saturationSeekBar.setProgress(50);
        warmthSeekBar.setMax(100); warmthSeekBar.setOnSeekBarChangeListener(seekBarChangeListener); warmthSeekBar.setProgress(50);

        contrastSeekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        contrastSeekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        saturationSeekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        saturationSeekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        warmthSeekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        warmthSeekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarLayout.setVisibility(View.GONE);
                filterRecyclerView.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                next.setText("Next");
                contrastSeekBar.setProgress(50); saturationSeekBar.setProgress(50); warmthSeekBar.setProgress(50);
            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(View.VISIBLE == filterRecyclerView.getVisibility()) {
                    filterRecyclerView.setVisibility(View.GONE);
                    seekBarLayout.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);
                    contrastSeekBar.setProgress(50); saturationSeekBar.setProgress(50); warmthSeekBar.setProgress(50);
                    next.setText("Finish");
                } else{
                    savePicture();
                }
            }
        });

        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                contrastSeekBar.setProgress(50);
                saturationSeekBar.setProgress(50);
                warmthSeekBar.setProgress(50);
            }
        });

        layout_finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        go_home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_finish.setVisibility(View.GONE);
                baseActivity.cameraTab.performClick();
                baseActivity.croppedBitmap = null;
                baseActivity.targetBitmap = null;
            }
        });

        finish_activity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_finish.setVisibility(View.GONE);
                baseActivity.finish();
            }
        });
    }

    private class filterAdapter extends RecyclerView.Adapter <ItemViewHolder>{
        ArrayList<Object> filters;

        private filterAdapter(ArrayList<Object> filters){
            this.filters = filters;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item_view, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            holder.filterCardView.setTag(position);
            if(0 == position) {
                Glide.with(baseActivity).load(resizeBitmap(baseActivity.croppedBitmap)).apply(imageItemOptions).into(holder.filterPreviewImage);
                holder.filterPreviewTextView.setText("original");
            }else {
                if (filters.get(position) instanceof FilterPackItem) {
                    Filter filter = ((FilterPackItem) filters.get(position)).filter;
                    Bitmap filteredBitmap = resizeBitmap(baseActivity.croppedBitmap);
                    Glide.with(baseActivity).load(filter.processFilter(filteredBitmap)).apply(imageItemOptions).into(holder.filterPreviewImage);
                    holder.filterPreviewTextView.setText(((FilterPackItem) filters.get(position)).description);
                }
                if (filters.get(position) instanceof GPUImageItem){
                    GPUImage gpuImage = ((GPUImageItem) filters.get(position)).gpuImage;
                    Bitmap filteredBitmap = resizeBitmap(baseActivity.croppedBitmap);
                    gpuImage.setImage(filteredBitmap);
                    Glide.with(baseActivity).load(gpuImage.getBitmapWithFilterApplied()).apply(imageItemOptions).into(holder.filterPreviewImage);
                    holder.filterPreviewTextView.setText(((GPUImageItem) filters.get(position)).description);
                }
            }
            holder.filterCardView.setOnClickListener(onItemClickListener);
        }

        @Override
        public int getItemCount() {
            return filters.size();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder{
        CardView filterCardView;
        ImageView filterPreviewImage;
        TextView filterPreviewTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            filterCardView = itemView.findViewById(R.id.filterCardView);
            filterPreviewImage = itemView.findViewById(R.id.filterPreviewImage);
            filterPreviewTextView = itemView.findViewById(R.id.filterPreviewTextView);
        }
    }

    private ArrayList<Object> getFilters(){
        ArrayList<Object> tmpFilters = new ArrayList<>();
        tmpFilters.add(null);

        Filter tmpFilter;
        FilterPackItem tmpFilterPack;
        GPUImageItem tmpGPUImageItem;

        tmpFilter = SampleFilters.getAweStruckVibeFilter();
        tmpFilterPack = new FilterPackItem(tmpFilter, "vibe");
        tmpFilters.add(tmpFilterPack);

        tmpFilter = SampleFilters.getBlueMessFilter();
        tmpFilterPack = new FilterPackItem(tmpFilter, "melon");
        tmpFilters.add(tmpFilterPack);

        tmpFilter = SampleFilters.getStarLitFilter();
        tmpFilterPack = new FilterPackItem(tmpFilter, "starlight");
        tmpFilters.add(tmpFilterPack);

        tmpFilter = SampleFilters.getLimeStutterFilter();
        tmpFilterPack = new FilterPackItem(tmpFilter, "lime");
        tmpFilters.add(tmpFilterPack);

        tmpFilter = SampleFilters.getNightWhisperFilter();
        tmpFilterPack = new FilterPackItem(tmpFilter, "night");
        tmpFilters.add(tmpFilterPack);

        GPUImage tmpGPUImage0 = new GPUImage(baseActivity);
        tmpGPUImage0.setFilter(new GPUImageSketchFilter());
        tmpGPUImageItem = new GPUImageItem(tmpGPUImage0, "sketch");
        tmpFilters.add(tmpGPUImageItem);

        GPUImage tmpGPUImage2 = new GPUImage(baseActivity);
        tmpGPUImage2.setFilter(new GPUImageSobelThresholdFilter());
        tmpGPUImageItem = new GPUImageItem(tmpGPUImage2, "dark sketch");
        tmpFilters.add(tmpGPUImageItem);

        GPUImage tmpGPUImage1 = new GPUImage(baseActivity);
        tmpGPUImage1.setFilter(new GPUImageSmoothToonFilter());
        tmpGPUImageItem = new GPUImageItem(tmpGPUImage1, "toon");
        tmpFilters.add(tmpGPUImageItem);

        return tmpFilters;
    }

    private OnClickListener onItemClickListener = new OnClickListener(){
        @Override
        public void onClick(View view) {
            int position = (int)view.getTag();
            if(0 == position){
                Glide.with(baseActivity).load(baseActivity.croppedBitmap).into(preview);
            }
            else {

                if(filters.get(position) instanceof FilterPackItem) {
                    Filter filter = ((FilterPackItem) filters.get(position)).filter;
                    Bitmap filteredBitmap = baseActivity.croppedBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Glide.with(baseActivity).load(filter.processFilter(filteredBitmap)).into(preview);
                }

                if(filters.get(position) instanceof GPUImageItem){
                    GPUImage gpuImage = ((GPUImageItem) filters.get(position)).gpuImage;
                    Bitmap filteredBitmap = baseActivity.croppedBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    gpuImage.setImage(filteredBitmap);
                    Glide.with(baseActivity).load(gpuImage.getBitmapWithFilterApplied()).into(preview);
                }
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
            switch (seekBar.getId()){
                case R.id.contrastSeekBar:
                    preview.setContrast((float) position/50);
                    break;
                case R.id.saturationSeekBar:
                    preview.setSaturation((float) position/50);
                    break;
                case R.id.warmthSeekBar:
                    preview.setWarmth((float) position/50);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    // Bitmap을 파일로 바꿔준다
    public File saveBitmapToFile(Bitmap bitmap){
        File dirDest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() , "/filter");
        File screenshotFile;
        String takenTime = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        String fileName = "filter_"+ takenTime +".jpg";
        if (dirDest.exists()) {
            screenshotFile = new File(dirDest, fileName);
        } else {
            if (dirDest.mkdir()) {
                screenshotFile = new File(dirDest, fileName);
            } else {
                screenshotFile = null;
            }
        }
        OutputStream outputStream = null;
        if(null != screenshotFile){
            try{
                outputStream = new FileOutputStream(screenshotFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
                outputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }finally{
                if (null != outputStream){
                    try{
                        outputStream.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return screenshotFile;
    }

    public Uri addImageToGallery(ContentResolver cr, String imgType, File filepath){
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, filepath.getName());
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filepath.getName());
        values.put(MediaStore.Images.Media.DESCRIPTION, "");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + imgType);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATA, filepath.toString());

        return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private Bitmap resizeBitmap(Bitmap source){
        Bitmap result = null;
        int resizedWidth = 180;
        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
        int targetHeight = (int) (resizedWidth * aspectRatio);
        result = Bitmap.createScaledBitmap(source, resizedWidth, targetHeight, false);

        return result;
    }

    private Bitmap cutBitmap(Bitmap source, ImageFilterView imageFilterView){
        Bitmap result = null;
        RectF bounds = new RectF();
        Drawable drawable = imageFilterView.getDrawable();
        if(null != drawable){
            imageFilterView.getImageMatrix().mapRect(bounds, new RectF(drawable.getBounds()));
        }
        result = Bitmap.createBitmap(source, (int)bounds.left, (int)bounds.top, (int)(bounds.right-bounds.left), (int)(bounds.bottom-bounds.top));
        return result;
    }

    public void onDestroy(){
        baseView = null;
        baseActivity = null;
        preview = null;
        filterRecyclerView = null;
        back = null; next = null;
        filters = null;
        imageItemOptions = null;
        seekBarLayout = null;
        contrastSeekBar = null; saturationSeekBar = null; warmthSeekBar = null;
    }

    private void savePicture(){
        preview.setDrawingCacheEnabled(true);
        Bitmap bitmapToSave = preview.getDrawingCache();
        File file = saveBitmapToFile(cutBitmap(bitmapToSave, preview));
        addImageToGallery(baseActivity.getContentResolver(), ".jpg", file);
        preview.setDrawingCacheEnabled(false);
        layout_finish.setVisibility(View.VISIBLE);
    }
}
