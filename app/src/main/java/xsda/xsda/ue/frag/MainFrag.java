package xsda.xsda.ue.frag;

import android.view.View;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import xsda.xsda.R;
import xsda.xsda.bean.LoginBean;
import xsda.xsda.helper.LoginOrOutHelper;
import xsda.xsda.ue.activity.SplashActivity;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.wxapi.WechatInfo;

/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

public class MainFrag extends RootFrag {


    // @BindView(R.id.rcv_main_left)
    // RecyclerView rcvMainLeft;
    // @BindView(R.id.rcv_main_right)
    // RecyclerView rcvMainRight;
    // @BindView(R.id.wd_main_offline)
    // OfflineWidget wdOffline;
    @BindView(R.id.tv_main_test)
    TextView tvTest;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_mains;
    }

    @Override
    public void onNexts(Object o, View view, String s) {
        if (o instanceof WechatInfo) {
            WechatInfo wechatInfo = (WechatInfo) o;
            Lgg.t("ma_test").ii("wechat info: " + wechatInfo.getNickname());
        }
    }

    // TODO 开始做退出登陆

    @Override
    public void initViewFinish(View inflateView) {
        clickEvent();
    }

    private void clickEvent() {
        tvTest.setOnClickListener(v -> {
            // 移除微信登陆
            Platform plat = ShareSDK.getPlatform(Wechat.NAME);
            plat.removeAccount(true);
            Lgg.t("ma_test").ii("plat had remove");
            // 从服务器退出
            LoginOrOutHelper loginOrOutHelper = new LoginOrOutHelper(activity);
            loginOrOutHelper.setOnLogOutSuccessListener(() -> {
                LoginBean loginBean = Ogg.readLoginJson(activity);
                Ogg.saveLoginJson(activity, loginBean.getPhoneNum(), loginBean.getPassword(), false);
                toFragActivity(getClass(), SplashActivity.class, LoginFrag.class, null, true, true, 0);
                Tgg.show(activity, "登出成功", 2500);
            });
            loginOrOutHelper.setOnLogOutFailedListener(e -> Tgg.show(activity, "登出失败", 2500));
            loginOrOutHelper.logout();

        });
    }

    @Override
    public boolean onBackPresss() {
        return false;
    }

    // @OnClick(R.id.bt_quit_wechat)
    // public void onViewClicked() {
    //     Platform plat = ShareSDK.getPlatform(Wechat.NAME);
    //     plat.removeAccount(true);
    // }
}
