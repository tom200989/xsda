package xsda.xsda.ue.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.avos.avoscloud.AVOSCloud;
import com.jiagu.sdk.xsdakeyProtected;
import com.mob.MobSDK;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.tinkerpatch.sdk.server.callback.ConfigRequestCallback;
import com.xsdakey.keyUtil.leanCloudKey;

import org.xutils.x;

import java.util.HashMap;

import xsda.xsda.BuildConfig;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;

public class XsdaApplication extends MultiDexApplication {

    public static XsdaApplication app;
    public static String deviceId;// 设备ID
    public static IWXAPI api;

    public static XsdaApplication getApp() {
        if (app == null) {
            synchronized (XsdaApplication.class) {
                if (app == null) {
                    app = new XsdaApplication();
                }
            }
        }
        return app;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化mobSDK
        MobSDK.init(this);
        // 初始化加固的SDK
        xsdakeyProtected.install(this);// leancloud key
        //roothiberProtected.install(this);// 框架
        // tinker init
        initTinkerPatch();
        // 初始化获取设备ID
        deviceId = Sgg.getInstance(this).getString(Cons.SP_DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Ogg.getDeviceId(this);
            Sgg.getInstance(this).putString(Cons.SP_DEVICE_ID, deviceId);
        }
        Lgg.t(Cons.TAG).vv("application deviceId: " + deviceId);
        // 初始化LeanClound key
        String appid = leanCloudKey.getAPPID();
        String appkey = leanCloudKey.getAPPKEY();
        Lgg.t(Cons.TAG).ii("appid: " + appid + "; appkey: " + appkey);
        AVOSCloud.initialize(this, appid, appkey);
        // 开启LeanClound调试
        AVOSCloud.setDebugLogEnabled(true);
        // 初始化xutils框架
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志, 开启debug会影响性能.
    }

    /**
     * 我们需要确保至少对主进程跟patch进程初始化 TinkerPatch
     */
    private void initTinkerPatch() {
        // 我们可以从这里获得Tinker加载过程的信息
        if (BuildConfig.TINKER_ENABLE) {
            // 初始化TinkerPatch SDK
            TinkerPatch
                    // 初始化开始
                    .init(TinkerPatchApplicationLike.getTinkerPatchApplicationLike())
                    // 是否反射library路径        
                    .reflectPatchLibrary()
                    // 设置每次都会访问后台
                    .fetchPatchUpdate(true)
                    // 访问后台「补丁包」时间间隔1小时
                    .setFetchPatchIntervalByHours(1)
                    // 设置访问后台配置的回到监听(参数为true这每次调用都会访问后天)
                    .fetchDynamicConfig(new ConfigRequestCallback() {
                        @Override
                        public void onSuccess(HashMap<String, String> hashMap) {
                            if (hashMap.keySet().size() > 0) {
                                for (String key : hashMap.keySet()) {
                                    Lgg.t(Cons.TAG).ii("tinker--> " + key + " == " + hashMap.get(key));
                                }
                            }
                        }

                        @Override
                        public void onFail(Exception e) {
                            Lgg.t(Cons.TAG).ee("Application tinker error: " + e.getMessage());
                        }
                    }, true)
                    // 设置后台访问「配置」的时间间隔
                    .setFetchDynamicConfigIntervalByHours(1)
                    // 补丁合成成功, 锁屏重启
                    .setPatchRestartOnSrceenOff(false)
                    // 监听补丁合成回调
                    .setPatchResultCallback(patchResult -> Lgg.t(Cons.TAG).ii("tinker patch result:\n" + patchResult.toString()))
                    // 锁屏清除补丁,回滚
                    .setPatchRollbackOnScreenOff(true)
                    // 设置清除补丁监听
                    .setPatchRollBackCallback(() -> Lgg.t(Cons.TAG).ii("tinker had roll back"));

            // 获取当前的补丁版本
            Lgg.t(Cons.TAG).ii("Current patch version is " + TinkerPatch.with().getPatchVersion());

            // fetchPatchUpdateAndPollWithInterval 与 fetchPatchUpdate(false)
            // 不同的是，会通过handler的方式去轮询
            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
        }
    }
}

