package xsda.xsda.ue.frag;

import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.github.ybq.android.spinkit.SpinKitView;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.io.File;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.bean.UpdateBean;
import xsda.xsda.helper.DownloadHelper;
import xsda.xsda.helper.GetUpdateHelper;
import xsda.xsda.helper.InstallApkHelper;
import xsda.xsda.helper.SDHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class DownFrag extends BaseFrag {

    @BindView(R.id.rl_download_all)
    PercentRelativeLayout rlDownloadAll;// 总布局
    @BindView(R.id.iv_download_logo)
    ImageView ivDownloadLogo;// logo
    @BindView(R.id.sk_download_ui)
    SpinKitView skDownloadUi;// 动效
    @BindView(R.id.tv_download_des_fix)
    TextView tvDownloadDesFix;// 更新说明
    @BindView(R.id.scv_download)
    ScrollView scvDownload;// 滚动面板
    @BindView(R.id.pg_download_loagding)
    NumberProgressBar pgDownloadLoagding;// 进度条
    @BindView(R.id.tv_download_percent)
    TextView tvDownloadPercent;// 百分比
    @BindView(R.id.tv_download_retry)
    TextView tvDownloadRetry;// 重试
    @BindView(R.id.tv_download_back)
    TextView tvDownloadBack;// 下次再说
    @BindView(R.id.tv_download_install)
    TextView tvDownloadInstall;// 安装

    private View inflate;
    private UpdateBean updateBean;
    private int STATE = -1;
    private int STATE_ERROR = 0;
    private int STATE_INSTALL = 1;
    private int STATE_LOADING = 2;
    private DownloadHelper downloadHelper;
    private File apk;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_download;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        updateBean = (UpdateBean) yourBean;
        checkSDCard();// 主程序入口
        onClickEvent();
    }

    public void onClickEvent() {

        /* 返回按钮:下次再说 */
        tvDownloadBack.setOnClickListener(v -> onBackPresss());

        /* 重试按钮:出错后为重试按钮设置监听 */
        tvDownloadRetry.setOnClickListener(v -> {
            showLoadingUi();
            setProgresss(0);
            downloadHelper.download(updateBean.getFile());
        });

        /* 安装按钮 */
        tvDownloadInstall.setOnClickListener(v -> {
            // 获取下载的文件名(非路径), bbb.apk
            String apkName = apk.getName();
            Lgg.t(Cons.TAG).ii("apkName: " + apkName);
            // 开始安装
            InstallApkHelper installApkHelper = new InstallApkHelper();
            installApkHelper.setOnInstallErrorListener(e -> {
                showDownErrorUi();
                Tgg.show(activity, R.string.download_apk_error, 2500);
            });
            installApkHelper.install(activity, apkName);
        });
    }

    @Override
    public boolean onBackPresss() {
        Lgg.t(Cons.TAG).ii("state: " + STATE);
        if (STATE == STATE_ERROR) {
            checkVersion();
        } else if (STATE == STATE_INSTALL) {
            checkVersion();
        } else if (STATE == STATE_LOADING) {
            Tgg.show(activity, "正在下载\n请勿退出", 3000);
        } else {
            return false;
        }
        return true;
    }


    private void checkSDCard() {
        Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":checkSDCard()");
        // 4.1.判断SD卡是否挂载并留有足够空间
        SDHelper sdHelper = new SDHelper();
        // 4.2.空间不足--> 继续切换到下个界面
        sdHelper.setOnSdErrorListener(() -> {
            checkVersion();
            Tgg.show(activity, R.string.sdcard_no_mounted, 2000);
        });

        // 4.2.空间正常--> 切换到下载界面
        sdHelper.setOnSdNormalListener(() -> {
            // 设置描述
            String newVersionFix = updateBean.getNewVersionFix();
            Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":checkSDCard()" + "\nnewVersionFix: " + newVersionFix);
            tvDownloadDesFix.setText(newVersionFix);
            // 根据情况显示安装还是下载
            ToInstallOrDownload(updateBean);
        });
        // 执行
        sdHelper.getRemindMemory(activity, updateBean.getNewVersionSize());
    }

    /**
     * 向导页|登录页
     */
    private void checkVersion() {
        GetUpdateHelper getUpdateHelper = new GetUpdateHelper();
        getUpdateHelper.setOnExceptionListener(e -> toGuideOrLogin());
        getUpdateHelper.setOnGetUpdateListener(updateBean -> {
            int localVersion = Ogg.getLocalVersion(getActivity());
            int newVersion = Integer.valueOf(updateBean.getNewVersionCode());
            if (localVersion > newVersion) {
                toGuideOrLogin();
            } else if (newVersion - localVersion <= 2) {
                toGuideOrLogin();
            } else {
                finish();
                kill();
            }
        });
        getUpdateHelper.getNewVersion();

    }

    private void toGuideOrLogin() {
        // 向导页|登录页
        if (Sgg.getInstance(activity).getBoolean(Cons.SP_GUIDE, false)) {
            // 进入登录页
            toFrag(getClass(), LoginFrag.class, null, false);
            Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":checkVersion()--> to login");
        } else {
            // 进入向导页
            toFrag(getClass(), GuideFrag.class, null, false);
            Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":checkVersion()--> to guide");
        }
    }

    /**
     * 根据情况显示安装还是下载
     *
     * @param updateBean 从网络获取的信息
     */
    private void ToInstallOrDownload(UpdateBean updateBean) {
        String localApkPath = Ogg.getLocalInstallApkPath(updateBean.getFile().getName());
        Lgg.t(Cons.TAG).ii("ToInstallOrDownload(): localApkPath: " + localApkPath);
        // 1.如果没有找到指定名称的apk文件--> 下载
        if (TextUtils.isEmpty(localApkPath)) {
            toDownload();
        } else {
            // 2.获取本地包信息
            PackageInfo packageInfo = Ogg.checkApkVersionInfo(activity, localApkPath);
            String localPackageName = packageInfo.packageName;
            String localSharedUserId = packageInfo.sharedUserId;
            int localVersionCode = packageInfo.versionCode;
            Lgg.t(Cons.TAG).ii("ToInstallOrDownload():\nlocalPackageName: " + localPackageName + "\nlocalSharedUserId: " + localSharedUserId + "\nlocalVersionCode: " + localVersionCode);

            // 3.包名是否与当前运行的APP的包名一致
            if (localPackageName.contains(activity.getPackageName()) & localSharedUserId.contains(getString(R.string.app_sui))) {
                Lgg.t(Cons.TAG).ii("packageName and shareUserId is same");
                // 4.安装包版本号是否和网络存档的版本号一致
                if (localVersionCode == Integer.valueOf(updateBean.getNewVersionCode())) {
                    Lgg.t(Cons.TAG).ii("version is same to leadCloud");
                    // 5.显示安装UI
                    showInstallUi();
                } else {
                    Lgg.t(Cons.TAG).ii("version is not same to leadCloud");
                    // 5.显示下载UI--> 执行下载逻辑
                    toDownload();
                }
            } else {
                Lgg.t(Cons.TAG).ii("packageName and shareUserId is not same");
                toDownload();
            }

        }
    }

    /**
     * 执行下载逻辑
     */
    private void toDownload() {

        showLoadingUi();
        setProgresss(0);
        downloadHelper = new DownloadHelper();

        /* 准备下载 */
        downloadHelper.setOnPreDownloadListener(() -> {
            // 显示下载中
            showLoadingUi();
            // 显示进度
            setProgresss(0);
        });

        /* 下载过程 */
        downloadHelper.setOnProgressListener(progress -> {
            // 显示下载中
            showLoadingUi();
            // 显示进度
            setProgresss(progress);
        });

        /* 下载出错 */
        downloadHelper.setOnDownErrorListener(e -> {
            // 显示出错
            showDownErrorUi();
            Tgg.show(activity, getString(R.string.download_neterror), 2500);
        });

        /* 下载完毕 */
        downloadHelper.setOnDownFinishListener(apk -> {
            this.apk = apk;
            // 显示安装
            showInstallUi();
        });
        // 执行逻辑
        downloadHelper.download(updateBean.getFile());
    }

    /**
     * 显示下载出错的UI
     */
    public void showDownErrorUi() {
        STATE = STATE_ERROR;
        setProgresss(0);
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
        STATE = STATE_LOADING;
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
        STATE = STATE_INSTALL;
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
    public void setProgresss(int progress) {
        progress = progress < 5 ? 5 : progress;
        pgDownloadLoagding.setProgress(progress);
        tvDownloadPercent.setText(String.format(getResources().getString(R.string.downloading), progress));
    }
}
