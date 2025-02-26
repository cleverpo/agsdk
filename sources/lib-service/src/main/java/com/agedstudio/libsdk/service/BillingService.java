package com.agedstudio.libsdk.service;

import android.app.Activity;
import android.content.Context;

import com.agedstudio.base.AgedStudioSDKClass;
import com.agedstudio.base.events.AGEvents;
import com.agedstudio.base.mgr.BillingMgr;
import com.agedstudio.base.utils.CommonUtil;
import com.agedstudio.base.utils.LogUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

public class BillingService extends AgedStudioSDKClass {
    private static final String TAG = BillingService.class.getSimpleName();

    private static BillingService ins = null;

    private BillingMgr billingMgr = null;

    public void init(Context context, BillingMgr billingMgr) {
        super.init(context);
        LogUtil.d(TAG, "BillingService--init");
        BillingService.ins = this;

        LogUtil.d(TAG, "BillingService--init--2");
        this.billingMgr = billingMgr;
        if (null != this.billingMgr) {
            LogUtil.d(TAG, "BillingService--init--3");
            this.billingMgr.init((Activity) context);
            this.billingMgr.setListener(new BillingMgr.BillingResultListener() {
                @Override
                public void onResult(String jsonString) {
                    LogUtil.d(TAG, "BillingService--init--4:"+jsonString);
                    CommonUtil.runOnGameThread(new Runnable() {
                        @Override
                        public void run() {
                            CommonUtil.eventCallback(AGEvents.OnBillingResult + "('" + jsonString +"')");
                        }
                    });
                }
            });
        }
    }

    public static void startConnection() {
        LogUtil.i(TAG, "BillingService--startConnection");
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BillingService.ins.billingMgr.startConnection();
            }
        });
    }

    /**
     * 发起支付
     * @param json
     * skuId 商品id
     * skuType 商品类型 inapp 对应消耗型商品，subs 对应订阅型商品
     * orderId 订单id
     */
    public static void purchase(String json) {
        LogUtil.i(TAG, "BillingService--purchase:"+json);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.i(TAG, "BillingService-----purchase--1");
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    BillingService.ins.billingMgr.purchase(
                            jsonObject.getString("skuId"),
                            jsonObject.getString("skuType"),
                            jsonObject.getString("orderIdOrPlanId")
                    );
                } catch (Exception exception) {
                    LogUtil.i(TAG, "BillingService-----purchase--2");
                    exception.printStackTrace();
                }
            }
        });
    }

    /**
     * 判断是否支持应用内支付
     * @return
     */
    public static boolean isEnvReady() {
        return BillingService.ins.billingMgr.isReady();
    }


    public static void initSkuInfos(String json) {
        LogUtil.i(TAG, "BillingService-----initSkuInfos--json:"+json);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.i(TAG, "BillingService-----initSkuInfos--1");
                    ArrayList<BillingMgr.SkuInfo> skuInfos = new ArrayList<>();
                    JSONArray jsonArray = JSONObject.parseArray(json);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        BillingMgr.SkuInfo skuInfo = new BillingMgr.SkuInfo();
                        skuInfo.skuId = object.getString("skuId");
                        skuInfo.skuType = object.getString("skuType");
                        skuInfos.add(skuInfo);
                    }
                    BillingService.ins.billingMgr.initSkuInfos(skuInfos);
                } catch (Exception exception) {
                    LogUtil.i(TAG, "BillingService-----initSkuInfos--2");
                    exception.printStackTrace();
                }
            }
        });
    }

    /**
     * 对商品进行消耗
     * @param json
     * purchaseToken 支付完成后，通过支付成功回调返回的 JSON 串获取
     * skuType inapp 对应消耗型商品，subs 对应订阅型商品
     */
    public static void consume(String json) {
        LogUtil.i(TAG, "BillingService-----consume--json:"+json);
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.i(TAG, "BillingService-----consume--1");
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    String skuId = jsonObject.getString("skuId");
                    String purchaseId = jsonObject.getString("purchaseId");
                    String skuType = jsonObject.getString("skuType");
                    String purchaseToken = jsonObject.getString("purchaseToken");
                    BillingService.ins.billingMgr.consume(skuId, purchaseId, skuType, purchaseToken);
                } catch (Exception exception) {
                    LogUtil.i(TAG, "BillingService-----consume--2");
                    exception.printStackTrace();
                }
            }
        });
    }

    /**
     * 查询所有的商品信息
     * skuIds
     */
    public static void querySkuDetails() {
        LogUtil.i(TAG, "BillingService-----querySkuDetails--");
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.i(TAG, "BillingService-----querySkuDetails--1");
                    BillingService.ins.billingMgr.querySkuDetails();
                } catch (Exception exception) {
                    LogUtil.i(TAG, "BillingService-----querySkuDetails--2");
                    exception.printStackTrace();
                }
            }
        });
    }

    public static String getSkuDetails(String skuId) {
        LogUtil.i(TAG, "BillingService-----getSkuDetails--skuId:"+skuId);
        if (null != BillingService.ins.billingMgr) {
            LogUtil.i(TAG, "BillingService-----getSkuDetails--1");
            return BillingService.ins.billingMgr.getSkuDetails(skuId);
        }
        LogUtil.i(TAG, "BillingService-----getSkuDetails--2");
        return "";
    }

    public static String getSkuPriceInfo() {
        LogUtil.i(TAG, "BillingService-----getSkuPriceInfo--");
        if (null != BillingService.ins.billingMgr) {
            LogUtil.i(TAG, "BillingService-----getSkuPriceInfo--1");
            return BillingService.ins.billingMgr.getSkuPriceInfo();
        }
        LogUtil.i(TAG, "BillingService-----getSkuPriceInfo--2");
        return "";
    }

    public static void queryPurchases() {
        LogUtil.i(TAG, "BillingService-----queryPurchases");
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "BillingService-----queryPurchases--1");
                BillingService.ins.billingMgr.queryPurchases();
            }
        });
    }

    public static void verifyTransactionReceip() {
        LogUtil.i(TAG, "BillingService-----verifyTransactionReceip");
        CommonUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "BillingService-----verifyTransactionReceip--1");
                BillingService.ins.billingMgr.verifyTransactionReceip();
            }
        });
    }

    public static String getPurchases() {
        LogUtil.i(TAG, "BillingService-----getPurchases");
        return  BillingService.ins.billingMgr.getPurchases();
    }

    public static String getHistoryPurchases() {
        LogUtil.i(TAG, "BillingService-----getHistoryPurchases");
        return  BillingService.ins.billingMgr.getHistoryPurchases();
    }
}
