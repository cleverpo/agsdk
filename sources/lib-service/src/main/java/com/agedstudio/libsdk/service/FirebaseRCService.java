package com.agedstudio.libsdk.service;

import android.content.Context;

import com.agedstudio.base.AbstractRCApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class FirebaseRCService extends AgedStudioSDKClass {

    private static final String TAG = FirebaseRCService.class.getSimpleName();
    private static FirebaseRCService mIns = null;
    private AbstractRCApi mRemoteConfigImp = null;

    public void init(Context context, AbstractRCApi imp) {
        super.init(context);
        FirebaseRCService.mIns = this;
        FirebaseRCService.mIns.mRemoteConfigImp = imp;
    }

    /**
     * 是否存在key
     * @param key
     * @return
     */
    public static boolean isExists(String key) {
        if (null == FirebaseRCService.mIns || null == FirebaseRCService.mIns.mRemoteConfigImp) {
            return false;
        }
        return FirebaseRCService.mIns.mRemoteConfigImp.isExists(key);
    }

    /**
     * 重置所有的数据
     */
    public static void reset() {
        if (null == FirebaseRCService.mIns || null == FirebaseRCService.mIns.mRemoteConfigImp) {
            return;
        }
        FirebaseRCService.mIns.mRemoteConfigImp.reset();
    }


    /**
     * 获取远程配置
     * @param type 1:boolean 2:double 3:long 4:string
     * @return
     */
    public static String getConfig(String key, int type) {
        if (null == FirebaseRCService.mIns || null == FirebaseRCService.mIns.mRemoteConfigImp) {
            return "";
        }
        String result = FirebaseRCService.mIns.mRemoteConfigImp.getConfig(key, type);

        LogUtil.i(TAG, "getConfig key: " + key + ", type: " + type + ", result: " + result);
        return result;
    }

    /**
     *  获取并激活远程配置
     */
    public static void fetchAndActivate() {
        if (null == FirebaseRCService.mIns || null == FirebaseRCService.mIns.getContext() || null == FirebaseRCService.mIns.mRemoteConfigImp) {
            return;
        }
        LogUtil.i(TAG, "fetchAndActivate");
        FirebaseRCService.mIns.mRemoteConfigImp.fetchAndActivate(new AbstractRCApi.OnCompleteListener(){
            @Override
            public void onComplete(boolean bool) {
                CommonUtil.runOnGameThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtil.eventCallback(AGEvents.OnFetchAndActivateRemoteConfig + "('" + bool + "')");
                    }
                });
            }
        });
    }
}
