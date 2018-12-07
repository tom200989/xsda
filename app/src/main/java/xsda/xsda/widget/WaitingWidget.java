package xsda.xsda.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiber.tools.layout.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import xsda.xsda.R;
import xsda.xsda.helper.TimerHelper;
import xsda.xsda.ue.app.XsdaApplication;

/**
 * Created by qianli.ma on 2018/7/17 0017.
 */

public class WaitingWidget extends RelativeLayout {

    private PercentRelativeLayout rlWaitingAll;
    private ImageView ivWaitingPin1;
    private ImageView ivWaitingPin2;
    private TextView tvWaitingDes;
    private Context context;
    private TimerHelper timerHelper;
    int count = 0;
    private String text;
    private List<String> texts;
    private RotateAnimation ra1;
    private RotateAnimation ra2;

    public WaitingWidget(Context context) {
        this(context, null, 0);
    }

    public WaitingWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaitingWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        text = context.getString(R.string.waitting_text);
        this.context = context;
        View.inflate(context, R.layout.widget_waiting, this);
        rlWaitingAll = findViewById(R.id.rl_waiting_all);
        rlWaitingAll.setOnClickListener(null);
        ivWaitingPin1 = findViewById(R.id.iv_waiting_pin1);
        ivWaitingPin2 = findViewById(R.id.iv_waiting_pin2);
        tvWaitingDes = findViewById(R.id.tv_waiting_des);
        initRes();
        initPinAnim();
    }

    private void initPinAnim() {
        ra1 = new RotateAnimation(0, 360, 1, 0.5f, 1, 0.5f);
        ra1.setDuration(1000);
        ra1.setRepeatCount(Animation.INFINITE);
        ra1.setRepeatMode(Animation.INFINITE);
        ra1.setInterpolator(new LinearInterpolator());
        ivWaitingPin1.setAnimation(ra1);
        ivWaitingPin1.startAnimation(ra1);
        ra1.startNow();

        ra2 = new RotateAnimation(0, 360, 1, 0.5f, 1, 0.5f);
        ra2.setDuration(2500);
        ra2.setRepeatCount(Animation.INFINITE);
        ra2.setRepeatMode(Animation.INFINITE);
        ra2.setInterpolator(new LinearInterpolator());
        ivWaitingPin2.setAnimation(ra2);
        ivWaitingPin2.startAnimation(ra2);
        ra2.startNow();
    }

    /**
     * 默认开启动画
     */
    private void initRes() {
        // 准备资源
        tvWaitingDes.setVisibility(INVISIBLE);
        tvWaitingDes.setText(text);
        if (texts != null) {
            texts.clear();
            texts = null;
        }
        texts = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            text += ".";
            texts.add(text);
        }
    }

    /**
     * 设置有动画(默认文本)
     */
    public void setVisibleByAnim() {
        count = 0;
        setVisibility(VISIBLE);
        tvWaitingDes.setVisibility(VISIBLE);
        // 启动定时器
        if (timerHelper != null) {
            timerHelper.stop();
        }
        if (timerHelper == null) {
            timerHelper = new TimerHelper((Activity) context) {
                @Override
                public void doSomething() {
                    if (count > 5) {
                        count = 0;
                    }
                    ((Activity) context).runOnUiThread(() -> tvWaitingDes.setText(texts.get(count)));

                    count++;
                }
            };
        }
        timerHelper.start(400);
    }

    /**
     * 设置没有动画(默认文本)
     */
    public void setVisibleByNoAnim() {
        setVisibility(VISIBLE);
        tvWaitingDes.setVisibility(VISIBLE);
        ((Activity) context).runOnUiThread(() -> tvWaitingDes.setText(XsdaApplication.getApp().getString(R.string.waitting_text)));

    }

    /**
     * 自定义描述文本
     *
     * @param text 文本
     */
    public void setVisibleText(String text) {
        setVisibility(VISIBLE);
        tvWaitingDes.setVisibility(VISIBLE);
        ((Activity) context).runOnUiThread(() -> tvWaitingDes.setText(text));

    }

    /**
     * 设置消失
     */
    public void setGone() {
        count = 0;
        if (timerHelper != null) {
            timerHelper.stop();
            timerHelper = null;
        }
        setVisibility(GONE);
    }
}
