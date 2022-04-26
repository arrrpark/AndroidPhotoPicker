package com.example.samplephotopicker.cameraview.Utils;

import android.content.Context;

/**
 * Created by SeungmyunPark on 2018-03-08.
 */

public class ScreenConverter {

    static public synchronized int DpToPx(float dp, Context context){
        if(null != context){
            float density = context.getResources().getDisplayMetrics().density;
            return Math.round(dp*density);
        }

        else{
            return 50;
        }
    }
}
