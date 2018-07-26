package xsda.xsda.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

/**
 * Created by qianli.ma on 2018/6/27 0027.
 */

public class InstallApkHelper {

    /**
     * 执行安装
     *
     * @param activity 环境
     * @param apkName  apk名: xsda.apk
     */
    public void install(Activity activity, String apkName) {

        try {
            // 1.得到文件路径: /store/0/mnt/xsdaApk/2r2fddsff23r23rw.apkFile
            File sdcard = Environment.getExternalStorageDirectory();
            String rootPath = sdcard.getAbsolutePath() + File.separator + Cons.INSTALL_FILEPATH;
            String apkPath = rootPath + File.separator + apkName;
            File apkFile = new File(apkPath);
            
            // 判断apk包是否存在
            if (!apkFile.exists()) {
                installErrorNext(null);
                return;
            }

            //判读版本是否在7.0 (android N) 以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri apkUri = FileProvider.getUriForFile(activity, activity.getPackageName(), apkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                activity.startActivity(intent);

            } else {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Lgg.t(Cons.TAG).ee(getClass().getSimpleName() + ":install()--> install error: " + e.getMessage());
            installErrorNext(e);
        }
    }

    private OnInstallErrorListener onInstallErrorListener;

    // 接口OnInstallErrorListener
    public interface OnInstallErrorListener {
        void installError(Exception e);
    }

    // 对外方式setOnInstallErrorListener
    public void setOnInstallErrorListener(OnInstallErrorListener onInstallErrorListener) {
        this.onInstallErrorListener = onInstallErrorListener;
    }

    // 封装方法installErrorNext
    private void installErrorNext(Exception e) {
        if (onInstallErrorListener != null) {
            onInstallErrorListener.installError(e);
        }
    }
}
