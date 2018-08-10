package xsda.xsda.ue.frag;

import android.view.View;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.hiber.hiber.RootFrag;

import butterknife.Bind;
import xsda.xsda.R;
import xsda.xsda.bean.UserClientBean;
import xsda.xsda.helper.AVClientHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.OfflineWidget;

/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

public class MainFrag extends RootFrag {

    @Bind(R.id.widget_offline)
    OfflineWidget widgetOffline;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_main;
    }

    @Override
    public void onNexts(Object o, View view, String s) {
        Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":onNexts()");
        UserClientBean uBean = (UserClientBean) o;
        AVUser avUser = uBean.getAvUser();
        AVIMClient avimClient = uBean.getAvimClient();
        Lgg.t(Cons.TAG).ii("username: " + avUser.getUsername());
        Lgg.t(Cons.TAG).ii("phoneNum: " + avUser.getMobilePhoneNumber());
        Lgg.t(Cons.TAG).ii("clientId: " + avimClient.getClientId());
        AVClientHelper avClientHelper = new AVClientHelper(getActivity());
        avClientHelper.setOnSetSinglePointLoginConnectionOfflineListener((client, code) -> {
            // TODO: 2018/8/9 0009  离线的处理
            widgetOffline.setVisibility(View.VISIBLE);
            widgetOffline.setOnClickOfflineOkListener(() -> toFrag(getClass(), LoginFrag.class, null, false));
        });
        avClientHelper.setOnSetSinglePointLoginConnectionPausedListener(client -> {
            Tgg.show(getActivity(), "即时通许断连", 2500);
        });
        avClientHelper.setOnSetSinglePointConnectionResumeListener(client -> {
            Tgg.show(getActivity(), "即时通许重连", 2500);
        });
        avClientHelper.setSinglePointLoginManager();
    }

    @Override
    public boolean onBackPresss() {
        if (widgetOffline.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }
}
