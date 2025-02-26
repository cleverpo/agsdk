package com.agedstudio.gpbilling;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.agedstudio.base.mgr.BillingMgr;

import com.agedstudio.base.utils.LogUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.billingclient.api.AccountIdentifiers;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class GPBillingMgr implements BillingMgr {
    private static final String TAG = GPBillingMgr.class.getSimpleName();

    private static GPBillingMgr instance = null;
    private Activity context = null;

    public BillingClient billingClient = null;

    private GPBillingListener mGPBillingListener;
    private BillingResultListener billingResultListener = null;

    public Handler handler = null;
    public boolean isReady = false;
    public boolean hasQueryInappProductDetails = false;
    public boolean hasQuerySubsProductDetails = false;

    private ArrayList<SkuInfo> skuInfos = null;

    private ArrayList<Purchase> purchases = new ArrayList<Purchase>();
    private ArrayList<Purchase> historyPurchases = new ArrayList<Purchase>();
    private Hashtable<String, ProductDetails> productDetailsMap = new Hashtable<>();

    public static GPBillingMgr getInstance() {
        if (GPBillingMgr.instance == null) {
            GPBillingMgr.instance = new GPBillingMgr();
        }
        return GPBillingMgr.instance;
    }

    private GPBillingMgr(){
        this.mGPBillingListener = new GPBillingListener(this);
    }

    public void init(Activity context) {
        LogUtil.i(TAG, "GPBillingMgr--init");
        this.context = context;
        PendingPurchasesParams pendingParams = PendingPurchasesParams
                .newBuilder()
                .enableOneTimeProducts()
                .build();
        this.billingClient = BillingClient.newBuilder(context)
                .setListener(this.mGPBillingListener)
                .enablePendingPurchases(pendingParams)
                .build();

        this.handler = new Handler();
    }

    public void setListener(@NonNull BillingMgr.BillingResultListener billingResultListener) {
        if (billingResultListener != null) {
            this.billingResultListener = billingResultListener;
        }
    }

    /**
     * 开始连接
     */
    public void startConnection() {
        LogUtil.i(TAG, "GPBillingMgr--startConnection");
        if (this.billingClient != null) {
            this.billingClient.startConnection(this.mGPBillingListener);
        }
    }
    /**
     * 检查当前客户端是否连接到了服务端
     *
     * @return
     */
    public boolean isReady() {
        return this.billingClient != null && this.billingClient.isReady() && this.isReady;
    }

    public void initSkuInfos(ArrayList<SkuInfo> skuInfos) {
        this.skuInfos = skuInfos;
    }

    /**
     * 开始购买/订阅
     * @param skuId
     * @param skuType
     * @param orderIdOrPlanId
     */
    public void purchase(String skuId, String skuType, String orderIdOrPlanId) {
        LogUtil.i(TAG, "GPBillingMgr--purchase---0-skuId=" + skuId + ", skuType=" + skuType + ", orderIdOrPlanId=" + orderIdOrPlanId);
        if (this.isReady()) {
            LogUtil.i(TAG, "GPBillingMgr--purchase-----1");
            do {
                LogUtil.i(TAG, "GPBillingMgr--purchase-----2");
                if (!this.isValidSkuType(skuType)) {
                    LogUtil.i(TAG, "GPBillingMgr--purchase-----3");
                    this.onResult(PURCHASE_REQUEST_FAIL, "invalid sku type");
                    break;
                }
                LogUtil.i(TAG, "GPBillingMgr--purchase-----4");
                if (skuId == null || TextUtils.isEmpty(skuId)) {
                    LogUtil.i(TAG, "GPBillingMgr--purchase-----5");
                    this.onResult(PURCHASE_REQUEST_FAIL, "invalid sku id");
                    break;
                }
                LogUtil.i(TAG, "GPBillingMgr--purchase-----6");
                LogUtil.i(TAG, "GPBillingMgr--purchase-----7");
                if (this.productDetailsMap.containsKey(skuId)) {
                    LogUtil.i(TAG, "GPBillingMgr--purchase-----7-1");
                    ProductDetails productDetails = this.productDetailsMap.get(skuId);
                    BillingFlowParams.Builder billingFlowParamsBuilder  = BillingFlowParams.newBuilder();
                    BillingFlowParams.ProductDetailsParams.Builder productDetailsParamsbuilder = BillingFlowParams.ProductDetailsParams.newBuilder();

                    productDetailsParamsbuilder.setProductDetails(productDetails);

                    if (BillingClient.ProductType.INAPP.equals(skuType)) {
                        LogUtil.i(TAG, "GPBillingMgr--purchase-----7-2");
                    } else {
                        //根据orderId找到offerToken
                        String offerToken = "";
                        for(ProductDetails.SubscriptionOfferDetails subscriptionDetail : productDetails.getSubscriptionOfferDetails()){
                            if(subscriptionDetail.getBasePlanId().equals(orderIdOrPlanId)){
                                offerToken = subscriptionDetail.getOfferToken();
                                break;
                            }
                        }
                        if(offerToken.isEmpty()){
                            LogUtil.i(TAG, "GPBillingMgr--purchase-----7-2");
                            this.onResult(PURCHASE_REQUEST_FAIL, "invalid order id");
                            break;
                        }
                        productDetailsParamsbuilder.setOfferToken(offerToken);

                        //查询是否有就订阅
                        String oldPurchaseToken = "";
                        for (int i = 0; i < this.historyPurchases.size(); i++) {
                            Purchase purchase = this.historyPurchases.get(i);
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.getProducts().get(0).equals(skuId)) {
                                oldPurchaseToken = purchase.getPurchaseToken();
                                break;
                            }
                        }
                        if(!oldPurchaseToken.isEmpty()){
                            BillingFlowParams.SubscriptionUpdateParams.Builder subscriptionUpdateParamsBuilder = BillingFlowParams.SubscriptionUpdateParams.newBuilder();
                            subscriptionUpdateParamsBuilder.setOldPurchaseToken(oldPurchaseToken);
                            subscriptionUpdateParamsBuilder.setSubscriptionReplacementMode(BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.UNKNOWN_REPLACEMENT_MODE);
                            billingFlowParamsBuilder.setSubscriptionUpdateParams(subscriptionUpdateParamsBuilder.build());
                        }
                    }
                    LogUtil.i(TAG, "GPBillingMgr--purchase-----7-3");
                    BillingFlowParams billingFlowParams = billingFlowParamsBuilder
                            .setProductDetailsParamsList(Arrays.asList(productDetailsParamsbuilder.build()))
                            .setObfuscatedAccountId(orderIdOrPlanId)
                            .setObfuscatedProfileId(orderIdOrPlanId)
                            .setIsOfferPersonalized(true)
                            .build();
                    this.context.setIntent(new Intent());
                    BillingResult result = this.billingClient.launchBillingFlow(this.context, billingFlowParams);

                    LogUtil.i(TAG, "GPBillingMgr--purchase-----7-4 code: " + result.getResponseCode() + " message: " + result.getDebugMessage());
                    if(result.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        this.onResult(PURCHASE_REQUEST_SUCCESS, "");
                    }else{
                        this.onResult(PURCHASE_REQUEST_FAIL, result.getDebugMessage());
                    }
                }
            } while (false);
        }
    }

    public void consume(String skuId, String purchaseId, String skuType, String purchaseToken) {
        LogUtil.i(TAG, "GPBillingMgr--consume--skuId:"+skuId+",purchaseId:"+purchaseId+",skuType:"+skuType);
        if (this.isReady()) {
            LogUtil.i(TAG, "GPBillingMgr--consume--1--purchases.size:"+this.purchases.size());
            String token = null;
            for (int i = 0; i < this.purchases.size(); i++) {
                Purchase purchase = this.purchases.get(i);
                if (purchase.getPurchaseToken().equals(purchaseToken)) {
                    if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                        token = purchaseToken;
                        break;
                    }
                }
            }
            LogUtil.i(TAG, "GPBillingMgr--consume--2");
            if (null == token) {
                LogUtil.i(TAG, "GPBillingMgr--consume--3");
                this.onResult(CONSUME_FAIL, "invalid token");
            }  else {
                LogUtil.i(TAG, "GPBillingMgr--consume--4");
                if (BillingClient.ProductType.INAPP.equals(skuType)) {
                    LogUtil.i(TAG, "GPBillingMgr--consume--5");
                    ConsumeParams params = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build();
                    this.billingClient.consumeAsync(params, this.mGPBillingListener);
                } else if (BillingClient.ProductType.SUBS.equals(skuType)) {
                    LogUtil.i(TAG, "GPBillingMgr--consume--6");
                    AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchaseToken)
                            .build();
                    this.billingClient.acknowledgePurchase(params, this.mGPBillingListener);
                }
            }
        }
    }

    public void querySkuDetails() {
        LogUtil.i(TAG, "GPBillingMgr--querySkuDetails");
        if (this.isReady()) {
            LogUtil.i(TAG, "GPBillingMgr--querySkuDetails--1");
            if (this.skuInfos == null || this.skuInfos.size() == 0) {
                return;
            }
            this.hasQueryInappProductDetails = false;
            this.hasQuerySubsProductDetails = false;

            this.queryInappSkuDetails();
        }
    }

    public void queryInappSkuDetails(){
        LogUtil.i(TAG, "GPBillingMgr--queryInappSkuDetails");
        if (this.isReady()) {
            LogUtil.i(TAG, "GPBillingMgr--queryInappSkuDetails--1");
            if (this.skuInfos == null || this.skuInfos.size() == 0) {
                return;
            }
            LogUtil.i(TAG, "GPBillingMgr--queryInappSkuDetails--2");
            ArrayList<QueryProductDetailsParams.Product> inappProductList = new ArrayList<>();
            for (int i = 0; i < this.skuInfos.size(); i++) {
                SkuInfo skuInfo = this.skuInfos.get(i);
                QueryProductDetailsParams.Product.Builder productBuilder = QueryProductDetailsParams.Product.newBuilder();
                productBuilder.setProductId(skuInfo.skuId);
                productBuilder.setProductType(skuInfo.skuType);
                if(BillingClient.ProductType.INAPP.equals(skuInfo.skuType)){
                    inappProductList.add(productBuilder.build());
                    LogUtil.i(TAG, "GPBillingMgr--queryInappSkuDetails--4--skuInfos:"+skuInfo.skuId+","+skuInfo.skuType);
                }
            }
            this.billingClient.queryProductDetailsAsync(
                    QueryProductDetailsParams.newBuilder()
                            .setProductList(inappProductList)
                            .build(),
                    this.mGPBillingListener);

            this.hasQueryInappProductDetails = true;
        }
    }


    public void querySubsSkuDetails(){
        LogUtil.i(TAG, "GPBillingMgr--querySubsSkuDetails");
        if (this.isReady()) {
            LogUtil.i(TAG, "GPBillingMgr--querySubsSkuDetails--1");
            if (this.skuInfos == null || this.skuInfos.size() == 0) {
                return;
            }
            LogUtil.i(TAG, "GPBillingMgr--querySubsSkuDetails--2");
            ArrayList<QueryProductDetailsParams.Product> subsProductList = new ArrayList<>();
            for (int i = 0; i < this.skuInfos.size(); i++) {
                SkuInfo skuInfo = this.skuInfos.get(i);
                QueryProductDetailsParams.Product.Builder productBuilder = QueryProductDetailsParams.Product.newBuilder();
                productBuilder.setProductId(skuInfo.skuId);
                productBuilder.setProductType(skuInfo.skuType);
                if(BillingClient.ProductType.SUBS.equals(skuInfo.skuType)){
                    subsProductList.add(productBuilder.build());
                    LogUtil.i(TAG, "GPBillingMgr--querySubsSkuDetails--4--skuInfos:"+skuInfo.skuId+","+skuInfo.skuType);
                }
            }

            this.billingClient.queryProductDetailsAsync(
                    QueryProductDetailsParams.newBuilder()
                            .setProductList(subsProductList)
                            .build(),
                    this.mGPBillingListener);

            this.hasQuerySubsProductDetails = true;
        }
    }

    public ProductDetails getProductDetails(String productId){
        if (this.productDetailsMap.containsKey(productId)) {
            return this.productDetailsMap.get(productId);
        }
        return null;
    }

    public String getSkuDetails(String skuId) {
        LogUtil.i(TAG, "GPBillingMgr--getSkuDetails--skuId:"+skuId);
        ProductDetails productDetails = this.getProductDetails(skuId);
        if(productDetails != null){
            JSONObject jsonObject = this.serializeProductDetails(productDetails);
            return jsonObject.toJSONString();
        }
        return "";
    }

    public String getSkuPriceInfo(){
        LogUtil.i(TAG, "GPBillingMgr--getSkuPriceInfo--");
        try {
            LogUtil.i(TAG, "GPBillingMgr--getSkuPriceInfo--1-1");
            ArrayList<Object> list = new ArrayList<Object>();
            for(String key : this.productDetailsMap.keySet()){
                ProductDetails productDetails = this.productDetailsMap.get(key);
                JSONObject jsonObject = this.serializeProductDetails(productDetails);
                list.add(jsonObject);
            }
            return JSONObject.toJSONString(list);
        } catch (Exception exception) {
            LogUtil.i(TAG, "GPBillingMgr--getSkuPriceInfo--1-2");
            exception.printStackTrace();
        }
        return "";
    }

    public void endConnection() {
        LogUtil.i(TAG, "GPBillingMgr--endConnection");
        if (this.isReady()) {
            LogUtil.i(TAG, "GPBillingMgr--endConnection--1");
            this.billingClient.endConnection();
        }
    }

    /**
     * 查询所有订单信息
     */
    public void queryPurchases() {
        LogUtil.i(TAG, "GPBillingMgr--queryPurchases");
        if (this.isReady()) {
            LogUtil.i(TAG, "GPBillingMgr--queryPurchases--1");
            // 查询内购后再自动查询订阅
            this.queryPurchases(BillingClient.ProductType.INAPP);
        }
    }

    private void queryPurchases(String skuType) {
        LogUtil.i(TAG, "GPBillingMgr--queryPurchases1--skuType:"+skuType);
        if (this.isValidSkuType(skuType)) {
            LogUtil.i(TAG, "GPBillingMgr--queryPurchases1--1");
            QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                    .setProductType(skuType)
                    .build();

            this.billingClient.queryPurchasesAsync(params, new PurchasesResponseListener(){
                @Override
                public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                    LogUtil.i(TAG, "GPBillingMgr--queryPurchases1--1-1");
                    GPBillingMgr inst = GPBillingMgr.this;
                    final int code = billingResult.getResponseCode();
                    final String message = billingResult.getDebugMessage();
                    if (code == BillingClient.BillingResponseCode.OK) {
                        LogUtil.i(TAG, "GPBillingMgr--queryPurchases1--1-1-1");
                        if (null != list) {
                            for (int i = 0; i < list.size(); i++) {
                                Purchase purchase = list.get(i);
                                inst.savePurchase(purchase);
                                inst.saveHistoryPurchase(purchase);
                            }
                        }
                        if (BillingClient.ProductType.INAPP.equals(skuType)) {
                            LogUtil.i(TAG, "GPBillingMgr--queryPurchases1--1-1-1-1");
                            inst.queryPurchases(BillingClient.ProductType.SUBS);
                        } else {
                            LogUtil.i(TAG, "GPBillingMgr--queryPurchases1--1-1-1-2");
                            inst.onResult(QUERY_PURCHASES_SUCCESS, "query purchases success", code, message);
                        }
                    } else {
                        LogUtil.i(TAG, "GPBillingMgr--queryPurchases1--1-1-2");
                        inst.onResult(QUERY_PURCHASES_FAIL, "query purchases fail", code, message);
                    }
                }
            });
        }
    }

    /**
     * 是否是有效的商品类型
     * @param skuType
     * @return
     */
    private boolean isValidSkuType(String skuType) {
        LogUtil.i(TAG, "GPBillingMgr--isValidSkuType");
        return BillingClient.ProductType.INAPP.equals(skuType) || BillingClient.ProductType.SUBS.equals(skuType);
    }

    public void onResult(int code, String message) {
        LogUtil.i(TAG, "GPBillingMgr--onResult--code:"+code+","+message);
        this.onResult(code, message, 0, null);
    }

    public void onResult(int code, String message, int rawCode, String rawMessage) {
        LogUtil.i(TAG, "GPBillingMgr--onResult1--code:"+code+","+message+",rawCode:"+rawCode+","+rawMessage);
        this.onResult(code, message, rawCode, rawMessage, null);
    }

    public void onResult(int code, String message, int rawCode, String rawMessage, Hashtable<String, Object> data) {
        LogUtil.i(TAG, "GPBillingMgr--onResult2--code:"+code+","+message+",rawCode=" + rawCode + ", rawMessage=" + rawMessage);
        try {
            LogUtil.i(TAG, "GPBillingMgr--onResult2--1");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", code);
            jsonObject.put("message", message);
            if (data != null) {
                jsonObject.put("data", data);
            }
            String respone = jsonObject.toJSONString();
            LogUtil.i(TAG, "GPBillingMgr--onResult2--1-1:respone="+respone);
            if (this.billingResultListener != null) {
                LogUtil.i(TAG, "GPBillingMgr--onResult2--1-2");
                this.billingResultListener.onResult(respone);
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "GPBillingMgr--onResult2--2");
        }
    }

    public void onDestroy() {
        this.endConnection();
    }

    public void verifyTransactionReceip() {
        LogUtil.i(TAG, "GPBillingMgr--verifyTransactionReceip");
        this.onResult(VERIFY_RESULT, "验证成功");
    }

    public void saveProductDetails(List<ProductDetails> list){
        for (int i = 0; i < list.size(); i++) {
            ProductDetails productDetails = list.get(i);
            String productId = productDetails.getProductId();
            if (this.productDetailsMap.containsKey(productId)) {
                this.productDetailsMap.remove(productId);
            }
            this.productDetailsMap.put(productId, productDetails);
        }
        LogUtil.i(TAG, "GPBillingMgr--ProductDetailsResponseListener--onProductDetailsResponse--1-2--productDetailsMap.size="+this.productDetailsMap.size());
    }

    public void savePurchase(Purchase purchase) {
        LogUtil.i(TAG, "GPBillingMgr--savePurchase");
        if (this.purchases.contains(purchase)) {
            this.purchases.remove(purchase);
        }

        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged()) return;

        this.purchases.add(purchase);
    }

    public void saveHistoryPurchase(Purchase purchase) {
        LogUtil.i(TAG, "GPBillingMgr--saveHistoryPurchase");
        Purchase oldPurchase = null;
        for(Purchase _purchase : this.historyPurchases){
            if(_purchase.getPurchaseToken().equals(purchase.getPurchaseToken())){
                oldPurchase = _purchase;
                break;
            }
        }
        if(oldPurchase != null && oldPurchase != purchase){
            if(!oldPurchase.isAcknowledged() && purchase.isAcknowledged()){
                //状态更新了，获得
                this.removePurchase(oldPurchase.getPurchaseToken());
                this.historyPurchases.remove(oldPurchase);

                Hashtable<String, Object> data = this.serializeConsume(purchase);
                this.onResult(BillingMgr.CONSUME_SUBS_SUCCESS, "consume subs success", BillingClient.BillingResponseCode.OK, "consume subs success", data);

                GPBillingOnPaidEventUtils.onPaidEvent(purchase, this.getProductDetails(purchase.getProducts().get(0)));
            }
        }

        if (this.historyPurchases.contains(purchase)) {
            this.historyPurchases.remove(purchase);
        }
        this.historyPurchases.add(purchase);
    }

    public Purchase removePurchase(String purchaseToken) {
        LogUtil.i(TAG, "GPBillingMgr--removePurchase--purchaseToken:"+purchaseToken+",purchases.size:"+this.purchases.size());
        for (int i = 0; i < this.purchases.size(); i++) {
            Purchase purchase = this.purchases.get(i);
            if (purchase.getPurchaseToken().equals(purchaseToken)) {
                this.purchases.remove(purchase);
                return purchase;
            }
        }
        LogUtil.i(TAG, "GPBillingMgr--removePurchase--purchases.size:"+this.purchases.size());
        return null;
    }

    public String getPurchases() {
        LogUtil.i(TAG, "GPBillingMgr--getPurchases--");
        ArrayList<Object> purchasesList = new ArrayList<Object>();
        for (int i = 0; i < this.purchases.size(); i++) {
            Purchase purchase = this.purchases.get(i);
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                Hashtable<String, Object> data = this.serializePurchase(purchase);
                purchasesList.add(data);
            }
        }
        try {
            LogUtil.i(TAG, "GPBillingMgr--getPurchases--1");
            return JSONObject.toJSONString(purchasesList);
        } catch (Exception e) {
            LogUtil.i(TAG, "GPBillingMgr--getPurchases--2");
        }
        return  "";
    }

    public String getHistoryPurchases() {
        LogUtil.i(TAG, "GPBillingMgr--getHistoryPurchases--");
        ArrayList<Object> purchasesList = new ArrayList<Object>();
        for (int i = 0; i < this.historyPurchases.size(); i++) {
            Purchase purchase = this.historyPurchases.get(i);
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                Hashtable<String, Object> data = this.serializePurchase(purchase);
                purchasesList.add(data);
            }
        }
        try {
            return JSONObject.toJSONString(purchasesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "";
    }

    public JSONObject serializeProductDetails(ProductDetails productDetails){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("skuId", productDetails.getProductId());
        jsonObject.put("skuType", productDetails.getProductType());
        if(BillingClient.ProductType.INAPP.equals(productDetails.getProductType())){
            jsonObject.put("price", productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());
            jsonObject.put("priceCurrencyCode", productDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode());
        }else{
            JSONArray jsonArray = new JSONArray();
            for(ProductDetails.SubscriptionOfferDetails subscriptionDetail : productDetails.getSubscriptionOfferDetails()){
                int pricingPhaseListSize = subscriptionDetail.getPricingPhases().getPricingPhaseList().size();
                JSONObject subJsonObject = new JSONObject();
                subJsonObject.put("id", subscriptionDetail.getBasePlanId());
                subJsonObject.put("price", subscriptionDetail.getPricingPhases().getPricingPhaseList().get(pricingPhaseListSize - 1).getFormattedPrice());
                subJsonObject.put("priceCurrencyCode", subscriptionDetail.getPricingPhases().getPricingPhaseList().get(pricingPhaseListSize - 1).getPriceCurrencyCode());
                jsonArray.add(subJsonObject);
            }
            jsonObject.put("plans", jsonArray);
        }
        return jsonObject;
    }

    public Hashtable serializePurchase(Purchase purchase){
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("paymentID", purchase.getOrderId());
        data.put("skuId", purchase.getProducts().get(0));
        data.put("productID", purchase.getProducts().get(0));
        data.put("purchaseTime", (long)(purchase.getPurchaseTime()));
        data.put("purchaseToken", purchase.getPurchaseToken());
        data.put("acknowledged", purchase.isAcknowledged());

        AccountIdentifiers identifiers = purchase.getAccountIdentifiers();
        if(identifiers != null){
            data.put("planId", identifiers.getObfuscatedAccountId());
        }
        return data;
    }

    public Hashtable serializeConsume(Purchase purchase){
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("productID", purchase.getProducts().get(0));
        AccountIdentifiers identifiers = purchase.getAccountIdentifiers();
        if(identifiers != null){
            data.put("planId", identifiers.getObfuscatedAccountId());
        }

        return data;
    }
}
