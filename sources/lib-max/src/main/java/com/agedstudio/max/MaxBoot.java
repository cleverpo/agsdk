package com.agedstudio.max;

import android.content.Context;

import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.DTBAdNetwork;
import com.amazon.device.ads.DTBAdNetworkInfo;
import com.amazon.device.ads.MRAIDPolicy;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.unity3d.ads.metadata.MetaData;


public class MaxBoot {
    private static final String TAG = MaxBoot.class.getSimpleName();

    public static void init(Context context, boolean hasUserConsent, String amazonAppId) {
        LogUtil.i(TAG, "MaxBoot-----init--hasUserConsent:"+hasUserConsent);
        // applovin
        AppLovinPrivacySettings.setHasUserConsent(hasUserConsent, context);
        // unity
        MetaData gdprMetaData = new MetaData(context);
        gdprMetaData.set("gdpr.consent", hasUserConsent);
        gdprMetaData.commit();

        // max
        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance( context ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( context, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                LogUtil.d(TAG, "SDK initialized");

                onAdsSDKInitializationComplete();
            }
        });
        AppLovinSdk.getInstance( context ).getSettings().setMuted( true );  // to mute

        //对Applovin（Max)-Amazon 要单独初始化
        AdRegistration.getInstance(amazonAppId, context);
        AdRegistration.setAdNetworkInfo( new DTBAdNetworkInfo( DTBAdNetwork.MAX ) );
        AdRegistration.setMRAIDSupportedVersions( new String[] { "1.0", "2.0", "3.0" } );
        AdRegistration.setMRAIDPolicy( MRAIDPolicy.CUSTOM );

        //AppLovin test
//        AppLovinSdk.getInstance( context ).showMediationDebugger();
        //Amazon test
//        AdRegistration.enableTesting(true);
//        AdRegistration.enableLogging(true);
    }

    private static void onAdsSDKInitializationComplete() {
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CommonUtil.eventCallback(AGEvents.OnAdsSdkInitializationCompleted + "()");
            }
        });
    }
}
