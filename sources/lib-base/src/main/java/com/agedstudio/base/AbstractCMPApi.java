package com.agedstudio.base;

import android.content.Context;

public abstract class AbstractCMPApi {

    protected Context mContext = null;
    /**
     * 是否debug模式
     */
    protected boolean mDebug = false;
    /**
     * 测试设备id
     */
    protected String mTestDeviceHashedId = null;

    public void init(Context context) {
        mContext = context;
    }

    public void setDebug(boolean debug, String testDeviceHashedId) {
        mDebug = debug;
        mTestDeviceHashedId = testDeviceHashedId;
    }

    /**
     * 展示弹窗
     */
    public abstract void showIfRequired();

    /**
     * 重置数据
     */
    public abstract void reset();

    /**
     * 是否可以请求广告
     * @return
     */
    public boolean canRequestAds() {
        return true;
    }

    public abstract void requestConsentInfoUpdate();

    public abstract String getPurposeConsents();

    public abstract int getConsentStatus();

    public abstract boolean isConsentFormAvailable();

}
