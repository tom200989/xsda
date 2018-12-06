package xsda.xsda.ue.app;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.avos.avoscloud.AVOSCloud;
import com.jiagu.sdk.hiberProtected;
import com.jiagu.sdk.xsdakeyProtected;
import com.mob.MobSDK;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.xsdakey.keyUtil.leanCloudKey;

import org.xutils.x;

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
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化mobSDK
        MobSDK.init(this);
        // 初始化加固的SDK
        xsdakeyProtected.install(this);// leancloud key
        hiberProtected.install(this);// 框架
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
}

