package xsda.xsda.ue.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.hiber.bean.RootProperty;

import butterknife.BindView;
import xsda.xsda.BuildConfig;
import xsda.xsda.R;
import xsda.xsda.helper.KickService;
import xsda.xsda.ue.frag.LoginFrag;
import xsda.xsda.ue.frag.MainFrag;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.widget.OfflineWidget;

/*
 * Created by qianli.ma on 2019/4/25 0025.
 */
public class MainActivity extends BaseActivity {

    public Class[] frags = {MainFrag.class};

    @BindView(R.id.wd_main_offline)
    OfflineWidget wdMainOffline;

    @Override
    public RootProperty initProperty() {
        return getRootProperty();
    }

    @Override
    public void onNexts() {

    }

    @Override
    public void initViewFinish(int layoutId) {
        super.initViewFinish(layoutId);
        new Handler().postDelayed(() -> startService(new Intent(this, KickService.class)), 500);
    }

    @Override
    public boolean onBackClick() {
        if (wdMainOffline.getVisibility() == View.VISIBLE) {
            Lgg.t(Cons.TAG).ii("click the backpress");
            return true;
        }
        return false;
    }

    /**
     * @return 获取初始化对象
     */
    public RootProperty getRootProperty() {
        RootProperty rootProperty = new RootProperty();
        rootProperty.setColorStatusBar(R.color.colorCompany);
        rootProperty.setContainId(R.id.fl_main_contain);
        rootProperty.setFullScreen(true);
        rootProperty.setLayoutId(R.layout.activity_main);
        rootProperty.setTAG("RootActivity");
        rootProperty.setSaveInstanceState(false);
        rootProperty.setProjectDirName(Cons.INSTALL_FILEPATH);
        rootProperty.setFragmentClazzs(frags);
        rootProperty.setPermissionCode(0x100);
        rootProperty.setPackageName(BuildConfig.APPLICATION_ID);
        return rootProperty;
    }

    @Override
    protected void otherLogin() {
        wdMainOffline.setVisibility(View.VISIBLE);
        wdMainOffline.setOnClickOfflineOkListener(() -> toFragActivity(getClass(), SplashActivity.class, LoginFrag.class, null, false));
    }
}
