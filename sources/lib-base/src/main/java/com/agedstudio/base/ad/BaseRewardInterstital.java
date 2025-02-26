package com.agedstudio.base.ad;

import android.app.Activity;

import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class BaseRewardInterstital extends BaseAd {

    private static final String TAG = BaseRewardInterstital.class.getSimpleName();

    /**
     * 是否加载中
     */
    protected boolean isLoading = false;
    /**
     * 重试倒计时
     */
    protected float restryCountdown = 0;

    protected BaseRewardInterstital.LoadListener mLoadListener = null;
    protected BaseRewardInterstital.ShowListener mShowListener = null;

    public BaseRewardInterstital(String placementID) {
        this.activity = (Activity) CommonUtil.getActivityContext();
        this.placementID = placementID;
    }

    public void setLoadListener(BaseRewardInterstital.LoadListener listener) {
        this.mLoadListener = listener;
    }

    public void setShowListener(BaseRewardInterstital.ShowListener listener) {
        this.mShowListener = listener;
    }

    /**
     * 加载广告
     */
    public void load() {}

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
        LogUtil.i(TAG, "reward interstitial loaded:placementID=" + this.placementID);
        if (null != this.mLoadListener) {
            this.mLoadListener.onLoaded();
        }
    }

    public void onCallListenerLoadFail(int errCode, String errMsg) {
        LogUtil.i(TAG, "reward interstitial failed to load:placementID=" + this.placementID + ",errCode=" + errCode + ",errMsg=" + errMsg);
        if (null != this.mLoadListener) {
            this.mLoadListener.onLoadFail(errCode, errMsg);
        }
    }

    public void onCallListenerShow() {
        LogUtil.i(TAG, "reward interstitial showed:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onShow();
        }
    }

    public void onCallListenerPlayFailed(int errCode, String errMsg) {
        LogUtil.i(TAG, "reward interstitial failed to show:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onPlayFailed(errCode, errMsg);
        }
    }

    public void onCallListenerClose() {
        LogUtil.i(TAG, "reward interstitial dismissed:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onClose();
        }
    }

    public void onCallListenerClick() {
        LogUtil.i(TAG, "reward interstitial clicked:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onClick();
        }
    }

    public void onCallListenerReward() {
        if (null != this.mShowListener) {
            this.mShowListener.onReward();
        }
    }
    /************************** listener **************************/

    public interface LoadListener {
        public void onLoaded();
        public void onLoadFail(int errCode, String errMsg);
    }

    public interface ShowListener {
        public void onShow();
        public void onPlayFailed(int errCode, String errMsg);
        public void onClose();
        public void onClick();
        public void onReward();
    }
}
