- 
- AGSDK

  - initInApplication()
    
    在application调用初始化

  - initInActivity()
  
    在activity调用初始化

  - initInActivity()
  
    在activity调用初始化

  - initializeMobileAdsSdk(boolean hasUserConsent, int mediation) {

    初始化广告渠道，必须等cmp结束获得hasUserConsent再调用
    mediation：
    1.admob 2.yandex 3.max

  - setGLSurfaceView, onResume, onPause, onDestroy, onActivityResult, onNewIntent, onRestart, onStop, onBackPressed, onConfigurationChanged, onRestoreInstanceState, onSaveInstanceState, onStart, onLowMemory

    activity生命周期调用


- FirebaseAnalyticsService
  - onEvent(String name, String jsonStr)
    
    发送事件

  - updateConstent()

    用户CMP同意后调用

  - updateConsentNotRequired()

    用户CMP不同意后调用
  
  - 参考
    https://firebase.google.cn/docs/analytics?hl=zh-cn

  - 测试
    
    开启测试 adb shell setprop debug.firebase.analytics.app PACKAGE_NAME
    关闭测试 adb shell setprop debug.firebase.analytics.app .none.
  
    查看事件log
    adb shell setprop log.tag.FA VERBOSE
    adb shell setprop log.tag.FA-SVC VERBOSE
    adb logcat -v time -s FA FA-SVC

- AppsFlyerService
  - onEvent(String name, String jsonStr)
    
    发送事件

  - 参考
    https://dev.appsflyer.com/hc/docs/android-sdk
  
  - 测试
  
    debug包默认打开测试

- GameAnalyticsService
  - onEvent(String name, String jsonStr)
    
    发送事件
    
  - reportException(String location, String message, String stack)

    上传错误信息

  - 参考
    https://docs.gameanalytics.com/integrations/sdk/android/

  - 测试

    

- AdjustService
  - onEvent(String name, String jsonStr)
    
    发送事件
  
  - 参考
    pdf

  - 测试
    
- AnalyticsService
  - onEvent(String name, String jsonStr)
    
    发送事件

  - onAdjustEvent(String name, String jsonStr)
    
    发送adjust事件

  - updateConstent()
    
    用户CMP同意后调用

  - updateConsentNotRequired()
    
    用户CMP不同意后调用

  - reportException(String location, String message, String stack)

    上报错误

- CMPService
  - setDebug(boolean debug, String testDeviceHashedId)

    设置是否debug模式
    
  - showIfRequired()

    展示弹窗

  - reset()

    重置数据

  - boolean canRequestAds()

    是否可以请求广告

  - requestConsentInfoUpdate()

    请求cmp信息

  - String getPurposeConsents()

    获取同意状态

  - int getConsentStatus()

    获取同意状态

  - isConsentFormAvailable()

    是否可用
  
  - 参考
    https://developers.google.com/admob/android/privacy?hl=zh-cn

  - 测试
    在logcat中找出addTestDeviceHashedId，记录testDeviceId. 然后在AGSDK初始化后，调用CMPService.setDebug(true, "[testDeviceId]");

- BannerService
  - 回调事件
    - onBannerLoaded

      banner加载成功

    - onBannerLoadFail

      banner加载失败

    - onBannerShow
    
      banner展示

    - onBannerClose
    
      banner关闭

    - onBannerClick
    
      banner点击

  - initAd(String placementID, String params)

    初始化banner广告

  - load(String placementID)

    加载banner广告

  - showWithPosition(String placementID, String position)

    展示banner广告

  - hide(String placementID)

    隐藏banner广告

  - reshow(String placementID)

    重新展示banner广告

  - remove(String placementID)

    移除banner广告

  - boolean isReady(String placementID)

    banner广告是否ready

  - update(String placementID, float dt)

    刷新banner广告

  - setMediation(int type)

    设置banner广告聚合（1: admob, 2: yandex, 3: max）

  - boolean isTablet()

    是否tablet banner广告

  - int getAdHeight()

    返回banner高度

- IntertitalService
  - 回调事件
    - onInterstitialLoaded

      interstitial加载成功

    - onInterstitialLoadFail

      interstitial加载失败

    - onInterstitialShow
    
      interstitial展示

    - onInterstitialFailedToShow
    
      interstitial展示失败

    - onInterstitialClose
    
      interstitial关闭

    - onInterstitialClick
    
      interstitial点击

  - initAd(String placementID, String params)

    初始化interstitial广告

  - load(String placementID)

    加载interstitial广告

  - show(String placementID, String placement)

    展示interstitial广告

  - boolean isReady(String placementID)

    interstitial广告是否ready

  - update(String placementID, float dt)

    刷新interstitial广告

  - setMediation(int type)

    设置interstitial广告聚合（1: admob, 2: yandex, 3: max）


- RewardVideoService
  - 回调事件
    - onRewardVideoLoaded

      reward vedio加载成功

    - onRewardVideoLoadFail

      reward vedio加载失败

    - onRewardVideoShow
    
      reward vedio展示

    - onRewardVideoPlayFailed
    
      reward vedio展示失败

    - onRewardVideoClose
    
      reward vedio关闭

    - onRewardVideoClick
    
      reward vedio点击

    - onRewardVideoReward
    
      rewardvideo获得奖励

  - initAd(String placementID, String params)

    初始化reward vedio广告

  - load(String placementID)

    加载reward vedio广告

  - show(String placementID, String placement)

    展示reward vedio广告

  - boolean isReady(String placementID)

    reward vedio广告是否ready

  - update(String placementID, float dt)

    刷新reward vedio广告

  - setMediation(int type)

    设置reward vedio广告聚合（1: admob, 2: yandex, 3: max）

- SysService
  - 回调事件
    - OnReviewed

      评价是否调起

  - String getExternalFilesDir()

  - String getExternalCacheDir()

  - String getCacheDir()

  - setScreenBrightness(int brightness)

    设置屏幕亮度

  - int getScreenBrightness()

    获取屏幕亮度

  - hideSplash()

    游戏启动完成，隐藏启动页

  - openStore()

    打开商店

  - openAmazon()

    打开amazon商店

  - openGooglePlay()

    打开Google Play

  - openReview()

    打开评分

  - setKeepScreenOn(boolean value)

    保持屏幕常亮

  - String getChannelName()

    返回Channel Name

  - end()

    关闭

  - vibrate(int milliseconds)

    震动

  - stopVibrate()

    停止震动

  - String getVersionName()
  
    获取版本名

  - int getVersionCode() 

    获取版本号

  - String getModel()

    获取设备型号

  - String getBrand()
  
    获取设备厂商

  - boolean isDebugApp()
  
    是否是debug包

- BillingService
  - startConnection() 
    
    发起连接

  - purchase(String json)

    发起支付
    * @param json
    * skuId 商品id
    * skuType 商品类型 inapp 对应消耗型商品，subs 对应订阅型商品
    * orderIdOrPlanId 订单或planId

  - boolean isEnvReady() 
    
    判断支付环境是否准备
  
  - void initSkuInfos(String json)
    
    初始化商品列表
  
  - consume(String json)

    对商品进行消耗
     * 对商品进行消耗
     * @param json
     * skuId 商品id
     * purchaseId 支付id
     * purchaseToken 支付完成后，通过支付成功回调返回的 JSON 串获取
     * skuType inapp 对应消耗型商品，subs 对应订阅型商品
    
  - void querySkuDetails()

    查询所有的商品信息

  - String getSkuDetails(String skuId) 
    
    查询商品信息

  - String getSkuPriceInfo() 
  
    查询商品信息

  - queryPurchases()
   
    查询支付订单

  - verifyTransactionReceip()

    验证支付

  - String getPurchases() 
  
    获取支付订单信息

  - String getHistoryPurchases() 
  
    获取历史订单信息