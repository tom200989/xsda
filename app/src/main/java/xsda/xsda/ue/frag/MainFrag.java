package xsda.xsda.ue.frag;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;

import butterknife.Bind;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import xsda.xsda.R;
import xsda.xsda.helper.LoginOrOutHelper;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.OfflineWidget;

/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

public class MainFrag extends BaseFrag {

    @Bind(R.id.widget_offline)
    OfflineWidget widgetOffline;
    @Bind(R.id.tv_test_main)
    TextView tvTestMain;
    @Bind(R.id.bt_quit_wechat)
    Button btQuitWechat;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_main;
    }

    @Override
    public boolean isNeedTimer() {
        return true;
    }

    @Override
    public void onNexts(Object o, View view, String s) {
        super.onNexts(o, view, s);// super父类以启动定时器
        clickEvent();
    }

    @Override
    public void otherDevicesLogin(AVObject avo) {
        widgetOffline.setVisibility(View.VISIBLE);
        widgetOffline.setOnClickOfflineOkListener(() -> toFrag(getClass(), LoginFrag.class, null, false));
    }

    private void clickEvent() {
        tvTestMain.setOnClickListener(v -> {
            LoginOrOutHelper loginOrOutHelper = new LoginOrOutHelper(getActivity());
            loginOrOutHelper.setOnLogOutSuccessListener(() -> {
                toFrag(getClass(), LoginFrag.class, null, false);
                Tgg.show(getActivity(), "登出成功", 2500);
            });
            loginOrOutHelper.setOnLogOutFailedListener(e -> Tgg.show(getActivity(), "登出失败", 2500));
            loginOrOutHelper.logout();
        });
    }

    @Override
    public boolean onBackPresss() {
        if (widgetOffline.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.bt_quit_wechat)
    public void onViewClicked() {
        Platform plat = ShareSDK.getPlatform(Wechat.NAME);
        plat.removeAccount(true);
    }
}
