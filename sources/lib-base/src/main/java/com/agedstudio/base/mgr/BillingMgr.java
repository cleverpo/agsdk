package com.agedstudio.base.mgr;

import android.app.Activity;

import java.util.ArrayList;

public interface BillingMgr {
    // 验证成功
    int SUCCESS = 0;
    // 失败
    int FAIL = 1;
    // 参数错误
    int PARAM_ERROR = 3;
    // 用户取消
    int USER_CANCELED = 4;
    // 初始化成功
    int INIT_SUCCESS = 5;
    // 初始化失败
    int INIT_FAIL = 6;
    // 网络错误
    int NETWORK_ERROR = 7;
    // 内购消费成功
    int CONSUME_INAPP_SUCCESS = 8;
    // 订阅消费成功
    int CONSUME_SUBS_SUCCESS = 9;
    // 查询商品详细信息成功
    int QUERY_SKU_DETAILS_SUCCESS = 10;
    // 查询商品详细信息失败
    int QUERY_SKU_DETAILS_FAIL = 11;
    // 查询有效的记录成功
    int QUERY_PURCHASES_SUCCESS = 12;
    // 查询有效的记录失败
    int QUERY_PURCHASES_FAIL = 13;
    // 再次购买非消耗性项目或者在订阅过期前再次购买订阅时出错。
    int ALREADY_PURCHASED = 14;
    // 购买成功
    int PURCHASE_SUCCESS = 15;// 验证结果
    // 验证结果
    int VERIFY_RESULT = 16;
    // 消耗失败
    int CONSUME_FAIL = 17;
    // 购买调起成功
    int PURCHASE_REQUEST_SUCCESS = 18;
    // 购买调起失败
    int PURCHASE_REQUEST_FAIL = 19;

    void init(Activity context);

    void startConnection();

    void endConnection();

    boolean isReady();

    void initSkuInfos(ArrayList<SkuInfo> skuInfos);

    void purchase(String skuId, String skuType, String orderIdOrPlanId);

    void consume(String skuId, String purchaseId, String skuType, String purchaseToken);

    void setListener(BillingResultListener billingResultListener);

    void querySkuDetails();

    String getSkuDetails(String skuId);

    String getSkuPriceInfo();

    void queryPurchases();

    void verifyTransactionReceip();

    String getPurchases();

    String getHistoryPurchases();

    public interface BillingResultListener {
        public void onResult(String jsonString);
    }

    public class SkuInfo {
        public String skuType;
        public String skuId;
    }
}
