package xsda.xsda.ue.root;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.githang.statusbar.StatusBarCompat;
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xsda.xsda.R;
import xsda.xsda.ue.frag.DownFrag;
import xsda.xsda.ue.frag.ForgotPsdFrag;
import xsda.xsda.ue.frag.GuideFrag;
import xsda.xsda.ue.frag.LoginFrag;
import xsda.xsda.ue.frag.NetErrFrag;
import xsda.xsda.ue.frag.RegisterFrag;
import xsda.xsda.ue.frag.SplashFrag;
import xsda.xsda.ue.frag.UpdateFrag;
import xsda.xsda.utils.Cons;

/*
 * Created by qianli.ma on 2018/6/20 0020.
 */

@SuppressLint("Registered")
public abstract class RootActivity extends FragmentActivity {

    // TOAT: 0.初始化基本配置
    int colorRoot = R.color.colorRoot;
    String rootDirName = Cons.INSTALL_FILEPATH;
    String TAG = "RootActivity";

    /* TOAT: 1.定义权限返回码(自定义) */
    private static final int REQUEST_NEED = 0x100;
    private static String[] PERMISSIONS_NEED = {// 填写需要申请的权限
            Manifest.permission.READ_EXTERNAL_STORAGE,// 读取外部存储
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入外部存储
            Manifest.permission.READ_PHONE_STATE// 电话状态
            // .... 需要什么权限, 需要先声明 ....
            // 注意: 非危险权限不需要申请, 一定不能加进来, 否则影响业务逻辑
    };

    /* TOAT: 2.定义所有的fragment(自定义) */
    public Class[] frags = new Class[]{// 所有的fragment
            SplashFrag.class,// 启动
            UpdateFrag.class,// 提示更新
            NetErrFrag.class, // 错误
            DownFrag.class, // 下载
            GuideFrag.class,// 引导
            RegisterFrag.class,// 注册
            ForgotPsdFrag.class,// 重置密码
            LoginFrag.class// 登陆
    };
    public FraHelpers fraHelpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置无标题栏(必须位于 super.onCreate(savedInstanceState) 之上)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // 填充视图
        setContentView(onCreateLayout());
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(colorRoot), false);
        // 初始化权限
        initPermission();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        
        /* 
         * 重写这个方法是为了解决: 用户点击权限允许后界面无法继续加载的bug.
         * NoSaveInstanceStateActivityName(): 一般由第一个Activity进行实现
         * onSaveInstanceState()方法在获取权限时, 导致fragment初始化失败
         * 如果当前的Activity没有必要保存状态 (默认是: Activity被后台杀死后,系统会保存Activity状态)
         * 则不需要调用 「super.onSaveInstanceState(outState)」这个方法
         */

        String noSaveInstanceStateActivityName = NoSaveInstanceStateActivityName();
        if (!TextUtils.isEmpty(noSaveInstanceStateActivityName)) {
            if (!getClass().getSimpleName().equalsIgnoreCase(noSaveInstanceStateActivityName)) {
                super.onSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            boolean isDispatcher = onBackClick();
            if (!isDispatcher) {
                // 如果fragment没有处理--> 则直接退出
                super.onBackPressed();
            }
        }
    }

    /**
     * 初始化权限
     */
    private void initPermission() {
        Log.i(TAG, getClass().getSimpleName() + ":initPermission()");
        // 6.0 以上申请权限
        if (Build.VERSION.SDK_INT < 23) {
            Log.i(TAG, getClass().getSimpleName() + ":Build.VERSION.SDK_INT < 23");
            initFragment();
        } else {
            // 1.开启对应的权限(同一个组的权限只需要申请一个, 同组的权限即可开通使用)
            int exstorePer = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int phonePer = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

            // 2.权限本身不允许--> 请求
            if (exstorePer == PackageManager.PERMISSION_DENIED // 读写权限
                        | phonePer == PackageManager.PERMISSION_DENIED // 电话记录权限
                    ) {
                Log.i(TAG, getClass().getSimpleName() + ":initPermission()--> requestPermissions");
                ActivityCompat.requestPermissions(this, PERMISSIONS_NEED, REQUEST_NEED);
            } else {// 2.权限本身允许--> 执行业务逻辑
                initFragment();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_NEED:// 基本的权限申请
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
        Log.i(TAG, getClass().getSimpleName() + ":applyBasePermission()");

        for (String permission : permissions) {
            Log.i(TAG, "permission: " + permission);
        }

        // 1.把所有回调的权限状态添加到自定义集合
        List<Integer> ints = new ArrayList<>();
        for (int grantResult : grantResults) {
            ints.add(grantResult);
        }

        // 2.判断: 如果有任意一个权限未通过, 则杀死进程(或者自定义处理)
        if (ints.contains(PackageManager.PERMISSION_DENIED)) {
            // TOAT: 此处可自定义操作
            finish();
            Log.i(TAG, getClass().getSimpleName() + ":permission not pass");
        } else {
            initFragment();
            File installRootDir = createInstallRootDir(rootDirName);
            Log.i(TAG, getClass().getSimpleName() + ":create dir state: " + installRootDir);
        }
    }


    /**
     * 初始化fragment
     */
    private void initFragment() {
        Log.i(TAG, getClass().getSimpleName() + ":initFragment()");
        // 容器
        int contain = onCreateContain();
        Class firstFrag = onCreateFirstFragment();
        // 初始化fragment
        getFragHelper(contain, firstFrag);
    }

    /**
     * 初始化frahelper单例
     *
     * @param contain   容器
     * @param firstFrag 首屏
     */
    private void getFragHelper(int contain, Class firstFrag) {
        Log.i(TAG, getClass().getSimpleName() + ":getFragHelper()");
        if (fraHelpers == null) {
            synchronized (FraHelpers.class) {
                if (fraHelpers == null) {
                    fraHelpers = new FraHelpers(this, frags, firstFrag, contain);
                    Log.i(TAG, getClass().getSimpleName() + ":new FraHelpers()");
                }
            }
        } else {
            onNexts();
        }
    }

    /**
     * @return 创建安装包根目录
     */
    public File createInstallRootDir(String dirName) {
        Log.i(TAG, getClass().getSimpleName() + ":createInstallRootDir()");
        File sdcard = Environment.getExternalStorageDirectory();
        String installDirPath = sdcard.getAbsolutePath() + File.separator + dirName;
        File installDir = new File(installDirPath);
        if (!installDir.exists() | !installDir.isDirectory()) {
            boolean isCreate = installDir.mkdir();
            Log.i(TAG, getClass().getSimpleName() + ":createInstallRootDir()--> install dir create: " + isCreate);
        }
        return installDir;
    }
    
    /* -------------------------------------------- abstract -------------------------------------------- */
    /**
     * @return layoutId
     */
    public abstract int onCreateLayout();

    /**
     * 不需要保存状态的Activity类名
     */
    public abstract String NoSaveInstanceStateActivityName();

    /**
     * @return containId
     */
    public abstract int onCreateContain();

    /**
     * @return first fragment class
     */
    public abstract Class onCreateFirstFragment();

    /**
     * 你的业务逻辑
     */
    public abstract void onNexts();

    /**
     * 回退键的点击事件
     *
     * @return true:自定义逻辑 false:super.onBackPress()
     */
    public abstract boolean onBackClick();
    
    /* -------------------------------------------- helper -------------------------------------------- */

    /**
     * 跳转到别的fragment
     *
     * @param classWhichFragmentStart 当前
     * @param targetFragmentClass     目标
     * @param attach                  额外附带数据对象
     * @param isTargetReload          是否重载视图
     */
    public void toFrag(Class classWhichFragmentStart, Class targetFragmentClass, Object attach, boolean isTargetReload) {
        Log.i(TAG, getClass().getSimpleName() + ":toFrag()--> " + targetFragmentClass.getSimpleName() + "isReload: " + isTargetReload);
        FragBean fragBean = new FragBean();
        fragBean.setCurrentFragmentClass(classWhichFragmentStart);
        fragBean.setTargetFragmentClass(targetFragmentClass);
        fragBean.setAttach(attach == null ? "" : attach);
        // 1.先跳转
        fraHelpers.transfer(targetFragmentClass, isTargetReload);
        // 2.在传输(否则会出现nullPointException)
        EventBus.getDefault().postSticky(fragBean);
    }

    /**
     * @param target 移除指定的fragment
     */
    public void removeFrag(Class target) {
        fraHelpers.remove(target);
    }

    /**
     * 结束当前Activit
     */
    public void finishOver() {
        RootHelper.finishOver(this);
    }

    /**
     * 杀死APP
     */
    public void kill() {
        RootHelper.kill();
    }

    /**
     * 吐司提示
     *
     * @param tip      提示
     * @param duration 时长
     */
    public void toast(String tip, int duration) {
        RootHelper.toast(this, tip, duration);
    }

    /**
     * 跳转(默认方式)
     *
     * @param context   当前环境
     * @param clazz     目标
     * @param isDefault 是否默认方式
     */
    public void toActivity(Activity context, Class<?> clazz, boolean isDefault) {
        RootHelper.toActivity(context, clazz, true, true, false, 0);
    }
}
