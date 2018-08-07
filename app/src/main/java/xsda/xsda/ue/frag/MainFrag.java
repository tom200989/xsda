package xsda.xsda.ue.frag;

import android.view.View;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.hiber.hiber.RootFrag;

import xsda.xsda.R;
import xsda.xsda.bean.UserClientBean;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

public class MainFrag extends RootFrag {

    @Override
    public int onInflateLayout() {
        return R.layout.frag_main;
    }

    @Override
    public void onNexts(Object o, View view, String s) {
        UserClientBean uBean = (UserClientBean) o;
        AVUser avUser = uBean.getAvUser();
        AVIMClient avimClient = uBean.getAvimClient();
        Lgg.t(Cons.TAG).vv("username: " + avUser.getUsername());
        Lgg.t(Cons.TAG).vv("phoneNum: " + avUser.getMobilePhoneNumber());
        Lgg.t(Cons.TAG).vv("clientId: " + avimClient.getClientId());
    }

    @Override
    public boolean onBackPresss() {
        return false;
    }
}
