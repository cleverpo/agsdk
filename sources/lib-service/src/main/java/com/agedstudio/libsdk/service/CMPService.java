package com.agedstudio.libsdk.service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.agedstudio.base.AbstractCMPApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class CMPService extends AgedStudioSDKClass {

    private static final String TAG = CMPService.class.getSimpleName();

    private static CMPService mIns = null;

    private AbstractCMPApi mApi = null;

    @Override
    public void init(Context context) {
        super.init(context);
        LogUtil.d(TAG, "CMPService---init--");
        CMPService.mIns = this;
    }

    public void initBussiness(AbstractCMPApi api) {
        this.mApi = api;
    }

    /**
     * 设置是否debug模式
     * @param debug
     * @param testDeviceHashedId
     */
    public static void setDebug(boolean debug, String testDeviceHashedId) {
        LogUtil.d(TAG, "CMPService---setDebug--");
        if (mIns.mApi == null) {
            return;
        }
        LogUtil.d(TAG, "CMPService---setDebug--1");
        mIns.mApi.setDebug(debug, testDeviceHashedId);
    }

    /**
     * 展示弹窗
     */
    public static void showIfRequired() {
        LogUtil.d(TAG, "CMPService---showIfRequired--");
        if (mIns.mApi == null) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "CMPService---showIfRequired--1");
                mIns.mApi.showIfRequired();
            }
        });
    }

    /**
     * 重置数据
     */
    public static void reset() {
        LogUtil.d(TAG, "CMPService---reset--");
        if (mIns.mApi == null) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "CMPService---reset--1");
                mIns.mApi.reset();
            }
        });
    }

    /**
     * 是否可以请求广告
     *
     * @return
     */
    public static boolean canRequestAds() {
        boolean result = true;
        if (mIns.mApi != null) {
            result = mIns.mApi.canRequestAds();
        }
        LogUtil.d(TAG, "CMPService---canRequestAds result: " + result);
        return result;
    }

    public static void requestConsentInfoUpdate() {
        LogUtil.d(TAG, "CMPService---requestConsentInfoUpdate--");
        if (mIns.mApi == null) {
            return;
        }
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "CMPService---requestConsentInfoUpdate--1");
                mIns.mApi.requestConsentInfoUpdate();
            }
        });

    }

    /**
     * 获取同意状态
     * @return
     */
    public static String getPurposeConsents() {
        LogUtil.d(TAG, "CMPService---getPurposeConsents--");
        if (mIns.mApi == null) {
            return "";
        }
        LogUtil.d(TAG, "CMPService---getPurposeConsents--1");
        return mIns.mApi.getPurposeConsents();
    }


    public static int getConsentStatus() {
        LogUtil.d(TAG, "CMPService---getConsentStatus--");
        if (mIns.mApi == null) {
            return 0;
        }
        LogUtil.d(TAG, "CMPService---getConsentStatus--1");
        return mIns.mApi.getConsentStatus();
    }

    /**
     * 是否可用
     * @return
     */
    public static boolean isConsentFormAvailable() {
        LogUtil.d(TAG, "CMPService---isConsentFormAvailable--");
        if (mIns.mApi == null) {
            return false;
        }
        LogUtil.d(TAG, "CMPService---isConsentFormAvailable--1");
        return mIns.mApi.isConsentFormAvailable();
    }

}
