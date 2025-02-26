package com.agedstudio.libsdk.service;

import android.content.Context;
import android.util.Log;

import com.agedstudio.base.AbstractRewardVideoApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.ad.BaseRewardVideo;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.AdUtil;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;

public class RewardVideoService extends AgedStudioSDKClass {

    private static final String TAG = RewardVideoService.class.getSimpleName();

    private static RewardVideoService mIns = null;
    private AbstractRewardVideoApi mAdMob = null;
    private AbstractRewardVideoApi mMax = null;
    private AbstractRewardVideoApi mYandex = null;

    /**
     * 1:admon 2:yandex 3:amazon
     */
    private int mMediation = 3;

    @Override
    public void init(Context context){
        super.init(context);
        LogUtil.i(TAG, "RewardVideoService-----init");
        RewardVideoService.mIns = this;
    }

    public void initAdMob(AbstractRewardVideoApi adSource) {
        this.mAdMob = adSource;
    }

    public void initMax(AbstractRewardVideoApi adSource) {
        LogUtil.i(TAG, "RewardVideoService-----initMax");
        this.mMax = adSource;
    }

    public void initYandex(AbstractRewardVideoApi adSource) {
        this.mYandex = adSource;
    }

    private BaseRewardVideo getAd(String placementID) {
        if (RewardVideoService.mIns.mMediation == 1) {
            return RewardVideoService.mIns.mAdMob.getAd(placementID);
        } else if (RewardVideoService.mIns.mMediation == 2) {
            return RewardVideoService.mIns.mYandex.getAd(placementID);
        } else if (RewardVideoService.mIns.mMediation == 3) {
            LogUtil.i(TAG, "RewardVideoService-----getAd----");
            return RewardVideoService.mIns.mMax.getAd(placementID);
        }
        return null;
    }

    public static void initAd(String placementID, String params) {
        LogUtil.i(TAG, "RewardVideoService-----initAd placementId: " + placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "RewardVideoService-----initAd---1");
                BaseRewardVideo ad = RewardVideoService.mIns.getAd(placementID);
                if (ad == null) {
                    LogUtil.i(TAG, "RewardVideoService-----initAd---2");
                    if (RewardVideoService.mIns.mMediation == 1) {
                        ad = RewardVideoService.mIns.mAdMob.initAd(placementID, params);
                    } else if (RewardVideoService.mIns.mMediation == 2) {
                        ad = RewardVideoService.mIns.mYandex.initAd(placementID, params);
                    } else if (RewardVideoService.mIns.mMediation == 3) {
                        LogUtil.i(TAG, "RewardVideoService-----initAd---1-1");
                        ad = RewardVideoService.mIns.mMax.initAd(placementID, params);
                    }
                    LogUtil.i(TAG, "RewardVideoService-----initAd---2");
                    if (ad != null) {
                        LogUtil.i(TAG, "RewardVideoService-----initAd---2-1");
                        ad.setLoadListener(new BaseRewardVideo.LoadListener() {
                            @Override
                            public void onLoaded() {
                                LogUtil.i(TAG, "RewardVideoService-----initAd---2-1-1-onLoaded");
                                AdUtil.onCallback(AGEvents.OnRewardVideoLoaded, placementID);
                            }

                            @Override
                            public void onLoadFail(int errCode, String errMsg) {
                                LogUtil.i(TAG, "RewardVideoService-----initAd---2-1-2-onLoadFail");
                                AdUtil.onCallback(AGEvents.OnRewardVideoLoadFail, placementID, errCode, errMsg);
                            }
                        });
                        ad.setShowListener(new BaseRewardVideo.ShowListener() {
                            @Override
                            public void onShow() {
                                LogUtil.i(TAG, "RewardVideoService-----initAd---2-1-3-onShow");
                                AdUtil.onCallback(AGEvents.OnRewardVideoShow, placementID);
                            }

                            @Override
                            public void onPlayFailed(int errCode, String errMsg) {
                                LogUtil.i(TAG, "RewardVideoService-----initAd---2-1-4-onPlayFailed");
                                AdUtil.onCallback(AGEvents.OnRewardVideoPlayFailed, placementID, errCode, errMsg);
                            }

                            @Override
                            public void onClose() {
                                LogUtil.i(TAG, "RewardVideoService-----initAd---2-1-5-onClose");
                                AdUtil.onCallback(AGEvents.OnRewardVideoClose, placementID);
                            }

                            @Override
                            public void onClick() {
                                AdUtil.onCallback(AGEvents.OnRewardVideoClick, placementID);
                            }

                            @Override
                            public void onReward() {
                                AdUtil.onCallback(AGEvents.OnRewardVideoReward, placementID);
                            }
                        });
                    }
                }
            }
        });
    }


    /**
     * 加载
     * @param placementID
     */
    public static void load(String placementID) {
        LogUtil.i(TAG, "RewardVideoService-----load placementId: " + placementID);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "RewardVideoService-----load placementId--1");
                BaseRewardVideo ad = RewardVideoService.mIns.getAd(placementID);
                if (ad != null) {
                    LogUtil.i(TAG, "RewardVideoService-----load placementId--2");
                    ad.load();
                }else{
                    AdUtil.onCallback(AGEvents.OnRewardVideoLoadFail, placementID, -1, "ad is not created");
                }
            }
        });
    }

    /**
     * 展示
     * @param placementID
     */
    public static void show(String placementID, String placement) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseRewardVideo ad = RewardVideoService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.show(placement);
                }else{
                    AdUtil.onCallback(AGEvents.OnRewardVideoPlayFailed, placementID, -1, "ad is not created");
                }
            }
        });
    }

    /**
     * 是否已准备好
     * @return
     */
    public static boolean isReady(String placementID) {
        BaseRewardVideo ad = RewardVideoService.mIns.getAd(placementID);
        if (ad != null) {
            return ad.isReady();
        }
        return false;
    }

    /**
     * 刷新
     * @param dt
     */
    public static void update(String placementID, float dt) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseRewardVideo ad = RewardVideoService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.update(dt);
                }
            }
        });
    }

    public static void setMediation(int type) {
        LogUtil.i(TAG, "RewardVideoService-----setMediation--type:"+type);
        RewardVideoService.mIns.mMediation = type;
    }
}
