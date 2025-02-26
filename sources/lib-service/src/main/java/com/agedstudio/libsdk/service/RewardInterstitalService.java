package com.agedstudio.libsdk.service;

import android.content.Context;

import com.agedstudio.base.AbstractRewardInterstitalApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.ad.BaseRewardInterstital;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.AdUtil;
import com.agedstudio.base.utils.CommonUtil;

public class RewardInterstitalService extends AgedStudioSDKClass {

    private static final String TAG = RewardInterstitalService.class.getSimpleName();

    private static RewardInterstitalService mIns = null;
    private AbstractRewardInterstitalApi mAdMob = null;

    /**
     * 1:admon 2:yandex 3:amazon
     */
    private int mMediation = 3;

    @Override
    public void init(Context context){
        super.init(context);
        RewardInterstitalService.mIns = this;
    }

    public void initAdMob(AbstractRewardInterstitalApi adSource) {
        this.mAdMob = adSource;
    }

    private BaseRewardInterstital getAd(String placementID) {
        if (RewardInterstitalService.mIns.mMediation == 1 && null != mAdMob) {
            return RewardInterstitalService.mIns.mAdMob.getAd(placementID);
        }
        return null;
    }

    public static void initAd(String placementID, String params) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseRewardInterstital ad = RewardInterstitalService.mIns.getAd(placementID);
                if (ad == null) {
                    if (RewardInterstitalService.mIns.mMediation == 1 && null != RewardInterstitalService.mIns.mAdMob) {
                        ad = RewardInterstitalService.mIns.mAdMob.initAd(placementID, params);
                    }
                    if (ad != null) {
                        ad.setLoadListener(new BaseRewardInterstital.LoadListener() {
                            @Override
                            public void onLoaded() {
                                AdUtil.onCallback(AGEvents.OnRewardInterstitalLoaded, placementID);
                            }

                            @Override
                            public void onLoadFail(int errCode, String errMsg) {
                                AdUtil.onCallback(AGEvents.OnRewardInterstitalLoadFail, placementID, errCode, errMsg);
                            }
                        });
                        ad.setShowListener(new BaseRewardInterstital.ShowListener() {
                            @Override
                            public void onShow() {
                                AdUtil.onCallback(AGEvents.OnRewardInterstitalShow, placementID);
                            }

                            @Override
                            public void onPlayFailed(int errCode, String errMsg) {
                                AdUtil.onCallback(AGEvents.OnRewardInterstitalPlayFailed, placementID, errCode, errMsg);
                            }

                            @Override
                            public void onClose() {
                                AdUtil.onCallback(AGEvents.OnRewardInterstitalClose, placementID);
                            }

                            @Override
                            public void onClick() {
                                AdUtil.onCallback(AGEvents.OnRewardInterstitalClick, placementID);
                            }

                            @Override
                            public void onReward() {
                                AdUtil.onCallback(AGEvents.OnRewardInterstitalReward, placementID);
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
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseRewardInterstital ad = RewardInterstitalService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.load();
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
                BaseRewardInterstital ad = RewardInterstitalService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.show(placement);
                }
            }
        });
    }

    /**
     * 是否已准备好
     * @return
     */
    public static boolean isReady(String placementID) {
        BaseRewardInterstital ad = RewardInterstitalService.mIns.getAd(placementID);
        if (ad != null) {
            return  ad.isReady();
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
                BaseRewardInterstital ad = RewardInterstitalService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.update(dt);
                }
            }
        });
    }

    public static void setMediation(int type) {
        RewardInterstitalService.mIns.mMediation = type;
    }
}
