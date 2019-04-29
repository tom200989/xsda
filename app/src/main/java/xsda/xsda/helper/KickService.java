package xsda.xsda.helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.hiber.hiber.RootEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import xsda.xsda.bean.KickBean;
import xsda.xsda.ue.activity.BaseActivity;
import xsda.xsda.utils.Avfield;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.RCode;
import xsda.xsda.utils.Sgg;

/*
 * Created by qianli.ma on 2019/4/25 0025.
 */
public class KickService extends Service {

    private TimerHelper timerHelper;
    private int count = 0;// 计数器
    private int MAX_COUNT = 5;//  出错请求的最大次数

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (timerHelper != null) {
            timerHelper.stop();
            timerHelper = null;
        }

        timerHelper = new TimerHelper(null) {
            @Override
            public void doSomething() {
                checkLogin();
            }
        };
        timerHelper.start(3000);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHelper.stop();
        timerHelper = null;
    }

    /**
     * 检查登陆
     */
    private void checkLogin() {
        Lgg.t(getClass().getSimpleName()).ii("Method--> " + getClass().getSimpleName() + ":checkLogin()");

        // 0.检查是否处于登陆状态
        AVUser avUser = BaseActivity.avUser;
        if (avUser == null) {
            avUser = AVUser.getCurrentUser();
            if (avUser == null) {
                checkLoginError(null);
            }
        } else {
            // 1.获取手机号码
            String phoneNum = avUser.getMobilePhoneNumber();
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
    }

    /**
     * 检查登陆接口出错(该方法由子类选择性实现)
     */
    private void checkLoginError(AVException e) {
        if (e != null && e.getCode() == 0) {
            EventBus.getDefault().postSticky(new KickBean(RCode.KICK_NETERR));
            Egg.print(getClass().getSimpleName(), ":checkLoginError()", e, null);
        } else {
            EventBus.getDefault().postSticky(new KickBean(RCode.KICK_LOGINERR));
            Egg.print(getClass().getSimpleName(), ":checkLoginError()", e, null);
        }
    }

    /**
     * 检查设备匹对
     *
     * @param avo loginstatus对象
     */
    private void checkDeviceId(AVObject avo) {
        // 获取服务器ID以及本地ID
        String deviceIdFromServer = avo.getString(Avfield.LoginStatus.deviceId);
        String deviceIdFromLocal = Sgg.getInstance(getApplicationContext()).getString(Cons.SP_DEVICE_ID, "");
        Lgg.t(Cons.TAG).ww("id server: "+deviceIdFromServer);
        Lgg.t(Cons.TAG).ww("id local: "+deviceIdFromLocal);
        if (!deviceIdFromServer.equalsIgnoreCase(deviceIdFromLocal) & avo.getBoolean(Avfield.LoginStatus.state)) {
            /* 如果ID不相等 & 设备处于登陆状态--> 意味这其他设备登陆 */
            otherDevicesLogin();
        }
    }

    /**
     * 其他设备登陆
     */
    public void otherDevicesLogin() {
        RootEvent.sendEvent(new KickBean(RCode.KICK_OTHER_LOGIN), false);
        stopSelf();// 检测到其他设备登陆--> 停止［防登陆后台］
        Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":otherDevicesLogin()");
    }


}
