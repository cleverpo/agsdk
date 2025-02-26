package com.agedstudio.libsdk.service;

import android.content.Context;

import com.agedstudio.base.AbstractInterstitalApi;
import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.ad.BaseInterstital;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.AdUtil;
import com.agedstudio.base.utils.CommonUtil;

public class IntertitalService extends AgedStudioSDKClass {

    private static final String TAG = IntertitalService.class.getSimpleName();

    private static IntertitalService mIns = null;
    private AbstractInterstitalApi mAdMob = null;
    private AbstractInterstitalApi mMax = null;
    private AbstractInterstitalApi mYandex = null;

    /**
     * 1:admon 2:yandex 3:amazon
     */
    private int mMediation = 1;

    @Override
    public void init(Context context){
        super.init(context);
        IntertitalService.mIns = this;
    }

    public void initAdMob(AbstractInterstitalApi adSource) {
        this.mAdMob = adSource;
    }

    public void initMax(AbstractInterstitalApi adSource) {
        this.mMax = adSource;
    }

    public void initYandex(AbstractInterstitalApi adSource) {
        this.mYandex = adSource;
    }

    private BaseInterstital getAd(String placementID) {
        if (IntertitalService.mIns.mMediation == 1) {
            return IntertitalService.mIns.mAdMob.getAd(placementID);
        } else if (IntertitalService.mIns.mMediation == 2) {
            return IntertitalService.mIns.mYandex.getAd(placementID);
        } else if (IntertitalService.mIns.mMediation == 3) {
            return IntertitalService.mIns.mMax.getAd(placementID);
        }
        return null;
    }

    public static void initAd(String placementID, String params) {
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseInterstital ad = IntertitalService.mIns.getAd(placementID);
                if (ad == null) {
                    if (IntertitalService.mIns.mMediation == 1) {
                        ad = IntertitalService.mIns.mAdMob.initAd(placementID, params);
                    } else if (IntertitalService.mIns.mMediation == 2) {
                        ad = IntertitalService.mIns.mYandex.initAd(placementID, params);
                    } else if (IntertitalService.mIns.mMediation == 3) {
                        ad = IntertitalService.mIns.mMax.initAd(placementID, params);
                    }

                    if (ad != null) {
                        ad.setLoadListener(new BaseInterstital.LoadListener() {
                            @Override
                            public void onLoaded() {
                                AdUtil.onCallback(AGEvents.OnInterstitialLoaded, placementID);
                            }

                            @Override
                            public void onLoadFail(int errCode, String errMsg) {
                                AdUtil.onCallback(AGEvents.OnInterstitialLoadFail, placementID, errCode, errMsg);
                            }
                        });
                        ad.setShowListener(new BaseInterstital.ShowListener() {
                            @Override
                            public void onShow() {
                                AdUtil.onCallback(AGEvents.OnInterstitialShow, placementID);
                            }

                            @Override
                            public void onFailedToShow(int errCode, String errMsg) {
                                AdUtil.onCallback(AGEvents.OnInterstitialFailedToShow, placementID, errCode, errMsg);
                            }

                            @Override
                            public void onClose() {
                                AdUtil.onCallback(AGEvents.OnInterstitialClose, placementID);
                            }

                            @Override
                            public void onClick() {
                                AdUtil.onCallback(AGEvents.OnInterstitialClick, placementID);
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
                BaseInterstital ad = IntertitalService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.load();
                }else{
                    AdUtil.onCallback(AGEvents.OnInterstitialLoadFail, placementID, -1, "ad is not created");
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
                BaseInterstital ad = IntertitalService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.show(placement);
                }else{
                    AdUtil.onCallback(AGEvents.OnInterstitialFailedToShow, placementID, -1, "ad is not created");
                }
            }
        });
    }

    /**
     * 是否已准备好
     * @return
     */
    public static boolean isReady(String placementID) {
        BaseInterstital ad = IntertitalService.mIns.getAd(placementID);
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
                BaseInterstital ad = IntertitalService.mIns.getAd(placementID);
                if (ad != null) {
                    ad.update(dt);
                }
            }
        });
    }

    public static void setMediation(int type) {
        IntertitalService.mIns.mMediation = type;
    }
}
