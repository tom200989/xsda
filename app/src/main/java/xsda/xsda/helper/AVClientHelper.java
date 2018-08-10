package xsda.xsda.helper;
/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

import android.app.Activity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

public class AVClientHelper {

    private Activity activity;

    public AVClientHelper(Activity activity) {
        this.activity = activity;
    }

    public void close(AVIMClient client) {
        if (client != null) {
            client.close(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    activity.runOnUiThread(() -> {
                        if (e == null) {
                            closeSuccessNext(avimClient);
                        } else {
                            closeFailedNext(avimClient, e);
                        }
                    });

                }
            });
        }
    }

    /**
     * 设置单点登陆回调
     */
    public void setSinglePointLoginManager() {
        AVIMClient.setClientEventHandler(new SinglePointLoginManager());
        Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":setSinglePointLoginManager()");
    }

    /* -------------------------------------------- inner class -------------------------------------------- */

    /**
     * 单点登陆回调类
     */
    private class SinglePointLoginManager extends AVIMClientEventHandler {

        @Override
        public void onConnectionPaused(AVIMClient avimClient) { // 连接暂停
            Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":onConnectionPaused()");
            activity.runOnUiThread(() -> setSinglePointLoginConnectionPausedNext(avimClient));
        }

        @Override
        public void onConnectionResume(AVIMClient avimClient) {// 连接重启
            Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":onConnectionResume()");
            activity.runOnUiThread(() -> setSinglePointLoginConnectionResumeNext(avimClient));
        }

        @Override
        public void onClientOffline(AVIMClient avimClient, int code) {// 连接已离线
            Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":onClientOffline()");
            activity.runOnUiThread(() -> setSinglePointLoginConnectionOfflineNext(avimClient, code));
        }
    }

    /* -------------------------------------------- impl -------------------------------------------- */

    private OnSetSinglePointLoginConnectionOfflineListener onSetSinglePointLoginConnectionOfflineListener;

    // 接口OnSetSinglePointLoginConnectionOfflineListener
    public interface OnSetSinglePointLoginConnectionOfflineListener {
        void setSinglePointLoginConnectionOffline(AVIMClient client, int code);
    }

    // 对外方式setOnSetSinglePointLoginConnectionOfflineListener
    public void setOnSetSinglePointLoginConnectionOfflineListener(OnSetSinglePointLoginConnectionOfflineListener onSetSinglePointLoginConnectionOfflineListener) {
        this.onSetSinglePointLoginConnectionOfflineListener = onSetSinglePointLoginConnectionOfflineListener;
    }

    // 封装方法setSinglePointLoginConnectionOfflineNext
    private void setSinglePointLoginConnectionOfflineNext(AVIMClient client, int code) {
        if (onSetSinglePointLoginConnectionOfflineListener != null) {
            onSetSinglePointLoginConnectionOfflineListener.setSinglePointLoginConnectionOffline(client, code);
        }
    }

    private OnSetSinglePointLoginConnectionResumeListener onSetSinglePointConnectionResumeListener;

    // 接口OnSetSinglePointConnectionResumeListener
    public interface OnSetSinglePointLoginConnectionResumeListener {
        void setSinglePointLoginConnectionResume(AVIMClient client);
    }

    // 对外方式setOnSetSinglePointConnectionResumeListener
    public void setOnSetSinglePointConnectionResumeListener(OnSetSinglePointLoginConnectionResumeListener onSetSinglePointConnectionResumeListener) {
        this.onSetSinglePointConnectionResumeListener = onSetSinglePointConnectionResumeListener;
    }

    // 封装方法setSinglePointLoginConnectionResumeNext
    private void setSinglePointLoginConnectionResumeNext(AVIMClient client) {
        if (onSetSinglePointConnectionResumeListener != null) {
            onSetSinglePointConnectionResumeListener.setSinglePointLoginConnectionResume(client);
        }
    }

    private OnSetSinglePointLoginConnectionPausedListener onSetSinglePointLoginConnectionPausedListener;

    // 接口OnSetSinglePointLoginConnectionPausedListener
    public interface OnSetSinglePointLoginConnectionPausedListener {
        void setSinglePointLoginConnectionPaused(AVIMClient client);
    }

    // 对外方式setOnSetSinglePointLoginConnectionPausedListener
    public void setOnSetSinglePointLoginConnectionPausedListener(OnSetSinglePointLoginConnectionPausedListener onSetSinglePointLoginConnectionPausedListener) {
        this.onSetSinglePointLoginConnectionPausedListener = onSetSinglePointLoginConnectionPausedListener;
    }

    // 封装方法setSinglePointLoginConnectionPausedNext
    private void setSinglePointLoginConnectionPausedNext(AVIMClient client) {
        if (onSetSinglePointLoginConnectionPausedListener != null) {
            onSetSinglePointLoginConnectionPausedListener.setSinglePointLoginConnectionPaused(client);
        }
    }

    private OnCloseSuccessListener onCloseSuccessListener;

    // 接口OnCloseSuccessListener
    public interface OnCloseSuccessListener {
        void closeSuccess(AVIMClient client);
    }

    // 对外方式setOnCloseSuccessListener
    public void setOnCloseSuccessListener(OnCloseSuccessListener onCloseSuccessListener) {
        this.onCloseSuccessListener = onCloseSuccessListener;
    }

    // 封装方法closeSuccessNext
    private void closeSuccessNext(AVIMClient client) {
        if (onCloseSuccessListener != null) {
            onCloseSuccessListener.closeSuccess(client);
        }
    }

    private OnCloseFailedListener onCloseFailedListener;

    // 接口OnCloseFailedListener
    public interface OnCloseFailedListener {
        void closeFailed(AVIMClient avimClient, AVException e);
    }

    // 对外方式setOnCloseFailedListener
    public void setOnCloseFailedListener(OnCloseFailedListener onCloseFailedListener) {
        this.onCloseFailedListener = onCloseFailedListener;
    }

    // 封装方法closeFailedNext
    private void closeFailedNext(AVIMClient avimClient, AVException e) {
        if (onCloseFailedListener != null) {
            onCloseFailedListener.closeFailed(avimClient, e);
        }
    }
}
