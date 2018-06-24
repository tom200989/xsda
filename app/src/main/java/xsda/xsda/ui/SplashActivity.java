package xsda.xsda.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import xsda.xsda.helper.PingHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Sgg;
import xsda.xsda.widget.NetErrorWidget;
import xsda.xsda.widget.PrivacyWidget;
import xsda.xsda.widget.SplashWidget;
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
        if (!Sgg.getInstance(this).getBoolean(Cons.PRIVACY_READ, false)) {
            // 延迟2秒出现隐私条款
            handler.postDelayed(() -> widgetPrivacy.setVisibility(View.VISIBLE), 0);
            // 设置隐私条款的点击事件
            widgetPrivacy.setOnClickAgreeListener(() -> {
                // 提交隐私条款「已阅读」
                Sgg.getInstance(this).putBoolean(Cons.PRIVACY_READ, true);
                widgetPrivacy.setVisibility(View.GONE);
                // 检测新版本
                checkUpdate();
            });
        } else {
            // 检测新版本
            checkUpdate();
        }

    }

    /**
     * 3.检查更新
     */
    private void checkUpdate() {
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
            toast("检测成功", 2000);
            // TODO: 2018/6/22 0022 跳转到登陆界面
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
                checkUpdate();
            });
        });
        pingHelper.ping(this, getString(R.string.ping_address));
    }

    @Override
    public void onBackPressed() {
        finish();
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
