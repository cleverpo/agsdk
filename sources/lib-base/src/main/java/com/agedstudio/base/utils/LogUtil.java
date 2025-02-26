package com.agedstudio.base.utils;

import android.util.Log;

public class LogUtil {
    private static String TAG = "AGSDK";
    public static void i(final String tag, final String msg){
        Log.i(TAG+ "." + tag, msg);
    }

    public static void d(final String tag, final String msg){
        Log.d(TAG+ "." + tag, msg);
    }

    public static void e(final String tag, final String msg){
        Log.e(TAG+ "." + tag, msg);
    }

}
