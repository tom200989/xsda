package xsda.xsda.ue.activity;

import xsda.xsda.R;
import xsda.xsda.ue.frag.SplashFrag;
import xsda.xsda.ue.root.RootActivity;

public class SplashActivity extends RootActivity {


    @Override
    public int onCreateLayout() {
        // 初始化布局
        return R.layout.activity_splash;
    }

    @Override
    public String NoSaveInstanceStateActivityName() {
        // 不保留销毁后的状态(如果当前Activity不需要保留则传递类名)
        return getClass().getSimpleName();
    }

    @Override
    public int onCreateContain() {
        // fragment容器ID
        return R.id.fl_splash_contain;
    }

    @Override
    public Class onCreateFirstFragment() {
        // 初始化第一个fragment(传递字节码)
        return SplashFrag.class;
    }

    @Override
    public void onNexts() {
        // 这里执行你的业务逻辑
    }

    @Override
    public boolean onBackClick() {
        // 返回键监听(true:执行你的业务逻辑, false:默认退出)
        return false;
    }

}
