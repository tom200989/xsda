package xsda.xsda.ue.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.bottomtab.bottomtab.BottomTab;
import com.hiber.bean.RootProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import xsda.xsda.BuildConfig;
import xsda.xsda.R;
import xsda.xsda.helper.KickService;
import xsda.xsda.ue.frag.CartFrag;
import xsda.xsda.ue.frag.LoginFrag;
import xsda.xsda.ue.frag.MainFrag;
import xsda.xsda.ue.frag.MyFrag;
import xsda.xsda.ue.frag.PicFrag;
import xsda.xsda.ue.frag.VideoFrag;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.widget.OfflineWidget;

/*
 * Created by qianli.ma on 2019/4/25 0025.
 */
public class MainActivity extends BaseActivity {

    public Class[] frags = {// 页面
            MainFrag.class, // 商城-3
            PicFrag.class, // 图片-1
            VideoFrag.class, // 视频-2
            CartFrag.class, // 购物车-4
            MyFrag.class // 我的-5
    };

    @BindView(R.id.wd_main_offline)
    OfflineWidget wdMainOffline;// 离线面板
    @BindView(R.id.bottom_tab)
    BottomTab bottomTab;// 底部切换栏
    
    private List<Class> fragList = new ArrayList<>();

    @Override
    public RootProperty initProperty() {
        return getRootProperty();
    }

    @Override
    public void onNexts() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void initViewFinish(int layoutId) {
        super.initViewFinish(layoutId);
        // 0.转换集合
        fragList = Ogg.turnFragList(0, 2, Arrays.asList(frags));
        // 1.创建底部切换栏
        createBottomTab();
        // 2.启动后台防踢服务
        new Handler().postDelayed(() -> startService(new Intent(this, KickService.class)), 500);
    }

    /**
     * 创建底部切换栏
     */
    private void createBottomTab() {
        // 初始化资源
        int[] icons = {R.drawable.pic, R.drawable.video, R.drawable.shop, R.drawable.cart, R.drawable.my};
        int[] titles = {R.string.main_bottomtab_pic, R.string.main_bottomtab_video, R.string.main_bottomtab_shop, R.string.main_bottomtab_cart, R.string.main_bottomtab_my};
        // 设置切换
        bottomTab.setOnBottomTabItemClickListener(position -> {
            Ogg.hideKeyBoard(this);
            toFrag(getClass(), fragList.get(position), null, false);
        });
        bottomTab.create(icons, titles);
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
