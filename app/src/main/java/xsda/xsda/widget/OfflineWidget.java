package xsda.xsda.widget;
/*
 * Created by qianli.ma on 2018/8/10 0010.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xsda.xsda.R;

public class OfflineWidget extends RelativeLayout {

    private ImageView ivOfflineBg;
    private TextView tvOfflineWidgetTitle;
    private TextView tvOfflineWidgetDes;
    private TextView tvOfflineWidgetOk;

    public OfflineWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.widget_offline, this);
        ivOfflineBg = findViewById(R.id.iv_offline_bg);
        ivOfflineBg.setOnClickListener(null);
        tvOfflineWidgetTitle = findViewById(R.id.tv_offline_widget_title);
        tvOfflineWidgetDes = findViewById(R.id.tv_offline_widget_des);
        tvOfflineWidgetOk = findViewById(R.id.tv_offline_widget_ok);
        tvOfflineWidgetOk.setOnClickListener(v -> {
            setVisibility(GONE);
            clickOfflineOkNext();
        });
    }

    private OnClickOfflineOkListener onClickOfflineOkListener;

    // 接口OnClickOfflineOkListener
    public interface OnClickOfflineOkListener {
        void clickOfflineOk();
    }

    // 对外方式setOnClickOfflineOkListener
    public void setOnClickOfflineOkListener(OnClickOfflineOkListener onClickOfflineOkListener) {
        this.onClickOfflineOkListener = onClickOfflineOkListener;
    }

    // 封装方法clickOfflineOkNext
    private void clickOfflineOkNext() {
        if (onClickOfflineOkListener != null) {
            onClickOfflineOkListener.clickOfflineOk();
        }
    }

}
