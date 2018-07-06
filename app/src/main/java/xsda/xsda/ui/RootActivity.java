package xsda.xsda.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;

import xsda.xsda.R;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Tgg;

/**
 * Created by qianli.ma on 2018/6/20 0020.
 */

@SuppressLint("Registered")
public class RootActivity extends Activity {

    public int colorStatuBar;// 状态栏颜色
    public Drawable privacy_checkbox_checked;// 隐私条款勾选
    public Drawable privacy_checkbox_uncheck;// 隐私条款不勾选
    public String check_net;// 字符: 正在检测网络
    public String loading_text;// 字符: 数据正在加载中
    public String loading_success;// 字符: 正在跳转

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏颜色
        colorStatuBar = getResources().getColor(R.color.colorRoot);
        // 设置无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 初始化资源
        privacy_checkbox_checked = getResources().getDrawable(R.drawable.privacy_checkbox_checked);
        privacy_checkbox_uncheck = getResources().getDrawable(R.drawable.privacy_checkbox_uncheck);
        check_net = getString(R.string.check_net);
        loading_text = getString(R.string.loading_text);
        loading_success = getString(R.string.loading_success);
    }

    /**
     * 吐司提示
     *
     * @param tip      提示
     * @param duration 时长
     */
    public void toast(String tip, int duration) {
        Tgg.show(this, tip, duration);
    }

    /**
     * 跳转(默认方式)
     *
     * @param context   当前环境
     * @param clazz     目标
     * @param isDefault 是否默认方式
     */
    public void to(Context context, Class<?> clazz, boolean isDefault) {
        to(context, clazz, true, true, false, 0);
    }

    /**
     * 跳转
     *
     * @param context         上下文
     * @param clazz           目标
     * @param isSingleTop     独立任务栈
     * @param isFinish        结束当前
     * @param overridepedding 转场
     * @param delay           延迟
     */
    public void to(final Context context,// 上下文
                   final Class<?> clazz,// 目标
                   final boolean isSingleTop,// 独立任务栈
                   final boolean isFinish,// 结束当前
                   boolean overridepedding, // 转场
                   final int delay) {// 延迟
        final Activity activity = (Activity) context;
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        Intent intent = new Intent(context, clazz);
                        // 独立任务栈
                        if (isSingleTop) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        }
                        // 启动
                        context.startActivity(intent);
                        // 转场(务必在启动后才可调用)
                        if (!overridepedding) {
                            activity.overridePendingTransition(0, 0);
                        }
                        // 结束当前(务必在启动后才可调用)
                        if (isFinish) {
                            activity.finish();
                        }
                        Lgg.t(Cons.TAG).ii("RootActivity:to(): " + clazz.getSimpleName());
                    });
                }
            } catch (Exception e) {
                Lgg.t(Cons.TAG).ee("RootActivity:to():error: " + e.getMessage());
                e.printStackTrace();
            }

        }).start();
    }
}
