package xsda.xsda.ue.app;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.avos.avoscloud.AVOSCloud;
import com.jiagu.sdk.roothiberProtected;

import org.xutils.x;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;

/**
 * Created by qianli.ma on 2018/6/20 0020.
 */

public class XsdaApplication extends MultiDexApplication {

    public static XsdaApplication app;
    public static String deviceId;// 设备ID

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
    public void onCreate() {
        super.onCreate();
        // 初始化获取设备ID
        deviceId = Sgg.getInstance(this).getString(Cons.SP_DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Ogg.getDeviceId(this);
            Sgg.getInstance(this).putString(Cons.SP_DEVICE_ID, deviceId);
        }
        Lgg.t(Cons.TAG).vv("application deviceId: " + deviceId);
        // 初始化LeanClound
        String appid = Ogg.readLeanCloudAppid(this);
        String appkey = Ogg.readLeanCloudAppkey(this);
        Lgg.t(Cons.TAG).ii("appid: " + appid + "; appkey: " + appkey);
        AVOSCloud.initialize(this, appid, appkey);
        // 开启LeanClound调试
        AVOSCloud.setDebugLogEnabled(true);
        // 初始化xutils框架
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志, 开启debug会影响性能.
        // 初始化框架SDK
        roothiberProtected.install(this);
    }
}
