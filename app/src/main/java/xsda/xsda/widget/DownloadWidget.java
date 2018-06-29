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

    private TextView tvDownloadDesFix;// 更新说明

    private TextView tvDownloadPercent;// 百分比显示
    private TextView tvDownloadRetry;// 重试按钮
    private TextView tvDownloadBack;// 下次再说
    private TextView tvDownloadInstall;// 安装按钮

    public DownloadWidget(Context context) {
        this(context, null, 0);
    }

    public DownloadWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.widget_download, this);
        initView();
        clickEvent();
    }

    private void initView() {
        rlDownloadAll = (RelativeLayout) findViewById(R.id.rl_download_all);
        ivDownloadLogo = (ImageView) findViewById(R.id.iv_download_logo);
        skDownloadUi = (SpinKitView) findViewById(R.id.sk_download_ui);
        tvDownloadDesFix = findViewById(R.id.tv_download_des_fix);
        scvDownload = (ScrollView) findViewById(R.id.scv_download);
        pgDownloadLoagding = (NumberProgressBar) findViewById(R.id.pg_download_loagding);
        tvDownloadPercent = (TextView) findViewById(R.id.tv_download_percent);
        tvDownloadRetry = findViewById(R.id.tv_download_retry);
        tvDownloadBack = findViewById(R.id.tv_download_back);
        tvDownloadInstall = findViewById(R.id.tv_download_install);
    }

    private void clickEvent() {

        // 屏蔽用户的其他操作
        rlDownloadAll.setOnClickListener(v -> {
        });

        // 点击重试
        tvDownloadRetry.setOnClickListener(v -> {
            showLoadingUi();
            retryDownNext();
        });

        // 点击回退
        tvDownloadBack.setOnClickListener(v -> {
            setVisibility(GONE);
            backNext();
        });

        // 点击安装
        tvDownloadInstall.setOnClickListener(v -> {
            installNext();
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
        tvDownloadInstall.setVisibility(GONE);// 安装
        tvDownloadPercent.setVisibility(GONE);// 百分比
        tvDownloadRetry.setVisibility(VISIBLE);// 重试
        tvDownloadBack.setVisibility(VISIBLE);// 下次再说
        pgDownloadLoagding.setVisibility(VISIBLE);// 进度条
    }

    /**
     * 显示正在下载的UI
     */
    public void showLoadingUi() {
        tvDownloadInstall.setVisibility(GONE);
        tvDownloadPercent.setVisibility(VISIBLE);
        tvDownloadRetry.setVisibility(GONE);
        tvDownloadBack.setVisibility(GONE);
        pgDownloadLoagding.setVisibility(VISIBLE);
    }

    /**
     * 显示「安装」UI
     */
    public void showInstallUi() {
        tvDownloadInstall.setVisibility(VISIBLE);
        tvDownloadPercent.setVisibility(GONE);
        tvDownloadRetry.setVisibility(GONE);
        tvDownloadBack.setVisibility(VISIBLE);
        pgDownloadLoagding.setVisibility(GONE);
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

    private OnInstallListener onInstallListener;

    // 接口OnInstallListener
    public interface OnInstallListener {
        void install();
    }

    // 对外方式setOnInstallListener
    public void setOnInstallListener(OnInstallListener onInstallListener) {
        this.onInstallListener = onInstallListener;
    }

    // 封装方法installNext
    private void installNext() {
        if (onInstallListener != null) {
            onInstallListener.install();
        }
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
