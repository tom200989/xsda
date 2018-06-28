package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.github.ybq.android.spinkit.SpinKitView;

import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/6/27 0027.
 */

public class DownloadWidget extends RelativeLayout {

    private RelativeLayout rlDownloadAll;// 总布局
    private ImageView ivDownloadLogo;// logo
    private SpinKitView skDownloadUi;// 动效
    private ScrollView scvDownload;// 描述滚动面板
    private NumberProgressBar pgDownloadLoagding;// 进度条
    private TextView tvDownloadPercent;// 百分比显示
    private TextView tvDownloadRetry;// 重试按钮
    private TextView tvDownloadBack;// 下次再说
    private TextView tvDownloadDesFix;

    public DownloadWidget(Context context) {
        this(context, null, 0);
    }

    public DownloadWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.widget_download, this);
        assignViews();
    }

    private void assignViews() {
        rlDownloadAll = (RelativeLayout) findViewById(R.id.rl_download_all);
        ivDownloadLogo = (ImageView) findViewById(R.id.iv_download_logo);
        skDownloadUi = (SpinKitView) findViewById(R.id.sk_download_ui);
        tvDownloadDesFix = findViewById(R.id.tv_download_des_fix);
        scvDownload = (ScrollView) findViewById(R.id.scv_download);
        pgDownloadLoagding = (NumberProgressBar) findViewById(R.id.pg_download_loagding);
        tvDownloadPercent = (TextView) findViewById(R.id.tv_download_percent);
        tvDownloadRetry = findViewById(R.id.tv_download_retry);
        tvDownloadBack = findViewById(R.id.tv_download_back);
        rlDownloadAll.setOnClickListener(v -> {
            // 屏蔽用户的其他操作
        });
        tvDownloadRetry.setOnClickListener(v -> {// 点击重试
            tvDownloadRetry.setVisibility(GONE);
            tvDownloadBack.setVisibility(GONE);
            tvDownloadPercent.setVisibility(VISIBLE);
            retryDownNext();
        });
        tvDownloadBack.setOnClickListener(v -> {// 点击回退
            setVisibility(GONE);
            backNext();
        });
    }

    /**
     * 设置更新描述
     *
     * @param des 描述
     */
    public void setUpdateFix(String des) {
        tvDownloadDesFix.setText(des);
    }

    /**
     * 显示下载出错的UI
     */
    public void showDownErrorUi() {
        tvDownloadRetry.setVisibility(VISIBLE);
        tvDownloadBack.setVisibility(VISIBLE);
        tvDownloadPercent.setVisibility(GONE);
    }

    /**
     * 设置进度
     *
     * @param progress 进度(max:100)
     */
    public void setProgress(int progress) {
        progress = progress < 5 ? 5 : progress;
        pgDownloadLoagding.setProgress(progress);
        tvDownloadPercent.setText(String.format(getResources().getString(R.string.downloading), progress));
    }

    private OnBackListener onBackListener;

    // 接口OnBackListener
    public interface OnBackListener {
        void back();
    }

    // 对外方式setOnBackListener
    public void setOnBackListener(OnBackListener onBackListener) {
        this.onBackListener = onBackListener;
    }

    // 封装方法backNext
    private void backNext() {
        if (onBackListener != null) {
            onBackListener.back();
        }
    }

    private OnRetryListener onRetryListener;

    // 接口OnRetryListener
    public interface OnRetryListener {
        void retryDown();
    }

    // 对外方式setOnRetryListener
    public void setOnRetryListener(OnRetryListener onRetryListener) {
        this.onRetryListener = onRetryListener;
    }

    // 封装方法retryDownNext
    private void retryDownNext() {
        if (onRetryListener != null) {
            onRetryListener.retryDown();
        }
    }

}
