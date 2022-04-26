package com.example.samplephotopicker.cameraview.FilterRecyclerView;

import com.zomato.photofilters.imageprocessors.Filter;

public class FilterPackItem {
    public Filter filter;
    public String description;

    public FilterPackItem(Filter filter, String description){
        this.filter = filter;
        this.description = description;
    }
}
