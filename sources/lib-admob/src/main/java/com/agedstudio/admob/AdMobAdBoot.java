package com.agedstudio.admob;

import android.content.Context;
import android.util.Log;

import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.SysUtils;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;
import java.util.List;

public class AdMobAdBoot {
    /**
     * 回调
     */
    private static final String OnAdsSdkInitializationCompleted = "onAdsSdkInitializationCompleted";

    private static final String TAG = AdMobAdBoot.class.getSimpleName();

    public static void init(Context context, boolean hasUserConsent) {
        // applovin
        AppLovinPrivacySettings.setHasUserConsent(hasUserConsent, context);

        if (SysUtils.isDebugApp()) {
            Log.e(TAG, "DEBUG");
            List<String> testDeviceIds = Arrays.asList("01D2459DDB0C64709A2A5C08A9A065A9", "D3DD2ACBA26D2178E0F5B42215411110", "46B9B6A363504F9327BF771037834826", "47A1AF91DEA0B72430AD43DA3965ACF5", "1355CBEE038FD8C9EB71831F1E3278BC");
            RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
            MobileAds.setRequestConfiguration(configuration);
        }
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                if (null != initializationStatus) {
                    onAdsSDKInitializationComplete();
                    Log.i(TAG, "onInitializationComplete");
                }
            }
        });
        MobileAds.setAppMuted(true);
    }

    private static void onAdsSDKInitializationComplete() {
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CommonUtil.evalString(OnAdsSdkInitializationCompleted + "()");
            }
        });
    }

}
