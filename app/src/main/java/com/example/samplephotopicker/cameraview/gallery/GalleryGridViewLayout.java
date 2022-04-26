package com.example.samplephotopicker.cameraview.gallery;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samplephotopicker.R;
import com.example.samplephotopicker.cameraview.RecyclerItemClickListener;
import com.example.samplephotopicker.cameraview.adapters.GalleryGridViewLayoutAdapter;
import com.example.samplephotopicker.cameraview.adapters.GalleryGridViewLayoutAdapterWithCheckBox;
import com.example.samplephotopicker.cameraview.adapters.ImageExtended;

import java.io.File;
import java.util.ArrayList;

public class GalleryGridViewLayout extends LinearLayout {

    public static final int WITHOUT_CHECKBOX = 0;
    public static final int WITH_CHECKBOX = 1;

    public static final String FROM_ALL = "ALL";

    private boolean isLastPage = false;
    private int totalItemCount, lastVisibleItem;
    private int pagingSize = 10;
    private int pagingStart = 0;
    private int preSelectedPosition = 0;

    private View rootView;

    public RecyclerView galleryGridRecyclerView;
    public RecyclerView.Adapter mGalleryAdapter;
    ArrayList<ImageExtended> ImageExtendedPagedList;

    public ArrayList<ImageExtended> mFiles; public File targetFile;
    public GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);

    public GalleryGridViewLayout(Context context) {
        super(context);
    }

    public GalleryGridViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryGridViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GalleryGridViewLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initView(int _type, int _numColumns, final String _FROM, final int _pagingSize){
        removeAllViews();

        if(_type != WITHOUT_CHECKBOX && _type != WITH_CHECKBOX) return;
        if(_numColumns > 10 || _numColumns < 0) return;
        if(_pagingSize > 50 || _pagingSize < 0) return;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.gallery_grid_view_layout_base, this, false);

        galleryGridRecyclerView = rootView.findViewById(R.id.galleryGridRecyclerView);

        //initialize variables
        if(null != mFiles) mFiles.clear();
        pagingStart = 0;
        pagingSize = _pagingSize;
        isLastPage = false;

        if(_FROM.equals(FROM_ALL)) {
            mFiles = fetchImages();
        } else{
            mFiles = fetchImagesFromDirectory(_FROM);
        }
        pagingStart += pagingSize;


        //define gridView type
        switch (_type) {
            case WITHOUT_CHECKBOX:
                mGalleryAdapter = new GalleryGridViewLayoutAdapter(getContext());
                ((GalleryGridViewLayoutAdapter) mGalleryAdapter).setItems(mFiles);
                galleryGridRecyclerView.setAdapter(mGalleryAdapter);
                break;

            case WITH_CHECKBOX:
                mGalleryAdapter = new GalleryGridViewLayoutAdapterWithCheckBox(getContext());
                ((GalleryGridViewLayoutAdapterWithCheckBox) mGalleryAdapter).setItems(mFiles);
                galleryGridRecyclerView.setAdapter(mGalleryAdapter);
                break;

            default:
                mGalleryAdapter = new GalleryGridViewLayoutAdapter(getContext());
                ((GalleryGridViewLayoutAdapter) mGalleryAdapter).setItems(mFiles);
                galleryGridRecyclerView.setAdapter(mGalleryAdapter);
        }

        gridLayoutManager = new GridLayoutManager(getContext(), _numColumns);
        galleryGridRecyclerView.setLayoutManager(gridLayoutManager);
        galleryGridRecyclerView.setHasFixedSize(true);

        //recyclerview pagination
        galleryGridRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisibleItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();
                totalItemCount = mGalleryAdapter.getItemCount() - 1;

                if(lastVisibleItem == totalItemCount && !isLastPage) {
                    if(_FROM.equals(FROM_ALL)) {
                        ImageExtendedPagedList = fetchImages();
                        mFiles.addAll(ImageExtendedPagedList);
                        pagingStart += pagingSize;
                        if(0 == ImageExtendedPagedList.size()) isLastPage = true;

                        recyclerView.post(new Runnable() {
                            @Override public void run() {
                                try {
                                    mGalleryAdapter.notifyItemRangeInserted(pagingStart, ImageExtendedPagedList.size());
                                } catch (Exception e) {
                                    mGalleryAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                    else{
                        ImageExtendedPagedList = fetchImagesFromDirectory(_FROM);
                        mFiles.addAll(ImageExtendedPagedList);
                        pagingStart += pagingSize;
                        if(0 == ImageExtendedPagedList.size()) isLastPage = true;

                        recyclerView.post(new Runnable() {
                            @Override public void run() {
                                try {
                                    mGalleryAdapter.notifyItemRangeInserted(pagingStart, ImageExtendedPagedList.size());
                                } catch (Exception e) {
                                    mGalleryAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });

        preSelectedPosition = 0;
        mFiles.get(0).setSelected(true);
        targetFile = mFiles.get(0).file;
        galleryGridRecyclerView.addOnItemTouchListener(itemClickListener);
        mGalleryAdapter.notifyDataSetChanged();

        addView(rootView);
    }

    public void addMargin(final int _margin){
        if(null != galleryGridRecyclerView){
            galleryGridRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.left = _margin; outRect.right = _margin; outRect.bottom = _margin;
                    if(parent.getChildLayoutPosition(view) >= 0 && parent.getChildLayoutPosition(view) <=4){
                        outRect.top = _margin;
                    }
                }
            });
        }
    }


    // get all images from content provider
    private ArrayList<ImageExtended> fetchImages() {
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor imageCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN
                        + " DESC LIMIT " + String.valueOf(pagingSize) + " OFFSET " + String.valueOf(pagingStart));

        ArrayList<ImageExtended> result = new ArrayList<>(imageCursor.getCount());

        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
        if (imageCursor == null) {
        } else if (imageCursor.moveToFirst()) {
            do {
                String filePath = imageCursor.getString(dataColumnIndex);
//                if(!filePath.endsWith(".gif")) {
                    try {
                        result.add(new ImageExtended(new File(filePath), false));
                    } catch (Exception e) {
                        Log.e("This file not exists : ", filePath);
                    }
//                }
            } while (imageCursor.moveToNext());
        } else {
        }
        imageCursor.close();
        return result;
    }


    // get images from specific directory of content provider
    private ArrayList<ImageExtended> fetchImagesFromDirectory(String directoryName){
        ArrayList<ImageExtended> results = new ArrayList<>();

        Cursor ImageCursor = getContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA},
                "bucket_display_name = \"" + directoryName + "\"", null,
                MediaStore.Images.ImageColumns.DATE_TAKEN
                        + " DESC LIMIT " + String.valueOf(pagingSize) + " OFFSET " + String.valueOf(pagingStart));


        if (ImageCursor.moveToFirst()) {
            int dataColumn = ImageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                String filePath = ImageCursor.getString(dataColumn);
//                if(!filePath.endsWith(".gif")) {
                    try {
                        results.add(new ImageExtended(new File(filePath), false));
                    } catch (Exception e) {
                        Log.e("This file not exists : ", filePath);
                    }
//                }
            } while (ImageCursor.moveToNext());
        }

        return results;
    }


    // get all image directories from content provider
    public ArrayList<String> getAllDirectories(){
        ArrayList<String> results = new ArrayList<>();

        Cursor ImageCursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{ MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME },
                "1) GROUP BY 1,(2", null, "MAX(datetaken) DESC");

        if (ImageCursor.moveToFirst()) {
            String bucket;
            int bucketColumn = ImageCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            do {
                bucket = ImageCursor.getString(bucketColumn);
                results.add(bucket);
            } while (ImageCursor.moveToNext());
        }

        return results;
    }

    // get content Uri from the file
    public static Uri getImageContentUriFromFile(Context context, File file){
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            targetFile = mFiles.get(position).getFile();
            mFiles.get(preSelectedPosition).setSelected(false);
            mFiles.get(position).setSelected(true);
            mGalleryAdapter.notifyItemChanged(preSelectedPosition);
            mGalleryAdapter.notifyItemChanged(position);
            preSelectedPosition = position;
        }

        @Override
        public void onLongItemClick(View view, int position) {

        }
    };

    private RecyclerItemClickListener itemClickListener = new RecyclerItemClickListener(getContext(), galleryGridRecyclerView, onItemClickListener);
}
