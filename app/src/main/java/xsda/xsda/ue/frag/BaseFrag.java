package xsda.xsda.ue.frag;
/*
 * Created by qianli.ma on 2018/8/13 0013.
 */

import android.support.annotation.Nullable;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.hiber.hiber.RootFrag;

import java.util.List;

import xsda.xsda.R;
import xsda.xsda.helper.TimerHelper;
import xsda.xsda.ue.activity.SplashActivity;
import xsda.xsda.utils.Avfield;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;

public class BaseFrag extends RootFrag {

    private TimerHelper timerHelper;// 定时器
    private int count = 0;// 计数器
    private int MAX_COUNT = 5;//  出错请求的最大次数

    @Override
    public boolean onBackPresss() {
        return false;
    }

    @Override
    public int onInflateLayout() {
        return 0;
    }

    @Override
    public void onNexts(Object o, View view, String s) {
        if (isNeedTimer()) {
            startTimer();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden & isNeedTimer()) {// 显示
            startTimer();
        } else {// 隐藏
            stopTimer();
        }
    }

    /**
     * 是否需要启动定时器
     * <tr></tr>
     * <b>该方法被子类重写,如不重写,则默认不启动定时器</b>
     *
     * @return true:需要
     */
    public boolean isNeedTimer() {
        return false;
    }

    /**
     * 启动定时器
     */
    public void startTimer() {
        Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":startTimer()");
        stopTimer();
        Lgg.t(Cons.TAG).ii("Method-->" + getClass().getSimpleName() + " renew the timer");
        timerHelper = new TimerHelper(getActivity()) {
            @Override
            public void doSomething() {
                checkLogin();
            }
        };
        timerHelper.start(2000);
    }

    /**
     * 检查登陆
     */
    private void checkLogin() {
        Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":checkLogin()");
        // 1.获取手机号码
        String phoneNum = ((SplashActivity) getActivity()).avUser.getMobilePhoneNumber();
        // 2.建立查询对象
        AVQuery<AVObject> query = new AVQuery<>(Avfield.LoginStatus.classname);
        query.whereEqualTo(Avfield.LoginStatus.phoneNum, phoneNum);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    count = 0;
                    if (list == null || list.size() <= 0) {
                        checkLoginError(null);
                    } else {
                        checkDeviceId(list.get(0));
                    }
                } else {// 出错--> 重复请求5次
                    if (count > MAX_COUNT) {
                        checkLoginError(e);
                        count = 0;
                    } else {
                        count++;
                    }
                }
            }
        });
    }

    /**
     * 检查设备匹对
     *
     * @param avo loginstatus对象
     */
    private void checkDeviceId(AVObject avo) {
        // 获取服务器ID以及本地ID
        String deviceIdFromServer = avo.getString(Avfield.LoginStatus.deviceId);
        String deviceIdFromLocal = Sgg.getInstance(getActivity()).getString(Cons.SP_DEVICE_ID, "");
        if (!deviceIdFromServer.equalsIgnoreCase(deviceIdFromLocal) ) {
            /* 如果ID不相等 & 设备处于登陆状态--> 意味这其他设备登陆 */
            otherDevicesLogin(avo);
        }
    }

    /**
     * 其他设备登陆
     */
    public void otherDevicesLogin(AVObject avo) {
        Tgg.show(getActivity(), R.string.base_other_login, 2500);
        toFrag(getClass(), LoginFrag.class, null, false);
        Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":otherDevicesLogin()");
    }

    /**
     * 检查登陆接口出错(该方法由子类选择性实现)
     */
    private void checkLoginError(@Nullable AVException e) {
        if (e != null && e.getCode() == 0) {
            Tgg.show(getActivity(), R.string.base_network_login, 2500);
            toFrag(getClass(), NetErrFrag.class, null, false);
            Egg.print(getClass().getSimpleName(), ":checkLoginError()", e, null);
        } else {
            Tgg.show(getActivity(), R.string.base_login_error, 2500);
            toFrag(getClass(), LoginFrag.class, null, false);
            Egg.print(getClass().getSimpleName(), ":checkLoginError()", e, null);
        }
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        if (timerHelper != null) {
            timerHelper.stop();
            timerHelper = null;
            Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":stopTimer()");
        }
    }
}
