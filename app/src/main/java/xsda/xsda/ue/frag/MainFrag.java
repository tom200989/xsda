package xsda.xsda.ue.frag;

import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
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

    @Override
    public int onInflateLayout() {
        return R.layout.frag_main;
    }

    @Override
    public void onNexts(Object o, View view, String s) {
        // TODO: 2018/8/13 0013 启动检测单点登陆机制
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
}
