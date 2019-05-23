package xsda.xsda.helper;

import android.app.Activity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.request.UriRequest;
import org.xutils.x;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

/**
 * Created by qianli.ma on 2018/6/22 0022.
 */

public class PingHelper {

    int i = 0;

    /**
     * 检测网络是否畅通: ping 地址
     *
     * @param address
     */
    public void ping(Activity activity, String address) {
        RequestParams params = new RequestParams(address);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void responseBody(UriRequest uriRequest) {
                
            }

            @Override
            public void onSuccess(String result) {
                // 记住主线程
                activity.runOnUiThread(() -> {
                    if (i <= 4) {
                        Lgg.t(Cons.TAG).ii("ping : " + i * 25 + "%");
                        progressNext(i * 25);
                        i++;
                        ping(activity, address);
                    } else {
                        Lgg.t(Cons.TAG).ii("ping success");
                        pingSuccessNext("连接成功");
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                // 记住主线程
                activity.runOnUiThread(() -> {
                    Lgg.t(Cons.TAG).ii("ping failed: " + ex.getMessage());
                    pingFailedNext(ex.getMessage());
                });
            }

            @Override
            public void onCancelled(CancelledException cex) {
                // 记住主线程
                activity.runOnUiThread(() -> {
                    Lgg.t(Cons.TAG).ii("ping cancel: " + cex.getMessage());
                    pingFailedNext(cex.getMessage());
                });
            }

            @Override
            public void onFinished() {
                activity.runOnUiThread(() -> {

                });
            }
        });
    }

    private OnProgressListener onProgressListener;

    // 接口OnProgressListener
    public interface OnProgressListener {
        void progress(int progress);
    }

    // 对外方式setOnProgressListener
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    // 封装方法progressNext
    private void progressNext(int progress) {
        if (onProgressListener != null) {
            onProgressListener.progress(progress);
        }
    }

    private OnPingFailedListener onPingFailedListener;

    // 接口OnPingFailedListener
    public interface OnPingFailedListener {
        void pingFailed(String msg);
    }

    // 对外方式setOnPingFailedListener
    public void setOnPingFailedListener(OnPingFailedListener onPingFailedListener) {
        this.onPingFailedListener = onPingFailedListener;
    }

    // 封装方法pingFailedNext
    private void pingFailedNext(String msg) {
        if (onPingFailedListener != null) {
            onPingFailedListener.pingFailed(msg);
        }
    }

    private OnPingSuccessListener onPingSuccessListener;

    // 接口OnPingSuccessListener
    public interface OnPingSuccessListener {
        void pingSuccess(String msg);
    }

    // 对外方式setOnPingSuccessListener
    public void setOnPingSuccessListener(OnPingSuccessListener onPingSuccessListener) {
        this.onPingSuccessListener = onPingSuccessListener;
    }

    // 封装方法pingSuccessNext
    private void pingSuccessNext(String msg) {
        if (onPingSuccessListener != null) {
            onPingSuccessListener.pingSuccess(msg);
        }
    }
}
