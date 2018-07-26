package xsda.xsda.helper;

import android.os.Environment;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;

import java.io.File;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;

/**
 * Created by qianli.ma on 2018/6/27 0027.
 */

public class DownloadHelper {

    /**
     * 下载
     *
     * @param file 文件对象
     */
    public void download(AVFile file) {
        // 创建文件夹
        File installDir = getInstallDir();
        createDir(installDir);
        preDownloadNext();
        file.clearCachedFile();
        // 请求下载
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                if (e == null) {
                    // 下载完毕保存到文件夹
                    File apk = Ogg.byte2File(bytes, installDir.getAbsolutePath(), file.getName() + ".apk");
                    downFinishNext(apk);
                    Lgg.t(Cons.TAG).ii("download new version finish");
                } else {
                    downErrorNext(e);
                    Egg.print(getClass().getSimpleName(), "download", e, null);
                }

            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer progress) {
                getProgressNext(progress);
                if (progress % 20 == 0) {
                    Lgg.t(Cons.TAG).ii("current progress: " + progress);
                }
            }
        });
    }

    /**
     * 创建文件夹
     */
    private void createDir(File rootDir) {
        // 不存在则创建
        if (!rootDir.exists() | !rootDir.isDirectory()) {
            boolean isCreate = rootDir.mkdir();
            Lgg.t(Cons.TAG).ii("create the download dir: " + isCreate);
        } else {
            // 存在则删除文件夹里内容
            deleted(rootDir);
        }
    }

    /**
     * @return 获取根目录对象
     */
    private File getInstallDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        String path = sdcard.getAbsolutePath() + File.separator + Cons.INSTALL_FILEPATH;
        return new File(path);
    }

    /**
     * 删除文件
     *
     * @param rootFile 根目录
     * @apiNote call by createDir()
     */
    private void deleted(File rootFile) {
        File[] files = rootFile.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleted(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    private OnPreDownloadListener onPreDownloadListener;

    // 接口OnPreDownloadListener
    public interface OnPreDownloadListener {
        void preDownload();
    }

    // 对外方式setOnPreDownloadListener
    public void setOnPreDownloadListener(OnPreDownloadListener onPreDownloadListener) {
        this.onPreDownloadListener = onPreDownloadListener;
    }

    // 封装方法preDownloadNext
    private void preDownloadNext() {
        if (onPreDownloadListener != null) {
            onPreDownloadListener.preDownload();
        }
    }

    private OnDownErrorListener onDownErrorListener;

    // 接口OnDownErrorListener
    public interface OnDownErrorListener {
        void downError(Exception e);
    }

    // 对外方式setOnDownErrorListener
    public void setOnDownErrorListener(OnDownErrorListener onDownErrorListener) {
        this.onDownErrorListener = onDownErrorListener;
    }

    // 封装方法downErrorNext
    private void downErrorNext(Exception e) {
        if (onDownErrorListener != null) {
            onDownErrorListener.downError(e);
        }
    }

    private OnDownFinishListener onDownFinishListener;

    // 接口OnDownFinishListener
    public interface OnDownFinishListener {
        void downFinish(File apk);
    }

    // 对外方式setOnDownFinishListener
    public void setOnDownFinishListener(OnDownFinishListener onDownFinishListener) {
        this.onDownFinishListener = onDownFinishListener;
    }

    // 封装方法downFinishNext
    private void downFinishNext(File apk) {
        if (onDownFinishListener != null) {
            onDownFinishListener.downFinish(apk);
        }
    }

    private OnProgressListener onProgressListener;

    // 接口OnProgressListener
    public interface OnProgressListener {
        void getProgress(int progress);
    }

    // 对外方式setOnProgressListener
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    // 封装方法getProgressNext
    private void getProgressNext(int progress) {
        if (onProgressListener != null) {
            onProgressListener.getProgress(progress);
        }
    }
}
