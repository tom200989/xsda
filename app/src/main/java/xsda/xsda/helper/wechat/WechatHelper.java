package xsda.xsda.helper.wechat;

import android.app.Activity;
import android.text.TextUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import xsda.xsda.utils.Lgg;

/*
 * Created by qianli.ma on 2018/9/11 0011.
 */
public class WechatHelper {

    private Platform platform;// 微信信息对象
    private Activity activity;
    private String TAG = "WechatHelper";

    public WechatHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 初始化授权
     */
    public Platform initCheckAuthorized() {
        Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":initCheckAuthorized()");
        // 获取微信平台
        platform = ShareSDK.getPlatform(Wechat.NAME);
        // 判断是否已经安装微信
        if (platform.isClientValid()) {
            hadInstallWechatNext();
            isAuthorize(platform);
            Lgg.t(TAG).ii("wechat had install");
        }
        return platform;
    }

    /**
     * 调起授权(主方法: 用于用户初次点击登陆按钮登陆,跳转到授权界面)
     */
    public void clickAuthorized() {
        if (platform != null) {
            if (!platform.isClientValid()) {
                wechatNotInstallNext();
                Lgg.t(TAG).ww("wechat had not install");
            } else {
                // 申请获取用户的信息(只要微信用户数据,不要微信自带的登陆功能)
                hadInstallWechatNext();
                platform.showUser(null);
                Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":clickAuthorized()");
            }
        }
    }

    /**
     * 授权操作
     */
    private void isAuthorize(final Platform plat) { 
        Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":isAuthorize()");
        // 1.封装微信信息
        WechatBean wechatBean = WechatBeanHandler.handle(null, platform);
        // 2.判断是否 [ 已经授权登陆并且没有退出 ](类似于自动登录)
        if (plat.isAuthValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                /* 已授权 */
                hadAuthorizedNext(wechatBean);
                Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":isAuthorize()--> hadAuthorized");
                return;
            }
        } else {
            /* 没有授权 */
            noAuthorizedNext(wechatBean);
            Lgg.t(TAG).ww("Method--> " + getClass().getSimpleName() + ":isAuthorize()--> noAuthorized");
        }

        // 3.授权监听
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
                /* 授权完成 */
                if (action == Platform.ACTION_USER_INFOR) {
                    WechatBean wechatBean = WechatBeanHandler.handle(res, platform);
                    authorizedCompleteNext(wechatBean);
                    Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":isAuthorize()--> authorizedComplete");
                }
            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {
                /* 授权错误 */
                WechatBean wechatBean = WechatBeanHandler.handle(null, platform);
                authorizedErrorNext(wechatBean, throwable);
                Lgg.t(TAG).ee("Method--> " + getClass().getSimpleName() + ":isAuthorize()--> authorizedError: " + throwable.getMessage());
            }

            @Override
            public void onCancel(Platform platform, int action) {
                /* 授权取消 */
                WechatBean wechatBean = WechatBeanHandler.handle(null, platform);
                authorizedCancelNext(wechatBean);
                Lgg.t(TAG).ww("Method--> " + getClass().getSimpleName() + ":isAuthorize()--> authorizedCancel");
            }
        });

        // 4.不启用单点登陆
        platform.SSOSetting(true);
    }

    /* -------------------------------------------- impl -------------------------------------------- */

    private OnHadInstallWechatListener onHadInstallWechatListener;

    // Inteerface--> 接口OnHadInstallWechatListener
    public interface OnHadInstallWechatListener {
        void hadInstallWechat();
    }

    // 对外方式setOnHadInstallWechatListener
    public void setOnHadInstallWechatListener(OnHadInstallWechatListener onHadInstallWechatListener) {
        this.onHadInstallWechatListener = onHadInstallWechatListener;
    }

    // 封装方法hadInstallWechatNext
    private void hadInstallWechatNext() {
        if (onHadInstallWechatListener != null) {
            onHadInstallWechatListener.hadInstallWechat();
        }
    }

    private OnWeChatNotInstallListener onWeChatNotInstallListener;

    // Inteerface--> 接口OnWeChatNotInstallListener
    public interface OnWeChatNotInstallListener {
        void wechatNotInstall();
    }

    // 对外方式setOnWeChatNotInstallListener
    public void setOnWeChatNotInstallListener(OnWeChatNotInstallListener onWeChatNotInstallListener) {
        this.onWeChatNotInstallListener = onWeChatNotInstallListener;
    }

    // 封装方法wechatNotInstallNext
    private void wechatNotInstallNext() {
        if (onWeChatNotInstallListener != null) {
            onWeChatNotInstallListener.wechatNotInstall();
        }
    }

    private OnAuthorizedCancelListener onAuthorizedCancelListener;

    // Inteerface--> 接口OnAuthorizedCancelListener
    public interface OnAuthorizedCancelListener {
        void authorizedCancel(WechatBean wechatBean);
    }

    // 对外方式setOnAuthorizedCancelListener
    public void setOnAuthorizedCancelListener(OnAuthorizedCancelListener onAuthorizedCancelListener) {
        this.onAuthorizedCancelListener = onAuthorizedCancelListener;
    }

    // 封装方法authorizedCancelNext
    private void authorizedCancelNext(WechatBean wechatBean) {
        if (onAuthorizedCancelListener != null) {
            activity.runOnUiThread(() -> onAuthorizedCancelListener.authorizedCancel(wechatBean));
        }
    }

    private OnAuthorizedErrorListener onAuthorizedErrorListener;

    // Inteerface--> 接口OnAuthorizedErrorListener
    public interface OnAuthorizedErrorListener {
        void authorizedError(WechatBean wechatBean, Throwable throwable);
    }

    // 对外方式setOnAuthorizedErrorListener
    public void setOnAuthorizedErrorListener(OnAuthorizedErrorListener onAuthorizedErrorListener) {
        this.onAuthorizedErrorListener = onAuthorizedErrorListener;
    }

    // 封装方法authorizedErrorNext
    private void authorizedErrorNext(WechatBean wechatBean, Throwable throwable) {
        if (onAuthorizedErrorListener != null) {
            activity.runOnUiThread(() -> onAuthorizedErrorListener.authorizedError(wechatBean, throwable));
        }
    }

    private OnAuthorizedCompleteListener onAuthorizedCompleteListener;

    // Inteerface--> 接口OnAuthorizedCompleteListener
    public interface OnAuthorizedCompleteListener {
        void authorizedComplete(WechatBean wechatBean);
    }

    // 对外方式setOnAuthorizedCompleteListener
    public void setOnAuthorizedCompleteListener(OnAuthorizedCompleteListener onAuthorizedCompleteListener) {
        this.onAuthorizedCompleteListener = onAuthorizedCompleteListener;
    }

    // 封装方法authorizedCompleteNext
    private void authorizedCompleteNext(WechatBean wechatBean) {
        if (onAuthorizedCompleteListener != null) {
            activity.runOnUiThread(() -> onAuthorizedCompleteListener.authorizedComplete(wechatBean));
        }
    }

    private OnNoAuthorizedListener onNoAuthorizedListener;

    // Inteerface--> 接口OnNoAuthorizedListener
    public interface OnNoAuthorizedListener {
        void noAuthorized(WechatBean wechatBean);
    }

    // 对外方式setOnNoAuthorizedListener
    public void setOnNoAuthorizedListener(OnNoAuthorizedListener onNoAuthorizedListener) {
        this.onNoAuthorizedListener = onNoAuthorizedListener;
    }

    // 封装方法noAuthorizedNext
    private void noAuthorizedNext(WechatBean wechatBean) {
        if (onNoAuthorizedListener != null) {
            onNoAuthorizedListener.noAuthorized(wechatBean);
        }
    }

    private OnHadAuthorizedListener onHadAuthorizedListener;

    // Inteerface--> 接口OnHadAuthorizedListener
    public interface OnHadAuthorizedListener {
        void hadAuthorized(WechatBean wechatBean);
    }

    // 对外方式setOnHadAuthorizedListener
    public void setOnHadAuthorizedListener(OnHadAuthorizedListener onHadAuthorizedListener) {
        this.onHadAuthorizedListener = onHadAuthorizedListener;
    }

    // 封装方法hadAuthorizedNext
    private void hadAuthorizedNext(WechatBean wechatBean) {
        if (onHadAuthorizedListener != null) {
            activity.runOnUiThread(() -> onHadAuthorizedListener.hadAuthorized(wechatBean));
        }
    }

}
