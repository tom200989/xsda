package xsda.xsda.ue.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.avos.avoscloud.AVUser;
import com.hiber.bean.RootProperty;
import com.hiber.hiber.RootMAActivity;
import com.hiber.impl.RootEventListener;

import xsda.xsda.R;
import xsda.xsda.bean.KickBean;
import xsda.xsda.ue.frag.LoginFrag;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.RCode;
import xsda.xsda.utils.Tgg;

/*
 * Created by qianli.ma on 2019/4/25 0025.
 */
@SuppressLint("Registered")
public class BaseActivity extends RootMAActivity {

    public static AVUser avUser;// 用户对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initViewFinish(int layoutId) {
        super.initViewFinish(layoutId);
        // 1.设置kickbean接收监听器, 接收来自KickService的数据包
        setEventListener(KickBean.class, new RootEventListener<KickBean>() {
            @Override
            public void getData(KickBean kickBean) {
                // 2.处理数据包
                Lgg.t("ma_kick").ww("receiver kickbean");
                int type = kickBean.getType();
                if (type == RCode.KICK_NETERR) {
                    Tgg.show(getApplicationContext(), R.string.base_network_login, 2500);
                    toFragActivity(getClass(), SplashActivity.class, LoginFrag.class, null, false);
                } else if (type == RCode.KICK_LOGINERR) {
                    Tgg.show(getApplicationContext(), R.string.base_login_error, 2500);
                    toFragActivity(getClass(), SplashActivity.class, LoginFrag.class, null, false);
                } else if (type == RCode.KICK_OTHER_LOGIN) {
                    otherLogin();
                }
            }

            @Override
            public boolean isCurrentPageEffectOnly() {
                // 3.仅作用当前页面
                return true;
            }
        });
    }

    /**
     * 其他设备登陆
     */
    protected void otherLogin() {
        // 默认操作--> 该方法可由外部重写
        Tgg.show(this, R.string.base_other_login, 2500);
        toFragActivity(getClass(), SplashActivity.class, LoginFrag.class, null, true);
        Lgg.t("ma_kick").ww(" other devices is login");
    }

    @Override
    public RootProperty initProperty() {
        return null;
    }

    @Override
    public void onNexts() {

    }

    @Override
    public boolean onBackClick() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
