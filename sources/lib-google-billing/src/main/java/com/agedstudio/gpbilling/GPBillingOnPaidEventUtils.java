package com.agedstudio.gpbilling;

import android.content.Context;

import com.agedstudio.base.AbstractAnalyticsApi;
import com.android.billingclient.api.AccountIdentifiers;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GPBillingOnPaidEventUtils {
    private static final String TAG = GPBillingOnPaidEventUtils.class.getSimpleName();

    private static Context mContext = null;
    private static List<AbstractAnalyticsApi> mAnalyticsApis = null;

    public static void init(Context context, List<AbstractAnalyticsApi> analyticsApiList) {
        mContext = context;
        mAnalyticsApis = analyticsApiList;
    }

    public static void onPaidEvent(Purchase purchase, ProductDetails productDetails) {
        if(purchase == null || productDetails == null) return;

        String productType = productDetails.getProductType();

        String currencyCode = "";
        long amount = 0;
        String itemType = productDetails.getName();
        String itemId = productDetails.getProductId();
        String cartType = "Shop";
        String receipt = purchase.getPurchaseToken();
        String signature = purchase.getSignature();
        String planId = "";
        if(productType.equals(BillingClient.ProductType.INAPP)){
            currencyCode = productDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode();
            amount = productDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros();
        }else{
            AccountIdentifiers identifiers = purchase.getAccountIdentifiers();
            if(identifiers == null){
                return;
            }
            planId = identifiers.getObfuscatedAccountId();
            for(ProductDetails.SubscriptionOfferDetails subscriptionDetail : productDetails.getSubscriptionOfferDetails()) {
                if (subscriptionDetail.getBasePlanId().equals(planId)) {
                    ProductDetails.PricingPhase pricingPhase = subscriptionDetail.getPricingPhases().getPricingPhaseList().get(0);
                    currencyCode = pricingPhase.getPriceCurrencyCode();
                    amount = (int)pricingPhase.getPriceAmountMicros();
                    break;
                }
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("currencyCode", currencyCode);
        params.put("amount", amount);
        params.put("itemType", itemType);
        params.put("itemId", itemId);
        params.put("planId", planId);
        params.put("cartType", cartType);
        params.put("receipt", receipt);
        params.put("signature", signature);

        for(AbstractAnalyticsApi api : mAnalyticsApis){
            api.onGoogleBillingEvent(params);
        }
    }
}
