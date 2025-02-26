package com.agedstudio.base.ad;


import android.app.Activity;

import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class BaseBanner extends BaseAd {

    private static final String TAG = BaseBanner.class.getSimpleName();

    protected BaseBanner.LoadListener mLoadListener = null;
    protected BaseBanner.ShowListener mShowListener = null;

    /**
     * 是否加载中
     */
    protected boolean isLoading = false;

    protected boolean isAdReady = false;

    protected int adHeight = 0;

    protected float restryCountdown = 0;

    public BaseBanner(String placementID) {
        this.activity = (Activity) CommonUtil.getActivityContext();
        this.placementID = placementID;
    }

    public void setLoadListener(BaseBanner.LoadListener listener) {
        this.mLoadListener = listener;
    }

    public void setShowListener(BaseBanner.ShowListener listener) {
        this.mShowListener = listener;
    }

    public void load() {}

    public void showWithPosition(String position) {}

    public void hide() {}

    public void reshow() {}

    public void remove() {}

    public boolean isReady() {
        return false;
    }

    public int getAdHeight() {
        return (int)this.adHeight;
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
        LogUtil.i(TAG, "banner loaded:placementID=" + this.placementID);
        if (null != this.mLoadListener) {
            this.mLoadListener.onLoaded();
        }
    }

    public void onCallListenerLoadFail(int errCode, String errMsg) {
        LogUtil.i(TAG, "banner failed to load:placementID=" + this.placementID + ",errCode=" + errCode + ",errMsg=" + errMsg);
        if (null != this.mLoadListener) {
            this.mLoadListener.onLoadFail(errCode, errMsg);
        }
    }

    public void onCallListenerShow() {
        LogUtil.i(TAG, "banner opened:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onShow();
        }
    }

    public void onCallListenerClose() {
        LogUtil.i(TAG, "banner closed:placementID=" + this.placementID);
        if (null != this.mShowListener) {
            this.mShowListener.onClose();
        }
    }

    public void onCallListenerClick() {
        LogUtil.i(TAG, "banner clicked:placementID=" + this.placementID);
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
        public void onClose();
        public void onClick();
    }


}
