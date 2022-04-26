package com.example.samplephotopicker.cameraview.Utils;

import android.util.Log;

public class LOG {

    public static void d(String _msg) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.d(" LOG ", _msg);
    }
    public static void d(String _msg, Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.d(" LOG ", _msg, _err);
    }
    public static void d(Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.d(" LOG ", "NO MSG", _err);
    }





    public static void e(String _msg) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.e(" LOG ", _msg);
    }
    public static void e(String _msg, Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.e(" LOG ", _msg, _err);
    }
    public static void e(Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.e(" LOG ", "NO MSG", _err);
    }





    public static void i(String _msg) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.i(" LOG ", _msg);
    }
    public static void i(String _msg, Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.i(" LOG ", _msg, _err);
    }
    public static void i(Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.i(" LOG ", "NO MSG", _err);
    }





    public static void v(String _msg) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.v(" LOG ", _msg);
    }
    public static void v(String _msg, Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.v(" LOG ", _msg, _err);
    }
    public static void v(Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.v(" LOG ", "NO MSG", _err);
    }





    public static void w(String _msg) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.w(" LOG ",_msg);
    }
    public static void w(String _msg, Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.w(" LOG ", _msg, _err);
    }
    public static void w(Throwable _err) {
        if (DevelopMode.MODE.DEVELOPE != DevelopMode.mode) return;
        Log.w(" LOG ","NO MSG", _err);
    }

}
