package com.agedstudio.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import org.json.JSONObject;

public interface AgedStudioSDKInterface {
    void init(Context context);
    void setGLSurfaceView(GLSurfaceView view);
    void onResume();
    void onPause();
    void onDestroy();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void onNewIntent(Intent intent);
    void onRestart();
    void onStop();
    void onBackPressed();
    void onConfigurationChanged(Configuration newConfig);
    void onRestoreInstanceState(Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    void onStart();
    void onLowMemory();
}
