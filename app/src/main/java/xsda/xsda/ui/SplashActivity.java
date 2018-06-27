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
import xsda.xsda.helper.GetUpdateHelper;
import xsda.xsda.helper.PingHelper;
import xsda.xsda.helper.SDHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Ogg;
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
    private static final int REQUEST_NEED = 0x100;
    private static String[] PERMISSIONS_NEED = {// 填写需要申请的权限
            Manifest.permission.READ_EXTERNAL_STORAGE,// 读取外部存储
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入外部存储
            Manifest.permission.READ_PHONE_STATE,// 电话状态
            // .... 需要什么权限, 需要先声明 ....
            // 注意: 非危险权限不需要申请, 一定不能加进来, 否则影响业务逻辑
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        handler = new Handler();
        // 设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, colorStatuBar, false);
        // 申请权限
        showPermission();
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
        pingHelper.ping(this, getString(R.string.ping_address));
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        // 1.获取当前APP的版本号
        int localVersion = Ogg.getLocalVersion(this);
        // 2.请求LeanClound最新版本
        GetUpdateHelper getUpdateHelper = new GetUpdateHelper();
        getUpdateHelper.setOnGetUpdateListener(updateBean -> {
            // 2.1.获取到最新的版本号
            int newVersion = Integer.valueOf(updateBean.getNewVersionCode());
            // 3.有更新版本
            if (newVersion > localVersion) {
                widgetUpdate.setVisibility(View.VISIBLE);
                widgetUpdate.setOnClickOkListener(() -> {
                    /* 4.判断SD卡是否挂载并留有足够空间 */
                    SDHelper sdHelper = new SDHelper();
                    sdHelper.setOnSdErrorListener(() -> {
                        // 5.空间不足--> 继续切换到下个界面
                        widgetUpdate.setVisibility(View.GONE);
                        toGuideOrMain();
                    });
                    sdHelper.setOnSdNormalListener(() -> {
                        // TODO: 2018/6/26 0026  空间正常
                        // 切换到下载界面
                        widgetDownload.setVisibility(View.VISIBLE);
                        // TODO: 2018/6/27 0027  执行下载逻辑
                        
                        Tgg.show(this, "下载新版本", 0);
                    });
                    sdHelper.getRemindMemory(this, updateBean.getNewVersionSize());
                });
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
        finish();
        Process.killProcess(Process.myPid());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 1.把所有回调的权限状态添加到自定义集合
        List<Integer> ints = new ArrayList<>();
        for (int grantResult : grantResults) {
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
}
