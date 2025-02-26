package com.agedstudio.libsdk.service;

import android.content.Context;

import com.agedstudio.base.AgedStudioSDKClass;

public class JSExceptionService extends AgedStudioSDKClass {

    private static final String TAG = JSExceptionService.class.getSimpleName();

    private static JSExceptionService ins = null;

    @Override
    public void init(Context context) {
        super.init(context);
        JSExceptionService.ins = this;
    }

    public static void logException(String location, String message, String stack) {
        AnalyticsService.reportException(location, message, stack);
    }
}
