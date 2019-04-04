package xsda.xsda.ue.frag;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import xsda.xsda.R;
import xsda.xsda.bean.LoginBean;
import xsda.xsda.helper.LoginOrOutHelper;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.OfflineWidget;
import xsda.xsda.wxapi.WechatInfo;

/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

public class MainFrag extends BaseFrag {


    @BindView(R.id.rcv_main_left)
    RecyclerView rcvMainLeft;
    @BindView(R.id.rcv_main_right)
    RecyclerView rcvMainRight;
    @BindView(R.id.wd_main_offline)
    OfflineWidget wdOffline;
    @BindView(R.id.tv_test)
    TextView tvTest;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_mains;
    }

    // TODO 开始做退出登陆


    /**
     * 接收来自WXEntryActivity的wechatinfo
     *
     * @param wechatInfo 用户信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getWXInfo(WechatInfo wechatInfo) {
        Lgg.t("main_test").ii("openid: " + wechatInfo.getOpenid());
    }

    @Override
    public boolean isNeedTimer() {
        return true;
    }

    @Override
    public void onNexts(Object o, View view, String s) {
        super.onNexts(o, view, s);
        clickEvent();
    }

    @Override
    public void otherDevicesLogin(AVObject avo) {
        wdOffline.setVisibility(View.VISIBLE);
        wdOffline.setOnClickOfflineOkListener(() -> toFrag(getClass(), LoginFrag.class, null, false));
    }

    private void clickEvent() {
        tvTest.setOnClickListener(v -> {
            LoginOrOutHelper loginOrOutHelper = new LoginOrOutHelper(activity);
            loginOrOutHelper.setOnLogOutSuccessListener(() -> {
                LoginBean loginBean = Ogg.readLoginJson(activity);
                Ogg.saveLoginJson(activity, loginBean.getPhoneNum(), loginBean.getPassword(), false);
                toFrag(getClass(), LoginFrag.class, null, false);
                Tgg.show(activity, "登出成功", 2500);
            });
            loginOrOutHelper.setOnLogOutFailedListener(e -> Tgg.show(activity, "登出失败", 2500));
            loginOrOutHelper.logout();

            Platform plat = ShareSDK.getPlatform(Wechat.NAME);
            plat.removeAccount(true);
        });

        // tvTestMain.setOnClickListener(v -> {
        //     LoginOrOutHelper loginOrOutHelper = new LoginOrOutHelper(activity);
        //     loginOrOutHelper.setOnLogOutSuccessListener(() -> {
        //         LoginBean loginBean = Ogg.readLoginJson(activity);
        //         Ogg.saveLoginJson(activity, loginBean.getPhoneNum(), loginBean.getPassword(), false);
        //         toFrag(getClass(), LoginFrag.class, null, false);
        //         Tgg.show(activity, "登出成功", 2500);
        //     });
        //     loginOrOutHelper.setOnLogOutFailedListener(e -> Tgg.show(activity, "登出失败", 2500));
        //     loginOrOutHelper.logout();
        // });
    }

    @Override
    public boolean onBackPresss() {
        if (wdOffline.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    // @OnClick(R.id.bt_quit_wechat)
    // public void onViewClicked() {
    //     Platform plat = ShareSDK.getPlatform(Wechat.NAME);
    //     plat.removeAccount(true);
    // }
}
