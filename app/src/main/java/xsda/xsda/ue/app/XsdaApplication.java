package xsda.xsda.ue.app;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.avos.avoscloud.AVOSCloud;
import com.jiagu.sdk.roothiberProtected;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;

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
    private ApplicationLike tinkerApplicationLike;

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
        // 初始化tinker
        initTinker();
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

    /**
     * 初始化tinker
     */
    private void initTinker() {
        Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":initTinker()");
        // 1.我们可以从这里获得Tinker加载过程的信息
        tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
        // 2.初始化TinkerPatch SDK
        TinkerPatch.init(tinkerApplicationLike)// 2.1.开始初始化
                .reflectPatchLibrary()// 2.2.反射库
                .setPatchRollbackOnScreenOff(true)// 2.3.设置回滚
                .setPatchRestartOnSrceenOff(true)// 2.4.设置重启
                .setFetchPatchIntervalByHours(3);// 2.5.设置轮询时间
        // 3.每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
    }
}
