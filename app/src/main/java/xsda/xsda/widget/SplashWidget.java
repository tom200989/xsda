package xsda.xsda.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/6/22 0022.
 */

public class SplashWidget extends RelativeLayout {

    public static String DEFAULT_TEXT = "";
    private ImageView ivSplashLogo;// 启动屏总部logo
    private TextView tvSplashCopyright;// 公司copyright
    private NumberProgressBar pgSplashLoagding;// 进度条
    private TextView tvSplashLoadingText;// 进度文本

    public SplashWidget(Context context) {
        this(context, null, 0);
    }

    public SplashWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.widget_splash, this);
        DEFAULT_TEXT = getResources().getString(R.string.loading_text);
        ivSplashLogo = findViewById(R.id.iv_splash_logo);
        tvSplashCopyright = findViewById(R.id.tv_splash_copyright);
        pgSplashLoagding = findViewById(R.id.pg_splash_loagding);
        tvSplashLoadingText = findViewById(R.id.tv_splash_loading_text);
    }

    /**
     * 设置进度条
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        if (progress > 100) {
            progress = 100;
        } else if (progress <= 0) {
            progress = 10;
        }
        pgSplashLoagding.setProgress(progress);
    }

    /**
     * 设置正在加载中的提示语
     *
     * @param text 提示文本
     */
    public void setLoadingText(String text) {
        tvSplashLoadingText.setText(TextUtils.isEmpty(text) ? DEFAULT_TEXT : text);
    }
}
