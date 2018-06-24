package xsda.xsda.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;

import xsda.xsda.R;
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
}
