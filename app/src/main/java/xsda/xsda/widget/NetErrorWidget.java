package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/6/22 0022.
 */

public class NetErrorWidget extends RelativeLayout {

    private RelativeLayout rlNeterrorAll;// 总布局
    private TextView tvNeterrorLogo;// 图标
    private TextView tvNeterrorDes;// 描述
    private TextView tvNeterrorRetry;// 重试
    private TextView tvNeterrorBack;// 退出

    public NetErrorWidget(Context context) {
        this(context, null, 0);
    }

    public NetErrorWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetErrorWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.widget_neterror, this);
        assignViews();
    }

    private void assignViews() {
        rlNeterrorAll = (RelativeLayout) findViewById(R.id.rl_neterror_all);
        tvNeterrorDes = (TextView) findViewById(R.id.tv_neterror_des);
        tvNeterrorLogo = (TextView) findViewById(R.id.tv_neterror_logo);
        tvNeterrorRetry = (TextView) findViewById(R.id.tv_neterror_retry);
        tvNeterrorBack = (TextView) findViewById(R.id.tv_neterror_back);
        rlNeterrorAll.setOnClickListener(v -> {});
        tvNeterrorRetry.setOnClickListener(v -> {
            setVisibility(GONE);
            retryNext();
        });
        tvNeterrorBack.setOnClickListener(v -> backNext());
    }

    private OnNetErrorBackListener onNetErrorBackListener;

    // 接口OnNetErrorBackListener
    public interface OnNetErrorBackListener {
        void back();
    }

    // 对外方式setOnNetErrorBackListener
    public void setOnNetErrorBackListener(OnNetErrorBackListener onNetErrorBackListener) {
        this.onNetErrorBackListener = onNetErrorBackListener;
    }

    // 封装方法backNext
    private void backNext() {
        if (onNetErrorBackListener != null) {
            onNetErrorBackListener.back();
        }
    }

    private OnNetErrorRetryListener onNetErrorRetryListener;

    // 接口OnNetErrorRetryListener
    public interface OnNetErrorRetryListener {
        void retry();
    }

    // 对外方式setOnNetErrorRetryListener
    public void setOnNetErrorRetryListener(OnNetErrorRetryListener onNetErrorRetryListener) {
        this.onNetErrorRetryListener = onNetErrorRetryListener;
    }

    // 封装方法retryNext
    private void retryNext() {
        if (onNetErrorRetryListener != null) {
            onNetErrorRetryListener.retry();
        }
    }
}
