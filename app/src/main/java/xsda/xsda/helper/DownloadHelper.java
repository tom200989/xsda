package xsda.xsda.helper;

import android.os.Environment;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetDataCallback;

import java.io.File;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

/**
 * Created by qianli.ma on 2018/6/27 0027.
 */

public class DownloadHelper extends GetDataCallback {

    public void download(AVFile file) {
        // 创建文件夹
        createDir();
        // 请求下载
        file.getDataInBackground(this);
    }

    /**
     * 创建文件夹
     */
    private void createDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        String path = sdcard.getAbsolutePath() + File.separator + Cons.ROOT_FILEPATH;
        File rootFile = new File(path);
        if (!rootFile.exists() | !rootFile.isDirectory()) {
            boolean isCreate = rootFile.mkdir();
            Lgg.t(Cons.TAG).ii("create the download dir: " + isCreate);
        } else {
            deleted(rootFile);
        }
    }

    /**
     * 删除文件
     *
     * @param rootFile 根目录
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
        } else {
            boolean mkdir = rootFile.mkdir();
            Lgg.t(Cons.TAG).ii("deleted first and create the download dir: " + mkdir);
        }
    }

    @Override
    public void done(byte[] bytes, AVException e) {
        
    }
}
