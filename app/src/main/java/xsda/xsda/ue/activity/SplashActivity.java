package xsda.xsda.ue.activity;

import android.Manifest;

import com.hiber.bean.RootProperty;
import com.hiber.hiber.RootMAActivity;

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

public class SplashActivity extends RootMAActivity {

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

    private static String[] permissions = {// 填写需要申请的权限
            Manifest.permission.READ_EXTERNAL_STORAGE,// 读取外部存储
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入外部存储
            Manifest.permission.READ_PHONE_STATE// 电话状态
            // .... 需要什么权限, 需要先声明 ....
            // 注意: 非危险权限不需要申请, 一定不能加进来, 否则影响业务逻辑
    };

    @Override
    public RootProperty initProperty() {
        return getRootProperty();
    }

    @Override
    public void onNexts() {

    }

    @Override
    public boolean onBackClick() {
        return false;
    }

    /**
     * @return 获取初始化对象
     */
    public RootProperty getRootProperty() {
        RootProperty rootProperty = new RootProperty();
        rootProperty.setColorStatusBar(R.color.colorCompany);
        rootProperty.setContainId(R.id.fl_splash_contain);
        rootProperty.setFullScreen(true);
        rootProperty.setLayoutId(R.layout.activity_splash);
        rootProperty.setTAG("RootActivity");
        rootProperty.setSaveInstanceState(false);
        rootProperty.setProjectDirName(Cons.INSTALL_FILEPATH);
        rootProperty.setFragmentClazzs(frags);
        rootProperty.setPermissionCode(0x100);
        rootProperty.setPermissions(permissions);
        return rootProperty;
    }
}
