package com.agedstudio.base.utils;


public class AdUtil {
    /**
     * 生成一条json格式的消息
     * @param code
     * @param message
     * @return
     */
    public static String genJsonMsg(int code, String message) {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        return jsonObject.toJSONString();
    }

    public static void onCallback(final String method, String placementID) {
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                CommonUtil.eventCallback(method + "('" + placementID + "');");
            }
        });
    }

    public static void onCallback(String method, String placementID, int code, String message) {
        CommonUtil.runOnGameThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i("AdUtil","AdUtil--onCallback--method:" + method+",placementID:"+placementID+",code:"+code+",message:"+message);
                String msg = AdUtil.genJsonMsg(code, message);
                CommonUtil.eventCallback(method + "('" + placementID + "','" + msg + "');");
            }
        });
    }
}
