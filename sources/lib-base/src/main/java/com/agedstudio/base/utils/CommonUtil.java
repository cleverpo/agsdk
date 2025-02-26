package com.agedstudio.base.utils;


import android.content.Context;

public class CommonUtil {
    public interface ICommonApplicationUtil{
        Context getApplicationContext();
    }

    public interface ICommonActivityUtil{
        void runOnGameThread(final Runnable runnable);
        void runOnUiThread(final Runnable runnable);
        void setKeepScreenOn(boolean on);
        void eventCallback(final String value);
        Context getActvityContext();
    }

    private static ICommonApplicationUtil _application_impl;
    private static ICommonActivityUtil _activity_impl;

    public static void initInApplication(ICommonApplicationUtil impl){
        _application_impl = impl;
    }
    public static void initInActivity(ICommonActivityUtil impl){
        _activity_impl = impl;
    }

    public static void runOnGameThread(final Runnable runnable) {
        _activity_impl.runOnGameThread(runnable);
    }

    public static void runOnUiThread(final Runnable runnable) {
        _activity_impl.runOnUiThread(runnable);
    }

    public static void eventCallback(String value) {
        _activity_impl.eventCallback(value);
    }

    public static void setKeepScreenOn(boolean on){
        _activity_impl.setKeepScreenOn(on);
    }

    public static Context getApplicationContext(){
        return _application_impl.getApplicationContext();
    }
    public static Context getActivityContext(){
        return _activity_impl.getActvityContext();
    }
}
