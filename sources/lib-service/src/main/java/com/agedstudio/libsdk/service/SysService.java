package com.agedstudio.libsdk.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;
import com.agedstudio.base.utils.SysUtils;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.gms.tasks.Task;

import java.io.File;

public class SysService extends AgedStudioSDKClass {
    private static final String TAG = SysService.class.getSimpleName();

    private static SysService mIns = null;

    @Override
    public void init(Context context) {
        super.init(context);
        SysService.mIns = this;
    }

    public static String getExternalFilesDir() {
        if (null == SysService.mIns || null == SysService.mIns.getContext()) {
            return "";
        }
        return SysService.mIns.getContext().getExternalFilesDir("").getAbsolutePath() + File.separator;
    }

    public static String getExternalCacheDir() {
        if (null == SysService.mIns || null == SysService.mIns.getContext()) {
            return "";
        }
        return SysService.mIns.getContext().getExternalCacheDir().getAbsolutePath() + File.separator;
    }

    public static String getCacheDir() {
        if (null == SysService.mIns || null == SysService.mIns.getContext()) {
            return "";
        }
        return SysService.mIns.getContext().getCacheDir().getAbsolutePath() + File.separator;
    }

    /**
     * 设置屏幕的亮度
     * @param birghtness 0～255
     */
    public static void setScreenBrightness(final int birghtness) {
        SysUtils.setScreenBrightness(birghtness);
    }

    /**
     * 获取屏幕的亮度
     * @returns 0～255
     */
    public static int getScreenBrightness() {
        return SysUtils.getScreenBrightness();
    }

    /**
     * 游戏启动完成，隐藏启动页
     */
    public static void hideSplash() {
        LogUtil.i(TAG, "hideSplash");
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO
            }
        });
    }

    public static void openStore() {
        String channelName = SysService.getChannelName();
        if (TextUtils.equals(channelName, "googleplay")) {
            SysService.openGooglePlay();
        } else if (TextUtils.equals(channelName, "amazon")) {
            SysService.openAmazon();
        } else {
            SysService.openGooglePlay();
        }
    }

    /**
     * 跳转到Google Play
     */
    public static void openGooglePlay() {
        LogUtil.i(TAG, "open google play");
        if (null == SysService.mIns || null == SysService.mIns.getContext()) {
            return;
        }
        final Activity activity = (Activity)SysService.mIns.getContext();
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String packageName = activity.getPackageName();
                if (packageName == null) {
                    packageName = "com.agedstudio.tileconnect.classic.puzzlematch";
                }
                try {
                    Uri uri = Uri.parse("market://details?id=" + packageName);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + packageName);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivity(intent);
                    }
                }
            }
        });
    }

    public static void openAmazon() {
        LogUtil.i(TAG, "open amazon");
        if (null == SysService.mIns || null == SysService.mIns.getContext()) {
            return;
        }
        final Activity activity = (Activity)SysService.mIns.getContext();
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String packageName = activity.getPackageName();
                if (packageName == null) {
                    packageName = "com.agedstudio.tileconnect.classic.puzzlematch";
                }
                try {
                    Uri uri = Uri.parse("amzn://apps/android?p=" + packageName);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivity(intent);
                    } else {
                        uri = Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + packageName);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Uri uri = Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + packageName);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        activity.startActivity(intent);
                    }
                }
            }
        });
    }


    public static void openYandex() {

    }


    /**
     * 打开评分
     */
    public static void openReview() {
        if (null == SysService.mIns || null == SysService.mIns.getContext()) {
            return;
        }
        final Activity activity = (Activity)SysService.mIns.getContext();
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ReviewManager manager = ReviewManagerFactory.create(activity);
                Task<ReviewInfo> requst = manager.requestReviewFlow();
                requst.addOnCompleteListener(requstTask -> {
                    if (requstTask.isSuccessful()) {
                        ReviewInfo reviewInfo = requstTask.getResult();
                        Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
                        flow.addOnCompleteListener(flowTask->{
                            if (flowTask.isSuccessful()) {
                                SysService.onReviewed(true);
                            } else {
                                SysService.onReviewed(false);
                            }
                        });
                    } else {
                        SysService.onReviewed(false);
                    }
                });
            }
        });

    }

    private static void onReviewed(boolean isSuccessful) {
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CommonUtil.eventCallback(AGEvents.OnReviewed + "('" + isSuccessful + "')");
            }
        });

    }

    public static void setKeepScreenOn(boolean value) {
        SysUtils.setKeepScreenOn(value);
    }

    public static String getChannelName() {
        return SysUtils.getChannelName();
    }

    public static void end() {
        SysUtils.end();
    }

    public static void vibrate(int milliseconds) {
        SysUtils.vibrate(milliseconds);
    }

    public static void stopVibrate() {
        SysUtils.stopVibrate();
    }

    public static String getVersionName() {
        return SysUtils.getVersionName();
    }

    public static int getVersionCode() {
        return SysUtils.getVersionCode();
    }


    /**
     * 获取设备型号
     * @return
     */
    public static String getModel() {
        return SysUtils.getModel();
    }

    /**
     * 获取设备厂商
     * @return
     */
    public static String getBrand() {
        return SysUtils.getBrand();
    }

    public static void launchMediationTestSuite() {

    }

    /**
     * 是否是debug包
     * @return
     */
    public static boolean isDebugApp() {
        return SysUtils.isDebugApp();
    }
}
