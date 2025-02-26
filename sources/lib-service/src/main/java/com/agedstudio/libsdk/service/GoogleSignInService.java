package com.agedstudio.libsdk.service;

import android.content.Context;
import android.content.Intent;

import com.agedstudio.base.AbstractSignInApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class GoogleSignInService extends AgedStudioSDKClass {
    private static final String TAG = GoogleSignInService.class.getSimpleName();

    private static GoogleSignInService ins = null;

    private AbstractSignInApi mImp = null;

    @Override
    public void init(Context context) {
        super.init(context);
        LogUtil.d(TAG, "init");
        GoogleSignInService.ins = this;
    }

    public void initBussiness(AbstractSignInApi api) {
        this.mImp = api;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mImp) {
            mImp.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void signIn() {
        if (null != GoogleSignInService.ins.mImp) {
            GoogleSignInService.ins.mImp.signIn((success, msg) -> {
                CommonUtil.runOnGameThread(() -> {
                    String callbackName = success ? AGEvents.OnSignInSuccess : AGEvents.OnSignInFail;
                    CommonUtil.eventCallback(callbackName + "('" + msg + "');");
                });
            });
        }
    }

    public static void signOut() {
        if (null != GoogleSignInService.ins.mImp) {
            GoogleSignInService.ins.mImp.signOut();
        }
    }
}
