package xsda.xsda.ue.frag;
/*
 * Created by qianli.ma on 2018/8/13 0013.
 */

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
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;

public class BaseFrag extends RootFrag {

    private TimerHelper timerHelper;// 定时器
    public boolean isNeedTimer;// 定时器启动标记(默认不需要)
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
        if (isNeedTimer) {
            startTimer();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden & isNeedTimer) {// 显示
            startTimer();
        } else {// 隐藏
            stopTimer();
        }
    }

    /**
     * 启动定时器
     */
    public void startTimer() {
        if (timerHelper == null) {
            timerHelper = new TimerHelper(getActivity()) {
                @Override
                public void doSomething() {
                    checkLogin();
                }
            };
        }
        timerHelper.start(2000);
    }

    /**
     * 检查登陆
     */
    private void checkLogin() {
        // 1.获取手机号码
        String phoneNum = ((SplashActivity) getActivity()).avUser.getMobilePhoneNumber();
        // 2.建立查询对象
        AVQuery<AVObject> query = new AVQuery<>(Avfield.LoginStatus.classname);
        query.whereEqualTo(Avfield.LoginStatus.phoneNum, phoneNum);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() <= 0) {
                        checkLoginError();
                    } else {
                        checkDeviceId(list.get(0));
                    }
                } else {// 出错--> 重复请求5次
                    if (count > MAX_COUNT) {
                        checkLoginError();
                        count = 0;
                    } else {
                        checkLogin();
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
        if (!deviceIdFromServer.equalsIgnoreCase(deviceIdFromLocal)) {
            // 如果ID不相等--> 意味这其他设备登陆
            otherDevicesLogin();
        }
    }

    /**
     * 其他设备登陆
     */
    public void otherDevicesLogin() {
        Tgg.show(getActivity(), R.string.base_other_login, 2500);
        toFrag(getClass(), LoginFrag.class, null, false);
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        if (timerHelper != null) {
            timerHelper.stop();
            timerHelper = null;
        }
    }

    /**
     * 检查登陆接口出错(该方法由子类选择性实现)
     */
    private void checkLoginError() {
        Tgg.show(getActivity(), R.string.base_network_error, 2500);
        toFrag(getClass(), LoginFrag.class, null, false);
    }
}
