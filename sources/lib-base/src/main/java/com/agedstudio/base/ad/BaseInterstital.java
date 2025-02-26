package com.agedstudio.base.ad;

import android.app.Activity;

import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class BaseInterstital extends BaseAd {

    private static final String TAG = BaseInterstital.class.getSimpleName();

    protected BaseInterstital.LoadListener mLoadListener = null;
    protected BaseInterstital.ShowListener mShowListener = null;
    /**
     * 是否加载中
     */
    protected boolean isLoading = false;
    /**
     * 重试倒计时
     */
    protected float restryCountdown = 0;

    public BaseInterstital(String placementID) {
        this.activity = (Activity) CommonUtil.getActivityContext();
        this.placementID = placementID;
    }

    public void setLoadListener(BaseInterstital.LoadListener listener) {
        this.mLoadListener = listener;
    }

    public void setShowListener(BaseInterstital.ShowListener listener) {
        this.mShowListener = listener;
    }

    /**
     * 加载广告
     */
    public void load() {

    }

    public void show(String placement) {}

    public boolean isReady() {
        return false;
    }

    protected void cancelRetryCountdown() {
        this.restryCountdown = 0;
    }

    public void update(float dt) {
        if (this.restryCountdown > 0) {
            this.restryCountdown -= dt;
            if (this.restryCountdown <= 0) {
                this.restryCountdown = 0;
                this.load();
            }
        }
    }

    /************************** listener **************************/
    public void onCallListenerLoaded() {
        LogUtil.i(TAG, "interstitial loaded:placementID=" + this.placementID);
        if (null != this.mLoadListener) {
            this.mLoadListener.onLoaded();
        }
    }

    public void onCallListenerLoadFail(int errCode, String errMsg) {
        LogUtil.i(TAG, "interstitial failed to load:placementID=" + this.placementID + ",errCode=" + errCode + ",errMsg=" + errMsg);
        if (null != this.mLoadListener) {
            this.mLoadListener.onLoadFail(errCode, errMsg);
        }
    }

    public void onCallListenerShow() {
        LogUtil.i(TAG, "interstitial showed:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onShow();
        }
    }

    public void onCallListenerFailedToShow(int errCode, String errMsg) {
        LogUtil.i(TAG, "interstitial failed to show:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onFailedToShow(errCode, errMsg);
        }
    }

    public void onCallListenerClose() {
        LogUtil.i(TAG, "interstitial dismissed:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onClose();
        }
    }

    public void onCallListenerClick() {
        LogUtil.i(TAG, "interstitial clicked:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onClick();
        }
    }
    /************************** listener **************************/


    public interface LoadListener {
        public void onLoaded();
        public void onLoadFail(int errCode, String errMsg);
    }

    public interface ShowListener {
        public void onShow();
        public void onFailedToShow(int errCode, String errMsg);
        public void onClose();
        public void onClick();
    }
}
