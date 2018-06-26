package xsda.xsda.helper;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;

import java.io.File;

import xsda.xsda.R;
import xsda.xsda.utils.Tgg;

/**
 * Created by qianli.ma on 2018/6/26 0026.
 */

public class SDHelper {

    /**
     * 获取可用的空间大小
     *
     * @param context   环境
     * @param needSpace 需要的大小
     */
    public void getRemindMemory(Context context, long needSpace) {
        // 转换为KB_MB_GB字符
        String needSpace_KB_MB_GB = Formatter.formatFileSize(context, needSpace);
        //sdcard状态是否挂载
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Tgg.show(context, context.getString(R.string.sdcard_no_mounted), 3000);
            errorNext();
        } else {
            File sdcard_filedir = Environment.getExternalStorageDirectory();
            // 获取可用空间
            long usableSpace = sdcard_filedir.getUsableSpace();
            // 判断是否小于指定的大小
            if (usableSpace < needSpace) {
                String noRemainedSpace = String.format(context.getString(R.string.sdcard_no_remained_space), needSpace_KB_MB_GB);
                Tgg.show(context, noRemainedSpace, 3000);
                errorNext();
            } else {
                // 已挂载并有足够的空间
                normalNext();
            }
        }
    }

    private OnSdNormalListener onSdNormalListener;

    // 接口OnSdNormalListener
    public interface OnSdNormalListener {
        void normal();
    }

    // 对外方式setOnSdNormalListener
    public void setOnSdNormalListener(OnSdNormalListener onSdNormalListener) {
        this.onSdNormalListener = onSdNormalListener;
    }

    // 封装方法normalNext
    private void normalNext() {
        if (onSdNormalListener != null) {
            onSdNormalListener.normal();
        }
    }

    private OnSdErrorListener onSdErrorListener;

    // 接口OnSdErrorListener
    public interface OnSdErrorListener {
        void error();
    }

    // 对外方式setOnSdErrorListener
    public void setOnSdErrorListener(OnSdErrorListener onSdErrorListener) {
        this.onSdErrorListener = onSdErrorListener;
    }

    // 封装方法errorNext
    private void errorNext() {
        if (onSdErrorListener != null) {
            onSdErrorListener.error();
        }
    }
}
