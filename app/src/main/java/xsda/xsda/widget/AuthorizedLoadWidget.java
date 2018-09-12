package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import xsda.xsda.R;

/*
 * Created by qianli.ma on 2018/9/12 0012.
 */
public class AuthorizedLoadWidget extends RelativeLayout {

    private ImageView ivAuthorizedLoadingBg;
    private ImageView ivAuthorizedLoadingLogo;

    public AuthorizedLoadWidget(Context context) {
        this(context, null, 0);
    }

    public AuthorizedLoadWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AuthorizedLoadWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.widget_authorized, this);
        ivAuthorizedLoadingBg = findViewById(R.id.iv_authorized_loading_bg);
        ivAuthorizedLoadingBg.setOnClickListener(null);
        ivAuthorizedLoadingLogo = findViewById(R.id.iv_authorized_loading_logo);
        setAnim(ivAuthorizedLoadingLogo);
    }

    /**
     * 设置转动动画
     *
     * @param view 目标view
     */
    private void setAnim(View view) {
        RotateAnimation ra = new RotateAnimation(0, 360, 0.5f, 0.5f);
        ra.setInterpolator(new LinearInterpolator());
        ra.setDuration(2000);
        ra.setRepeatMode(Animation.INFINITE);
        ra.setRepeatCount(Animation.INFINITE);
        ra.setFillAfter(true);
        view.setAnimation(ra);
        ra.startNow();
        view.startAnimation(ra);
    }
}
