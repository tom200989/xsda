package xsda.xsda.ue.app;

import android.support.multidex.MultiDexApplication;

import com.avos.avoscloud.AVOSCloud;

import org.xutils.x;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;

/**
 * Created by qianli.ma on 2018/6/20 0020.
 */

public class XsdaApplication extends MultiDexApplication {

    public static XsdaApplication app;

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
        com.jiagu.sdk.roothiberProtected.install(this);
    }

}
