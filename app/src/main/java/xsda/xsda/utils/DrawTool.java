package xsda.xsda.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import xsda.xsda.R;

public class DrawTool {
    
    // 默认图片资源
    private static int DefaultDrawId = R.drawable.ic_launcher_background;

    /**
     * 字节 --> 输入流
     * byte --> InputStream
     *
     * @param bytes 字节
     * @return 输入流
     */
    public static InputStream Byte2InputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    /**
     * 输入流 --> 字节
     * InputStream --> byte
     *
     * @param in 输入流
     * @return 字节
     */
    public byte[] InputStream2Bytes(InputStream in) {
        StringBuilder builder = new StringBuilder();
        byte[] bytes = new byte[1024];
        try {
            while (in.read(bytes, 0, 1024) != -1) {
                builder.append(new String(bytes).trim());
            }
            return builder.toString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 位图 --> 输入流
     * Bitmap --> InputStream (JPEG, quality: 100)
     *
     * @param bitmap 图元
     * @return 输入流
     */
    public static InputStream Bitmap2InputStream(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * 位图 --> 输入流
     * Bitmap --> InputStream (PNG, quality: 自定义)
     *
     * @param bitmap  图元
     * @param quality 图片质量
     * @return 输入流
     */
    public static InputStream Bitmap2InputStream(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * 输入流 --> 位图
     * InputStream --> Bitmap
     *
     * @param inputStream 输入流
     * @return 位图
     */
    public static Bitmap InputStream2Bitmap(InputStream inputStream) {
        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * 图元 --> 输入流
     * Drawable --> InputStream
     *
     * @param drawable 图元
     * @return 输入流
     */
    public InputStream Drawable2InputStream(Drawable drawable) {
        Bitmap bitmap = Drawable2Bitmap(drawable);
        return Bitmap2InputStream(bitmap);
    }

    /**
     * 输入流 --> 图元
     * InputStream --> Drawable
     *
     * @param inputStream 输入流
     * @return 图元
     */
    public static Drawable InputStream2Drawable(InputStream inputStream) {
        Bitmap bitmap = InputStream2Bitmap(inputStream);
        return Bitmap2Drawable(bitmap);
    }

    /**
     * 图元 --> 字节
     * Drawable --> byte
     *
     * @param drawable 图元
     * @return 字节
     */
    public byte[] Drawable2Bytes(Drawable drawable) {
        return Bitmap2Bytes(Drawable2Bitmap(drawable));
    }

    /**
     * 字节 --> 图元
     * byte --> Drawable
     *
     * @param bytes 字节
     * @return 图元
     */
    public Drawable Bytes2Drawable(byte[] bytes) {
        return Bitmap2Drawable(Bytes2Bitmap(bytes));
    }

    /**
     * 位图 --> 字节
     * Bitmap --> byte
     *
     * @param bitmap 图元
     * @return 字节
     */
    public static byte[] Bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 字节 --> 位图
     * byte --> Bitmap
     *
     * @param bytes 字节
     * @return 位图
     */
    public Bitmap Bytes2Bitmap(byte[] bytes) {
        if (bytes.length != 0) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    /**
     * 图元 --> 位图
     * Drawable --> Bitmap
     *
     * @param drawable 图元
     * @return 位图
     */
    public static Bitmap Drawable2Bitmap(Drawable drawable) {
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 位图 --> 图元
     * Bitmap --> Drawable
     *
     * @param bitmap 位图
     * @return 图元
     */
    public static Drawable Bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * 文件 --> 字节
     * File --> byte
     *
     * @param filePath 文件路径
     * @return 字节
     */
    public static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 字节 --> 文件
     * byte --> file
     *
     * @param bytes    字节
     * @param fileDir  文件目录
     * @param fileName 文件名
     * @return 文件
     */
    public static File Byte2File(byte[] bytes, String fileDir, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(fileDir);
            if (!dir.exists() | !dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(fileDir + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 获取缩略图 (File path)
     *
     * @param context  域
     * @param filePath 图片地址
     * @param scale    缩小比例, 如: 0.01, 0.5
     * @return 位图
     */
    public static Bitmap File2ThumboBitmap(Context context, String filePath, @FloatRange(from = 0.0001, to = 10000) float scale) {
        // 转换为option倍数
        scale = scale <= 0 ? 4 : scale;
        int scaleOption = (int) (1f / scale);
        // 设定默认(无法正常显示时的图片)
        Bitmap bitmap = Drawable2Bitmap(context.getResources().getDrawable(DefaultDrawId));
        try {
            // 1.加载位图
            InputStream inputStream = new FileInputStream(filePath);
            //2.为位图设置100K的缓存
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inTempStorage = new byte[100 * 1024];
            //3.设置位图颜色显示优化方式
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            //4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
            options.inPurgeable = true;
            //5.设置位图缩放比例
            options.inSampleSize = scaleOption <= 0 ? 4 : scaleOption;
            //6.设置解码位图的尺寸信息
            options.inInputShareable = true;
            //7.解码位图
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取缩略图 (inputstream)
     *
     * @param context     域
     * @param inputStream 图片流
     * @param scale       缩小比例, 如: 0.01, 0.5
     * @return 位图
     */
    public static Bitmap Stream2ThumboBitmap(Context context, InputStream inputStream, @FloatRange(from = 0.0001, to = 10000) float scale) {
        // 转换为option倍数
        scale = scale <= 0 ? 4 : scale;
        int scaleOption = (int) (1f / scale);
        // 设定默认(无法正常显示时的图片)
        Bitmap bitmap = Drawable2Bitmap(context.getResources().getDrawable(DefaultDrawId));
        try {
            // 1.加载位图
            //2.为位图设置100K的缓存
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inTempStorage = new byte[100 * 1024];
            //3.设置位图颜色显示优化方式
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            //4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
            opts.inPurgeable = true;
            //5.设置位图缩放比例
            opts.inSampleSize = scaleOption <= 0 ? 4 : scaleOption;
            //6.设置解码位图的尺寸信息
            opts.inInputShareable = true;
            //7.解码位图
            bitmap = BitmapFactory.decodeStream(inputStream, null, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取缩略图 (bitmap)
     *
     * @param context 域
     * @param bitmap  图片源
     * @param scale   缩小比例, 如: 0.01, 0.5
     * @return 位图
     */
    public static Bitmap Bitmap2ThumboBitmap(Context context, Bitmap bitmap, @FloatRange(from = 0.0001, to = 10000) float scale) {
        // 转换为option倍数
        scale = scale <= 0 ? 4 : scale;
        int scaleOption = (int) (1f / scale);
        // 设定默认(无法正常显示时的图片)
        Bitmap newBitmap = Drawable2Bitmap(context.getResources().getDrawable(DefaultDrawId));
        try {
            // 1.加载位图
            InputStream is = Bitmap2InputStream(bitmap);
            //2.为位图设置100K的缓存
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inTempStorage = new byte[100 * 1024];
            //3.设置位图颜色显示优化方式
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            //4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
            opts.inPurgeable = true;
            //5.设置位图缩放比例
            opts.inSampleSize = scaleOption <= 0 ? 4 : scaleOption;
            //6.设置解码位图的尺寸信息
            opts.inInputShareable = true;
            //7.解码位图
            newBitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newBitmap;
    }

    /**
     * 获取适配手机屏幕的缩略图
     *
     * @param activity 域
     * @param filePath 图片地址
     * @return 位图
     */
    public static Bitmap File2FitPhoneBitmap(Activity activity, String filePath) {
        // 0.设定默认(无法正常显示时的图片)
        Bitmap bitmap = Drawable2Bitmap(activity.getResources().getDrawable(DefaultDrawId));
        try {
            // 0.1.获取图片流
            InputStream in1 = new FileInputStream(filePath);
            // 0.2.获取屏幕大小
            int ww = activity.getWindowManager().getDefaultDisplay().getWidth();
            int wh = activity.getWindowManager().getDefaultDisplay().getHeight();

            // 1.提取图片特征
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;// 申请提取图片元数据
            opts.inPurgeable = true;// 申请GC清理请求

            // 2. 使用is图片流进行bitmap图元生成(注意: 这里使用了is, 下边要使用的时候要重新new一个, 否则is为空)
            Bitmap tempBm = BitmapFactory.decodeStream(in1, null, opts);
            // 3.计算比例
            int pw = opts.outWidth;
            int ph = opts.outHeight;
            // --> 3.1.默认为1
            int scale = 1;
            float rateW = pw * 1f / ww;
            float rateH = ph * 1f / wh;
            if (pw >= ph) {// 宽度>高度
                scale = (int) rateW;
            }
            if (pw < ph) {// 高度>宽度
                scale = (int) rateH;
            }
            // 4.关闭申请图片元数据以及流对象并且把生成的临时图片置空
            opts.inSampleSize = scale;
            opts.inJustDecodeBounds = false;
            in1.close();
            tempBm = null;
            // 5.重新创建一个IS流
            InputStream in2 = new FileInputStream(filePath);
            // 6.重新创建特征对象
            BitmapFactory.Options opts1 = new BitmapFactory.Options();
            // 7.把计算的比例赋值进去以及设置其他特征值
            opts1.inSampleSize = scale;
            opts1.inTempStorage = new byte[100 * 1024];// 设置缓存为100K
            opts1.inPreferredConfig = Bitmap.Config.RGB_565;// 设置颜色为565模式
            opts1.inPurgeable = true;// 设置允许垃圾回收
            // 8.重新打包这个图片
            bitmap = BitmapFactory.decodeStream(in2, null, opts1);
            // 9.关流
            in1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
