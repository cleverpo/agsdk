package com.agedstudio.cmp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.agedstudio.base.AbstractCMPApi;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class CMPBusiness extends AbstractCMPApi {
    public static final String TAG = CMPBusiness.class.getSimpleName();

    private ConsentInformation mConsentInformation = null;

    @Override
    public void init(Context context) {
        super.init(context);
        mConsentInformation = UserMessagingPlatform.getConsentInformation(context);
        LogUtil.i(TAG, "CMPBusiness----init----");
    }

    @Override
    public void showIfRequired() {
        LogUtil.i(TAG, "CMPBusiness----showIfRequired----");
        ConsentRequestParameters parameters = null;
        if (mDebug) {
            LogUtil.i(TAG, "CMPBusiness----showIfRequired----1");
            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(mContext)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(mTestDeviceHashedId)
                    .setForceTesting(true)
                    .build();
            parameters = new ConsentRequestParameters.Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .setConsentDebugSettings(debugSettings)
                    .build();
        } else {
            LogUtil.i(TAG, "CMPBusiness----showIfRequired----2");
            parameters = new ConsentRequestParameters.Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .build();
        }

        LogUtil.i(TAG, "CMPBusiness----showIfRequired--3--requestConsentInfoUpdate");
        mConsentInformation.requestConsentInfoUpdate(
            (Activity) mContext,
            parameters,
            (ConsentInformation.OnConsentInfoUpdateSuccessListener) ()->{
                LogUtil.i(TAG, "CMPBusiness----showIfRequired--4--OnConsentInfoUpdateSuccessListener");
                UserMessagingPlatform.loadAndShowConsentFormIfRequired((Activity) mContext, (ConsentForm.OnConsentFormDismissedListener) loadAndShowError-> {
                    LogUtil.i(TAG, "CMPBusiness----showIfRequired--5--OnConsentFormDismissedListener");
                    if (loadAndShowError == null) {
                        LogUtil.i(TAG, "CMPBusiness----showIfRequired--6--OnConsentFormDismissedListener");
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                        String purposeConsents = sharedPref.getString("IABTCF_PurposeConsents", "");
                        if (null != purposeConsents && !purposeConsents.isEmpty()) {
                            LogUtil.i(TAG, "CMPBusiness----showIfRequired--7--OnConsentFormDismissedListener");
                            char purposeOneString = purposeConsents.charAt(0);
                            if (purposeOneString == '0') {
                                LogUtil.i(TAG, "CMPBusiness----showIfRequired--7-1--OnConsentFormDismissedListener");
                                // 用户拒绝存储
                                onReject();
                            } else if (purposeOneString == '1') {
                                LogUtil.i(TAG, "CMPBusiness----showIfRequired--7-2--OnConsentFormDismissedListener");
                                // 用户同意存储
                                onAccept();
                            }
                        }
                    }

                    if (loadAndShowError != null) {
                        LogUtil.i(TAG, "CMPBusiness----showIfRequired--8--OnConsentFormDismissedListener");
                        // IABTCF_PurposeConsents
                        LogUtil.i(TAG, String.format("loadAndShowError code=%s message=%s", loadAndShowError.getErrorCode(), loadAndShowError.getMessage()));
                        onResultFailure(loadAndShowError.getErrorCode(), loadAndShowError.getMessage());
                    } else {
                        LogUtil.i(TAG, "CMPBusiness----showIfRequired--9--OnConsentFormDismissedListener");
                        onResultSuccess();
                    }
                });
            },
            (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError->{
                LogUtil.i(TAG, "CMPBusiness----showIfRequired--10--OnConsentFormDismissedListener");
                LogUtil.i(TAG, String.format("requestConsentError code=%s message:%s", requestConsentError.getErrorCode(), requestConsentError.getMessage()));
                onResultFailure(requestConsentError.getErrorCode(), requestConsentError.getMessage());
            }
        );
    }

    @Override
    public void reset() {
        mConsentInformation.reset();
    }

    @Override
    public boolean canRequestAds() {
        return mConsentInformation.canRequestAds();
    }

    public void requestConsentInfoUpdate() {
        LogUtil.i(TAG, "CMPBusiness----requestConsentInfoUpdate--");
        ConsentRequestParameters parameters = null;
        if (mDebug) {
            LogUtil.i(TAG, "CMPBusiness----requestConsentInfoUpdate--1");
            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(mContext)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(mTestDeviceHashedId)
                    .setForceTesting(true)
                    .build();
            parameters = new ConsentRequestParameters.Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .setConsentDebugSettings(debugSettings)
                    .build();
        } else {
            LogUtil.i(TAG, "CMPBusiness----requestConsentInfoUpdate--2");
            parameters = new ConsentRequestParameters.Builder()
                    .setTagForUnderAgeOfConsent(false)
                    .build();
        }

        LogUtil.i(TAG, "requestConsentInfoUpdate");
        mConsentInformation.requestConsentInfoUpdate(
                (Activity) mContext,
                parameters,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) ()->{
                    LogUtil.i(TAG, "CMPBusiness----requestConsentInfoUpdate--3");
                    onConsentInfoUpdateSuccess();
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError->{
                    LogUtil.i(TAG, "CMPBusiness----requestConsentInfoUpdate--4");
                    LogUtil.i(TAG, String.format("requestConsentError code=%s message:%s", requestConsentError.getErrorCode(), requestConsentError.getMessage()));
                    onConsentInfoUpdateFailure();
                }
        );
    }

    private void onResultSuccess() {
        LogUtil.i(TAG, "CMPBusiness----onResultSuccess--");
        CommonUtil.eventCallback(AGEvents.OnCMPResultSuccess + "()");
    }
    private void onResultFailure(int code, String message) {
        LogUtil.i(TAG, "CMPBusiness----onResultFailure--code=" + code + ", message=" + message);
        LogUtil.i(TAG, "code=" + code + ", message=" + message);

        CommonUtil.eventCallback(AGEvents.OnCMPResultFailure + "()");
    }

    /**
     * 接受
     */
    private void onAccept() {
        LogUtil.i(TAG, "CMPBusiness----onAccept---");
        CommonUtil.eventCallback(AGEvents.OnCMPAccept + "()");
    }

    /**
     * 拒绝
     */
    private void onReject() {
        LogUtil.i(TAG, "CMPBusiness----onReject---");
        CommonUtil.eventCallback(AGEvents.OnCMPReject + "()");
    }

    public String getPurposeConsents() {
        LogUtil.i(TAG, "CMPBusiness----getPurposeConsents---");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getString("IABTCF_PurposeConsents", "");

    }

    public int getConsentStatus() {
        LogUtil.i(TAG, "CMPBusiness----getConsentStatus---");
        int status = mConsentInformation.getConsentStatus();
        if (status == ConsentInformation.ConsentStatus.UNKNOWN) {
            LogUtil.i(TAG, "CMPBusiness----getConsentStatus---0");
            return 0;
        } else if (status == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
            LogUtil.i(TAG, "CMPBusiness----getConsentStatus---1");
            return 1;
        } else if (status == ConsentInformation.ConsentStatus.REQUIRED) {
            LogUtil.i(TAG, "CMPBusiness----getConsentStatus---2");
            return 2;
        } else if (status == ConsentInformation.ConsentStatus.OBTAINED) {
            LogUtil.i(TAG, "CMPBusiness----getConsentStatus---3");
            return 3;
        }
        return 0;
    }

    /**
     * 是否可用
     * @return
     */
    public boolean isConsentFormAvailable() {
        LogUtil.i(TAG, "CMPBusiness----isConsentFormAvailable---");
        return mConsentInformation.isConsentFormAvailable();
    }

    private void onConsentInfoUpdateSuccess() {
        LogUtil.i(TAG, "CMPBusiness----onConsentInfoUpdateSuccess---");
        CommonUtil.eventCallback(AGEvents.OnCMPConsentInfoUpdateSuccess + "()");
    }

    private void onConsentInfoUpdateFailure() {
        LogUtil.i(TAG, "CMPBusiness----onConsentInfoUpdateFailure---");
        CommonUtil.eventCallback(AGEvents.OnCMPConsentInfoUpdateFailure + "()");
    }
}
