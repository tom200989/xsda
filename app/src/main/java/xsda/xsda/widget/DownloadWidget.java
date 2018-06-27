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
        scvDownload = (ScrollView) findViewById(R.id.scv_download);
        pgDownloadLoagding = (NumberProgressBar) findViewById(R.id.pg_download_loagding);
        tvDownloadPercent = (TextView) findViewById(R.id.tv_download_percent);
        rlDownloadAll.setOnClickListener(v -> {
            // 屏蔽用户的其他操作
        });
    }

    /**
     * 设置进度
     *
     * @param progress 进度(max:100)
     */
    public void setProgress(int progress) {
        pgDownloadLoagding.setProgress(progress);
        tvDownloadPercent.setText(String.format(getResources().getString(R.string.downloading), progress));
    }

}
