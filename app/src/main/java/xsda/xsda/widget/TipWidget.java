package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/6/26 0026.
 */

public class TipWidget extends RelativeLayout {

    private ImageView ivUpdateBg;
    private RelativeLayout rlUpdateContent;
    private TextView tvUpdateTitle;
    private TextView tvUpdateDes;
    private TextView tvUpdateCancel;
    private TextView tvUpdateOk;

    public TipWidget(Context context) {
        this(context, null, 0);
    }

    public TipWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.widget_tip, this);
        ivUpdateBg = (ImageView) findViewById(R.id.iv_update_bg);
        rlUpdateContent = (RelativeLayout) findViewById(R.id.rl_update_content);
        tvUpdateTitle = (TextView) findViewById(R.id.tv_update_title);
        tvUpdateDes = (TextView) findViewById(R.id.tv_update_des);
        tvUpdateCancel = (TextView) findViewById(R.id.tv_update_cancel);
        tvUpdateOk = (TextView) findViewById(R.id.tv_update_ok);

        ivUpdateBg.setOnClickListener(v -> {
        });
        rlUpdateContent.setOnClickListener(v -> {
        });
        tvUpdateCancel.setOnClickListener(v -> {
            setVisibility(GONE);
            clickCancelNext();
        });
        tvUpdateOk.setOnClickListener(v -> {
            setVisibility(GONE);
            clickOkNext();
        });
    }

    private OnClickCancelListener onClickCancelListener;

    // 接口OnClickCancelListener
    public interface OnClickCancelListener {
        void clickCancel();
    }

    // 对外方式setOnClickCancelListener
    public void setOnClickCancelListener(OnClickCancelListener onClickCancelListener) {
        this.onClickCancelListener = onClickCancelListener;
    }

    // 封装方法clickCancelNext
    private void clickCancelNext() {
        if (onClickCancelListener != null) {
            onClickCancelListener.clickCancel();
        }
    }

    private OnClickOkListener onClickOkListener;

    // 接口OnClickOkListener
    public interface OnClickOkListener {
        void clickOk();
    }

    // 对外方式setOnClickOkListener
    public void setOnClickOkListener(OnClickOkListener onClickOkListener) {
        this.onClickOkListener = onClickOkListener;
    }

    // 封装方法clickOkNext
    private void clickOkNext() {
        if (onClickOkListener != null) {
            onClickOkListener.clickOk();
        }
    }

    /**
     * 设置更新的内容
     *
     * @param des 更新的内容
     */
    public void setUpdateDes(String des) {
        tvUpdateDes.setText(des);
    }
}
