package xsda.xsda.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.callback.AVServerDateCallback;

import java.util.Date;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

/**
 * Created by qianli.ma on 2018/7/13 0013.
 */

public class GetServerDateHelper {
    public void get() {
        AVOSCloud.getServerDateInBackground(new AVServerDateCallback() {
            @Override
            public void done(Date date, AVException e) {
                if (e != null) {
                    getServerErrorNext(e);
                    return;
                }
                getServerDateSuccessNext(date);
                long serverTime = date.getTime();
                Lgg.t(Cons.TAG).ii("serverTime: " + serverTime);
                getServerDateLongSuccessNext(serverTime);
            }
        });
    }

    private OnGetServerDateLongSuccessListener onGetServerDateLongSuccessListener;

    // 接口OnGetServerDateLongSuccessListener
    public interface OnGetServerDateLongSuccessListener {
        void getServerDateLongSuccess(long millute);
    }

    // 对外方式setOnGetServerDateLongSuccessListener
    public void setOnGetServerDateLongSuccessListener(OnGetServerDateLongSuccessListener onGetServerDateLongSuccessListener) {
        this.onGetServerDateLongSuccessListener = onGetServerDateLongSuccessListener;
    }

    // 封装方法getServerDateLongSuccessNext
    private void getServerDateLongSuccessNext(long millute) {
        if (onGetServerDateLongSuccessListener != null) {
            onGetServerDateLongSuccessListener.getServerDateLongSuccess(millute);
        }
    }

    private OnGetServerDateSuccessListener onGetServerDateSuccessListener;

    // 接口OnGetServerDateSuccessListener
    public interface OnGetServerDateSuccessListener {
        void getServerDateSuccess(Date date);
    }

    // 对外方式setOnGetServerDateSuccessListener
    public void setOnGetServerDateSuccessListener(OnGetServerDateSuccessListener onGetServerDateSuccessListener) {
        this.onGetServerDateSuccessListener = onGetServerDateSuccessListener;
    }

    // 封装方法getServerDateSuccessNext
    private void getServerDateSuccessNext(Date date) {
        if (onGetServerDateSuccessListener != null) {
            onGetServerDateSuccessListener.getServerDateSuccess(date);
        }
    }

    private OnGetServerErrorListener onGetServerErrorListener;

    // 接口OnGetServerErrorListener
    public interface OnGetServerErrorListener {
        void getServerError(Exception e);
    }

    // 对外方式setOnGetServerErrorListener
    public void setOnGetServerErrorListener(OnGetServerErrorListener onGetServerErrorListener) {
        this.onGetServerErrorListener = onGetServerErrorListener;
    }

    // 封装方法getServerErrorNext
    private void getServerErrorNext(Exception e) {
        if (onGetServerErrorListener != null) {
            onGetServerErrorListener.getServerError(e);
        }
    }
}
