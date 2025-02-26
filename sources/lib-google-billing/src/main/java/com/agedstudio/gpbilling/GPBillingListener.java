package com.agedstudio.gpbilling;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agedstudio.base.mgr.BillingMgr;
import com.agedstudio.base.utils.LogUtil;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.Hashtable;
import java.util.List;

public class GPBillingListener implements BillingClientStateListener, ProductDetailsResponseListener, PurchasesUpdatedListener, ConsumeResponseListener, AcknowledgePurchaseResponseListener {
    private static final String TAG = GPBillingMgr.class.getSimpleName();

    private GPBillingMgr mBillingMgr;

    public GPBillingListener(GPBillingMgr billingMgr){
        this.mBillingMgr = billingMgr;
    }
    @Override
    public void onBillingServiceDisconnected() {
        LogUtil.i(TAG, "GPBillingMgr--BillingClientStateListener--onBillingServiceDisconnected");
        GPBillingMgr inst = this.mBillingMgr;
        inst.isReady = false;
        inst.onResult(GPBillingMgr.NETWORK_ERROR, "service disconnected");
        //2秒后自动重连
        inst.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "GPBillingMgr--BillingClientStateListener--onBillingServiceDisconnected--1");
                inst.startConnection();
            }
        }, 2000);
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        LogUtil.i(TAG, "GPBillingMgr--onBillingSetupFinished");
        GPBillingMgr inst = this.mBillingMgr;
        final int code = billingResult.getResponseCode();
        final String message = billingResult.getDebugMessage();
        if (code == BillingClient.BillingResponseCode.OK) {
            LogUtil.i(TAG, "GPBillingMgr--onBillingSetupFinished--1");

            BillingResult result = inst.billingClient.isFeatureSupported(BillingClient.FeatureType.PRODUCT_DETAILS);
            LogUtil.i(TAG, "Product Details Support code: " + result.getResponseCode());
            if(result.getResponseCode() != BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
                LogUtil.i(TAG, "GPBillingMgr--onBillingSetupFinished--1-1--isFeatureProductDetailsSupport: true");
            }
            if(inst.isReady)
                return;

            inst.isReady = true;
            inst.onResult(GPBillingMgr.INIT_SUCCESS, "init success", code, message);
            return;
        }

        LogUtil.i(TAG, "GPBillingMgr--onBillingSetupFinished--2");
        inst.isReady = false;
        inst.onResult(GPBillingMgr.INIT_FAIL, "init fail", code, message);
    }

    @Override
    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
        LogUtil.i(TAG, "GPBillingMgr--ProductDetailsResponseListener--onProductDetailsResponse");
        final GPBillingMgr inst = this.mBillingMgr;
        final int code = billingResult.getResponseCode();
        final String message = billingResult.getDebugMessage();

        if (code == BillingClient.BillingResponseCode.OK) {
            LogUtil.i(TAG, "GPBillingMgr--ProductDetailsResponseListener--onProductDetailsResponse--1");
            if (null != list) {
                LogUtil.i(TAG, "GPBillingMgr--ProductDetailsResponseListener--onProductDetailsResponse--1-1--list.size="+list.size());
                inst.saveProductDetails(list);
            }

            if(!inst.hasQuerySubsProductDetails){
                inst.querySubsSkuDetails();
            }else{
                inst.onResult(BillingMgr.QUERY_SKU_DETAILS_SUCCESS, message, code, message);
            }
        } else {
            LogUtil.i(TAG, "GPBillingMgr--ProductDetailsResponseListener--onProductDetailsResponse--2");
            inst.onResult(BillingMgr.QUERY_SKU_DETAILS_FAIL, message, code, message);
        }
    }

    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        LogUtil.i(TAG, "GPBillingMgr--PurchasesUpdatedListener--onPurchasesUpdated");
        GPBillingMgr inst = this.mBillingMgr;
        int code = billingResult.getResponseCode();
        String message = billingResult.getDebugMessage();
        if (code == BillingClient.BillingResponseCode.OK) {
            LogUtil.i(TAG, "GPBillingMgr--PurchasesUpdatedListener--onPurchasesUpdated--1");
            if (null != list) {
                LogUtil.i(TAG, "GPBillingMgr--PurchasesUpdatedListener--onPurchasesUpdated--1-1:list.size="+list.size());

                for (int i = 0; i < list.size(); i++) {
                    Purchase purchase = list.get(i);
                    inst.savePurchase(purchase);
                    inst.saveHistoryPurchase(purchase);

                    inst.onResult(BillingMgr.PURCHASE_SUCCESS, "success", code, "", inst.serializePurchase(purchase));
                }
            }
            inst.queryPurchases();
        } else if (code == BillingClient.BillingResponseCode.USER_CANCELED) {
            LogUtil.i(TAG, "GPBillingMgr--PurchasesUpdatedListener--onPurchasesUpdated--2");
            inst.onResult(BillingMgr.USER_CANCELED, "user canceled", code, message);
        } else if (code == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            LogUtil.i(TAG, "GPBillingMgr--PurchasesUpdatedListener--onPurchasesUpdated--3");
            inst.onResult(BillingMgr.ALREADY_PURCHASED, "already purchased", code, message);
            inst.queryPurchases();
        } else {
            LogUtil.i(TAG, "GPBillingMgr--PurchasesUpdatedListener--onPurchasesUpdated--4");
            inst.onResult(100000 + code, message, code, message);
        }
    }

    @Override
    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String purchaseToken) {
        GPBillingMgr inst = this.mBillingMgr;
        LogUtil.i(TAG, "GPBillingMgr--consume--5-1");
        final int code = billingResult.getResponseCode();
        final String message = billingResult.getDebugMessage();
        if (code == BillingClient.BillingResponseCode.OK) {
            LogUtil.i(TAG, "GPBillingMgr--consume--5-1-1--code:"+code+",message:"+message);
            Purchase purchase = inst.removePurchase(purchaseToken);
            if(purchase != null){
                Hashtable<String, Object> data = inst.serializeConsume(purchase);
                inst.onResult(BillingMgr.CONSUME_INAPP_SUCCESS, message, code, message, data);

                GPBillingOnPaidEventUtils.onPaidEvent(purchase, inst.getProductDetails(purchase.getProducts().get(0)));
            }
        } else {
            LogUtil.i(TAG, "GPBillingMgr--consume--5-1-2--code:"+code+",message:"+message);
            inst.onResult(BillingMgr.CONSUME_FAIL, message, code, message);
        }
    }

    @Override
    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
        LogUtil.i(TAG, "GPBillingMgr--consume--6-1");
        final GPBillingMgr inst = this.mBillingMgr;
        final int code = billingResult.getResponseCode();
        final String message = billingResult.getDebugMessage();
        if (code == BillingClient.BillingResponseCode.OK) {
            LogUtil.i(TAG, "GPBillingMgr--consume--6-1-1");
            inst.queryPurchases();
        } else {
            LogUtil.i(TAG, "GPBillingMgr--consume--6-1-2");
            inst.onResult(BillingMgr.CONSUME_FAIL, message, code, message);
        }
    }
}
