package com.agedstudio.base.ad;

import android.app.Activity;

import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class BaseRewardVideo extends BaseAd {

    private static final String TAG = BaseRewardVideo.class.getSimpleName();

    protected BaseRewardVideo.LoadListener mLoadListener = null;
    protected BaseRewardVideo.ShowListener mShowListener = null;

    /**
     * 是否加载中
     */
    protected boolean isLoading = false;
    /**
     * 重试倒计时
     */
    protected float restryCountdown = 0;

    public BaseRewardVideo(String placementID) {
        this.activity = (Activity) CommonUtil.getActivityContext();
        this.placementID = placementID;
    }

    public void setLoadListener(BaseRewardVideo.LoadListener listener) {
        this.mLoadListener = listener;
    }

    public void setShowListener(BaseRewardVideo.ShowListener listener) {
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
        LogUtil.i(TAG, "BaseRewardVideo---onCallListenerLoaded:placementID=" + this.placementID);
        if (null != this.mLoadListener) {
            LogUtil.i(TAG, "BaseRewardVideo---onCallListenerLoaded---1");
            this.mLoadListener.onLoaded();
        }
    }

    public void onCallListenerLoadFail(int errCode, String errMsg) {
        LogUtil.i(TAG, "BaseRewardVideo---onCallListenerLoadFail:placementID=" + this.placementID + ",errCode=" + errCode + ",errMsg=" + errMsg);
        if (null != this.mLoadListener) {
            LogUtil.i(TAG, "BaseRewardVideo---onCallListenerLoadFail--1");
            this.mLoadListener.onLoadFail(errCode, errMsg);
        }
    }

    public void onCallListenerShow() {
        LogUtil.i(TAG, "BaseRewardVideo---onCallListenerShow:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            LogUtil.i(TAG, "BaseRewardVideo---onCallListenerShow---1");
            this.mShowListener.onShow();
        }
    }

    public void onCallListenerPlayFailed(int errCode, String errMsg) {
        LogUtil.i(TAG, "BaseRewardVideo---onCallListenerPlayFailed:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            LogUtil.i(TAG, "BaseRewardVideo---onCallListenerPlayFailed---1");
            this.mShowListener.onPlayFailed(errCode, errMsg);
        }
    }

    public void onCallListenerClose() {
        LogUtil.i(TAG, "BaseRewardVideo---onCallListenerClose:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            LogUtil.i(TAG, "BaseRewardVideo---onCallListenerClose---1");
            this.mShowListener.onClose();
        }
    }

    public void onCallListenerClick() {
        LogUtil.i(TAG, "BaseRewardVideo---onCallListenerClick:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            LogUtil.i(TAG, "BaseRewardVideo---onCallListenerClick---1");
            this.mShowListener.onClick();
        }
    }

    public void onCallListenerReward() {
        LogUtil.i(TAG, "BaseRewardVideo---onCallListenerReward---");
        if (null != this.mShowListener) {
            LogUtil.i(TAG, "BaseRewardVideo---onCallListenerReward----1");
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
