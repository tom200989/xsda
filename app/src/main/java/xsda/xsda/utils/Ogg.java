package xsda.xsda.utils;

import android.content.Context;

import com.liyi.liyiutils.main.LiyiEncryty;

import java.io.InputStream;

import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/6/22 0022.
 */

public class Ogg {

    /**
     * 把流转换成字符
     *
     * @param ins 原始流
     * @return 字符
     */
    public static String streamToString(InputStream ins) {
        byte[] b = new byte[1024];
        StringBuilder sb = new StringBuilder();
        int len;
        try {
            while ((len = ins.read(b)) != -1) {
                sb.append(new String(b, 0, len));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取LeanClound: APPID
     *
     * @param context 环境
     * @return LeanClound: APPID
     */
    public static String readLeanCloudAppid(Context context) {
        // 获取appid
        InputStream in_appid = context.getResources().openRawResource(R.raw.param0);
        InputStream in_private = context.getResources().openRawResource(R.raw.param1);
        String en_appid = Ogg.streamToString(in_appid);
        String privateKey = Ogg.streamToString(in_private);
        return LiyiEncryty.decodeByPrivate(en_appid, privateKey);
    }

    /**
     * 获取LeanClound: APPKEY
     *
     * @param context 环境
     * @return LeanClound: APPKEY
     */
    public static String readLeanCloudAppkey(Context context) {
        // 获取appid
        InputStream in_appkey = context.getResources().openRawResource(R.raw.param5);
        InputStream in_private = context.getResources().openRawResource(R.raw.param3);
        String en_appkey = Ogg.streamToString(in_appkey);
        String privateKey = Ogg.streamToString(in_private);
        return LiyiEncryty.decodeByPrivate(en_appkey, privateKey);
    }

}
