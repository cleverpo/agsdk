package com.agedstudio.base;

import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.agedstudio.base.utils.LogUtil;

import java.util.Hashtable;
import java.util.Map;

public class AgedStudioSDKWrapper {
    private static final String TAG = AgedStudioSDKWrapper.class.getSimpleName();

    private Context mainActive = null;
    private static AgedStudioSDKWrapper mInstace = null;
    private Hashtable<String, AgedStudioSDKClass> mSdkClasses = new Hashtable<>();

    public static AgedStudioSDKWrapper getInstance() {
        if (null == mInstace) {
            mInstace = new AgedStudioSDKWrapper();
        }
        return mInstace;
    }

    public void init(Context context) {
        LogUtil.i(TAG, "init");
        this.mainActive = context;
    }

    public Context getContext() {
        return this.mainActive;
    }

    public void addSDKClass(String name, AgedStudioSDKClass sdkClass) {
        if (this.mSdkClasses.containsKey(name)) {
            return;
        }
        if (null != sdkClass) {
            this.mSdkClasses.put(name, sdkClass);
        }
    }

    public AgedStudioSDKClass getSDKClass(String name) {
        return this.mSdkClasses.get(name);
    }

    public void setGLSurfaceView(GLSurfaceView view, Context context) {
        this.mainActive = context;
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().setGLSurfaceView(view);
        }
    }

    public void onResume() {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onResume();
        }
    }

    public void onPause() {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onPause();
        }
    }

    public void onDestroy() {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onDestroy();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onNewIntent(Intent intent) {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onNewIntent(intent);
        }
    }

    public void onRestart() {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onRestart();
        }
    }

    public void onStop() {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onStop();
        }
    }

    public void onBackPressed() {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onBackPressed();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onConfigurationChanged(newConfig);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onRestoreInstanceState(savedInstanceState);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onSaveInstanceState(outState);
        }
    }

    public void onStart() {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onStart();
        }
    }

    public void onLowMemory() {
        for (Map.Entry<String, AgedStudioSDKClass> entry : this.mSdkClasses.entrySet()) {
            entry.getValue().onLowMemory();
        }
    }
}

