package xsda.xsda.helper;

import android.app.Activity;
import android.text.TextUtils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

/**
 * Created by qianli.ma on 2018/6/22 0022.
 */

public class PingHelper {

    private Thread pingThread;
    int i = 0;

    /**
     * 检测网络是否畅通: ping 地址
     *
     * @param address
     */
    public void ping(Activity activity, String address) {
        pingThread = new Thread(() -> {
            try {
                Process process = Runtime.getRuntime().exec("ping " + address);
                InputStreamReader r = new InputStreamReader(process.getInputStream());
                LineNumberReader returnData = new LineNumberReader(r);
                StringBuilder msg = new StringBuilder();
                String line = "";
                i = 0;
                while ((line = returnData.readLine()) != null & !TextUtils.isEmpty(line)) {
                    if (i <= 4) {
                        Lgg.t(Cons.TAG).ii(line);
                        msg.append(line).append("\n");
                        activity.runOnUiThread(() -> progressNext(i * 25));
                        i++;
                    } else {
                        i = 0;
                        break;
                    }
                }

                // 主线程操作
                activity.runOnUiThread(() -> {
                    if (msg.toString().contains("100% loss")) {
                        Lgg.t(Cons.TAG).ii("连接失败");
                        pingFailedNext(msg.toString());
                    } else if (TextUtils.isEmpty(msg.toString())) {
                        Lgg.t(Cons.TAG).ii("有网络但没有数据");
                        pingFailedNext(msg.toString());
                    } else {
                        Lgg.t(Cons.TAG).ii("连接畅通");
                        pingSuccessNext(msg.toString());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pingThread.start();
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
