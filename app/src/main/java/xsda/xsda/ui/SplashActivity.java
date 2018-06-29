package xsda.xsda.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xsda.xsda.R;
import xsda.xsda.bean.UpdateBean;
import xsda.xsda.helper.DownloadHelper;
import xsda.xsda.helper.GetUpdateHelper;
import xsda.xsda.helper.InstallApkHelper;
import xsda.xsda.helper.PingHelper;
import xsda.xsda.helper.SDHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.RCode;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.DownloadWidget;
import xsda.xsda.widget.NetErrorWidget;
import xsda.xsda.widget.PrivacyWidget;
import xsda.xsda.widget.SplashWidget;
import xsda.xsda.widget.TipWidget;
import xsda.xsda.widget.WaitWidget;

public class SplashActivity extends RootActivity {


    @Bind(R.id.widget_splash)
    SplashWidget widgetSplash;// 起始界面
    @Bind(R.id.widget_privacy)
    PrivacyWidget widgetPrivacy;// 隐私条款
    @Bind(R.id.widget_neterror)
    NetErrorWidget widgetNeterror;// 网络异常
    @Bind(R.id.widget_wait)
    WaitWidget widgetWait;// 等待界面
    @Bind(R.id.widget_update)
    TipWidget widgetUpdate;// 新版本界面
    @Bind(R.id.widget_download)
    DownloadWidget widgetDownload;// 下载界面

    private Handler handler;
    private static final int REQUEST_NEED = RCode.BASE_REQUEST_CODE;
    private static String[] PERMISSIONS_NEED = {// 填写需要申请的权限
            Manifest.permission.READ_EXTERNAL_STORAGE,// 读取外部存储
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入外部存储
            Manifest.permission.READ_PHONE_STATE// 电话状态
            // .... 需要什么权限, 需要先声明 ....
            // 注意: 非危险权限不需要申请, 一定不能加进来, 否则影响业务逻辑
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        // 初始化某些操作
        init();
        // 设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, colorStatuBar, false);
        // 申请权限
        showPermission();
    }

    /**
     * 初始化某些操作
     */
    private void init() {
        handler = new Handler();
        Ogg.createInstallRootDir();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 1.申请权限
     */
    private void showPermission() {
        // 6.0 以上申请权限
        if (Build.VERSION.SDK_INT < 23) {
            // 显示隐私条款
            showPrivacy();
        } else {
            // 1.开启对应的权限(同一个组的权限只需要申请一个, 同组的权限即可开通使用)
            int exstorePer = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int phonePer = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

            // 2.权限本身不允许--> 请求
            if (exstorePer == PackageManager.PERMISSION_DENIED // 读写权限
                        | phonePer == PackageManager.PERMISSION_DENIED // 电话记录权限
                    ) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_NEED, REQUEST_NEED);
            } else {// 2.权限本身允许--> 执行业务逻辑
                // 显示隐私条款
                showPrivacy();
            }
        }
    }

    /**
     * 2.显示隐私条款
     */
    private void showPrivacy() {
        // 是否要进入隐私条款界面
        if (!Sgg.getInstance(this).getBoolean(Cons.SP_PRIVACY_READ, false)) {
            // 延迟2秒出现隐私条款
            handler.postDelayed(() -> widgetPrivacy.setVisibility(View.VISIBLE), 0);
            // 设置隐私条款的点击事件
            widgetPrivacy.setOnClickAgreeListener(() -> {
                // 提交隐私条款「已阅读」
                Sgg.getInstance(this).putBoolean(Cons.SP_PRIVACY_READ, true);
                widgetPrivacy.setVisibility(View.GONE);
                // 检测新版本
                checkConnect();
            });
        } else {
            // 检测新版本
            checkConnect();
        }

    }

    /**
     * 3.检查更新
     */
    private void checkConnect() {
        // 显示等待界面
        widgetSplash.setVisibility(View.VISIBLE);
        handler.postDelayed(this::ping, 3000);
    }

    /**
     * 4.检测是否连接上LeanClound服务器
     */
    public void ping() {
        // ping指定地址
        PingHelper pingHelper = new PingHelper();

        pingHelper.setOnPingSuccessListener(msg -> {
            widgetSplash.setLoadingText(loading_success);
            checkUpdate();// 检测是否有新版本
        });

        pingHelper.setOnProgressListener(progress -> {
            // 显示进度
            if (progress <= 50) {
                widgetSplash.setLoadingText(check_net);
            } else if (progress > 50 & progress <= 75) {
                widgetSplash.setLoadingText(loading_text);
            } else {
                widgetSplash.setLoadingText(loading_success);
            }
            widgetSplash.setProgress(progress);
        });

        pingHelper.setOnPingFailedListener(msg -> {
            widgetNeterror.setVisibility(View.VISIBLE);
            widgetNeterror.setOnNetErrorBackListener(this::finish);
            widgetNeterror.setOnNetErrorRetryListener(() -> {
                widgetNeterror.setVisibility(View.GONE);
                checkConnect();
            });
        });
        pingHelper.ping(this, getString(R.string.ping_address_backup));
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        // 1.获取当前运行APP的版本号
        int runVersion = Ogg.getLocalVersion(this);
        // 2.请求LeanClound最新版本
        GetUpdateHelper getUpdateHelper = new GetUpdateHelper();
        getUpdateHelper.setOnGetUpdateListener(updateBean -> {
            // 2.1.获取到最新的版本号
            int newVersion = Integer.valueOf(updateBean.getNewVersionCode());
            // 3.有更新版本
            if (newVersion > runVersion) {
                widgetUpdate.setVisibility(View.VISIBLE);
                widgetUpdate.setUpdateDesFix(updateBean.getNewVersionFix());
                // 4.点击了「确定更新」
                widgetUpdate.setOnClickOkListener(() -> {
                    
                    // 4.1.判断SD卡是否挂载并留有足够空间
                    SDHelper sdHelper = new SDHelper();
                    // 4.2.空间不足--> 继续切换到下个界面
                    sdHelper.setOnSdErrorListener(() -> {
                        widgetUpdate.setVisibility(View.GONE);
                        toGuideOrMain();
                    });
                    
                    // 4.2.空间正常--> 切换到下载界面
                    sdHelper.setOnSdNormalListener(() -> {
                        widgetDownload.setVisibility(View.VISIBLE);
                        // 设置描述
                        widgetDownload.setUpdateFix(updateBean.getNewVersionFix());
                        // TODO: 2018/6/29 0029  
                        // 开始下载
                        downNewVersion(updateBean);
                    });
                    
                    sdHelper.getRemindMemory(this, updateBean.getNewVersionSize());
                });

                // 4.点击了「下次再说」
                widgetUpdate.setOnClickCancelListener(this::toGuideOrMain);
                
            } else {
                // 3.没有新版本--> 切换到下个界面
                toGuideOrMain();
            }
        });
        getUpdateHelper.setOnExceptionListener(e -> widgetNeterror.setVisibility(View.VISIBLE));
        getUpdateHelper.getNewVersion();
    }

    /**
     * 执行下载逻辑
     *
     * @param updateBean 新版本对象
     */
    private void downNewVersion(UpdateBean updateBean) {

        DownloadHelper downloadHelper = new DownloadHelper();

        /* 准备下载 */
        downloadHelper.setOnPreDownloadListener(() -> {
            // 显示下载中
            widgetDownload.showLoadingUi();
            // 显示进度
            widgetDownload.setProgress(0);
        });
        
        /* 下载过程 */
        downloadHelper.setOnProgressListener(progress -> {
            // 显示下载中
            widgetDownload.showLoadingUi();
            // 显示进度
            widgetDownload.setProgress(progress);
        });
        
        /* 下载出错 */
        downloadHelper.setOnDownErrorListener(e -> {
            // 显示出错
            widgetDownload.showDownErrorUi();
            // 出错后为重试按钮设置监听
            widgetDownload.setOnRetryListener(() -> {
                widgetDownload.setProgress(0);
                downNewVersion(updateBean);
            });
            // 出错后为返回按钮设置监听
            widgetDownload.setOnBackListener(this::toGuideOrMain);
        });
        
        /* 下载完毕 */
        downloadHelper.setOnDownFinishListener(apk -> {
            // 显示安装
            widgetDownload.showInstallUi();
            widgetDownload.setOnInstallListener(() -> {
                // 获取下载的文件名(非路径), bbb.apk
                String apkName = apk.getName();
                Lgg.t(Cons.TAG).ii("apkName: " + apkName);
                // 开始安装
                InstallApkHelper.install(this, apkName);
            });
        });

        downloadHelper.download(updateBean.getFile());
    }

    /**
     * 切换到向导页或者主页
     */
    private void toGuideOrMain() {
        if (Sgg.getInstance(this).getBoolean(Cons.SP_GUIDE, false)) {
            // TODO: 2018/6/26 0026 进入主页
            Tgg.show(this, "进入主页", 0);
        } else {
            // TODO: 2018/6/26 0026 进入向导页
            Tgg.show(this, "进入向导页", 0);
        }
    }

    @Override
    public void onBackPressed() {
        // 对界面做分类判断
        if (widgetSplash.getVisibility() == View.VISIBLE) {// 启动页
            finish();
            Process.killProcess(Process.myPid());
        } else if (widgetPrivacy.getVisibility() == View.VISIBLE) {// 隐私页
            finish();
            Process.killProcess(Process.myPid());
        } else if (widgetNeterror.getVisibility() == View.VISIBLE) {// 出错页
            finish();
            Process.killProcess(Process.myPid());
        } else if (widgetUpdate.getVisibility() == View.VISIBLE) {// 更新提示页
            // 界面隐藏 
            widgetUpdate.setVisibility(View.GONE);
            // 继续向下执行
            toGuideOrMain();
        } else if (widgetDownload.getVisibility() == View.VISIBLE) {// 下载页
            // TODO: 2018/6/28 0028  弹出询问界面提示用户要重新下载,是否确定回退
            Tgg.show(this, "将会取消下载, 之前下载的内容将清除", 3000);
        } else {
            finish();
            Process.killProcess(Process.myPid());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RCode.BASE_REQUEST_CODE:// 基本的权限申请
                applyBasePermission(permissions, grantResults);
                break;
        }
    }

    /**
     * 申请基本的权限
     *
     * @param grantResults 权限组
     */
    private void applyBasePermission(String[] permissions, int[] grantResults) {

        for (String permission : permissions) {
            Lgg.t("ma_permission").ii(permission);
        }

        // 1.把所有回调的权限状态添加到自定义集合
        List<Integer> ints = new ArrayList<>();
        for (int grantResult : grantResults) {
            Lgg.t("ma_permission").ii(grantResult + "");
            ints.add(grantResult);
        }

        // 2.判断: 如果有任意一个权限未通过, 则杀死进程(或者自定义处理)
        if (ints.contains(PackageManager.PERMISSION_DENIED)) {
            finish();
        } else {
            // 显示隐私条款
            showPrivacy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + " onDestroy()");
    }
}
