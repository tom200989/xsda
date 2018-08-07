package xsda.xsda.helper;
/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

import android.app.Activity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

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

    /* -------------------------------------------- impl -------------------------------------------- */
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
