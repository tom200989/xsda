package xsda.xsda.helper;
/*
 * Created by qianli.ma on 2018/7/31 0031.
 */

import android.app.Activity;
import android.os.Handler;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientOpenOption;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

public class LoginHelper {

    private Activity activity;

    public LoginHelper(Activity activity) {
        this.activity = activity;
    }

    public void login(String phoneNum, String password) {
        loginPrepareNext();
        new Handler().postDelayed(() -> {
            UserExistHelper userExistHelper = new UserExistHelper(activity);
            userExistHelper.setOnExceptionListener(e -> activity.runOnUiThread(() -> {
                loginErrorNext(e);
                loginAfterNext();
            }));
            userExistHelper.setOnUserNotExistListener(() -> activity.runOnUiThread(() -> {
                loginUserNotExistNext();
                loginAfterNext();
            }));
            userExistHelper.setOnUserHadExistListener(() -> toLogin(phoneNum, password));
            userExistHelper.isExist(phoneNum);
        }, 1000);
    }

    /**
     * 去登陆
     *
     * @param phoneNum 号码
     * @param password 密码
     */
    private void toLogin(String phoneNum, String password) {
        // 1.登陆存储
        AVUser.loginByMobilePhoneNumberInBackground(phoneNum, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                activity.runOnUiThread(() -> {
                    if (e == null) {
                        // 2.连接聊天室
                        connectClient(avUser);
                        Lgg.t(Cons.TAG).ii("Method--> " + "LoginHelper: loginByMobilePhoneNumberInBackground" + "() success");
                    } else {
                        loginErrorNext(e);
                        loginAfterNext();
                        Lgg.t(Cons.TAG).ii("Method--> " + "LoginHelper: loginByMobilePhoneNumberInBackground" + "() failed");
                    }
                });
            }
        });
    }

    /**
     * 连接聊天室
     *
     * @param avUser 用户对象
     */
    private void connectClient(AVUser avUser) {
        Lgg.t(Cons.TAG).vv("Method--> " + getClass().getSimpleName() + ":connectClient()");
        AVIMClientOpenOption option = new AVIMClientOpenOption();
        option.setForceSingleLogin(true);// 设置强制登陆
        /* 注意: 一定要使用--> AVIMClient.getInstance(AVUser, Tag), 其他方法没用 */
        AVIMClient client = AVIMClient.getInstance(avUser, avUser.getMobilePhoneNumber());
        client.open(option, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                activity.runOnUiThread(() -> {
                    if (e == null) {
                        loginSuccessNext(avUser, client);
                        Lgg.t(Cons.TAG).ii("Method--> " + "LoginHelper: client connect success--> clientId: " + "" + client.getClientId());
                    } else {
                        AVUser.logOut();
                        loginErrorNext(e);
                        Lgg.t(Cons.TAG).ii("Method--> " + "LoginHelper: client connect failed");
                    }
                    loginAfterNext();
                });
            }
        });
    }

    private OnLoginUserNotExistListener onLoginUserNotExistListener;

    // 接口OnLoginUserNotExistListener
    public interface OnLoginUserNotExistListener {
        void loginUserNotExist();
    }

    // 对外方式setOnLoginUserNotExistListener
    public void setOnLoginUserNotExistListener(OnLoginUserNotExistListener onLoginUserNotExistListener) {
        this.onLoginUserNotExistListener = onLoginUserNotExistListener;
    }

    // 封装方法loginUserNotExistNext
    private void loginUserNotExistNext() {
        if (onLoginUserNotExistListener != null) {
            onLoginUserNotExistListener.loginUserNotExist();
        }
    }

    private OnLoginPrepareListener onLoginPrepareListener;

    // 接口OnLoginPrepareListener
    public interface OnLoginPrepareListener {
        void loginPrepare();
    }

    // 对外方式setOnLoginPrepareListener
    public void setOnLoginPrepareListener(OnLoginPrepareListener onLoginPrepareListener) {
        this.onLoginPrepareListener = onLoginPrepareListener;
    }

    // 封装方法loginPrepareNext
    private void loginPrepareNext() {
        activity.runOnUiThread(() -> {
            if (onLoginPrepareListener != null) {
                onLoginPrepareListener.loginPrepare();
            }
        });
    }

    private OnLoginAfterListener onLoginAfterListener;

    // 接口OnLoginAfterListener
    public interface OnLoginAfterListener {
        void loginAfter();
    }

    // 对外方式setOnLoginAfterListener
    public void setOnLoginAfterListener(OnLoginAfterListener onLoginAfterListener) {
        this.onLoginAfterListener = onLoginAfterListener;
    }

    // 封装方法loginAfterNext
    private void loginAfterNext() {
        activity.runOnUiThread(() -> {
            if (onLoginAfterListener != null) {
                onLoginAfterListener.loginAfter();
            }
        });
    }

    private OnLoginErrorListener onLoginErrorListener;

    // 接口OnLoginErrorListener
    public interface OnLoginErrorListener {
        void loginError(AVException ex);
    }

    // 对外方式setOnLoginErrorListener
    public void setOnLoginErrorListener(OnLoginErrorListener onLoginErrorListener) {
        this.onLoginErrorListener = onLoginErrorListener;
    }

    // 封装方法loginErrorNext
    private void loginErrorNext(AVException ex) {
        if (onLoginErrorListener != null) {
            onLoginErrorListener.loginError(ex);
        }
    }

    private OnLoginSuccessListener onLoginSuccessListener;

    // 接口OnLoginSuccessListener
    public interface OnLoginSuccessListener {
        void loginSuccess(AVUser avUser, AVIMClient avClient);
    }

    // 对外方式setOnLoginSuccessListener
    public void setOnLoginSuccessListener(OnLoginSuccessListener onLoginSuccessListener) {
        this.onLoginSuccessListener = onLoginSuccessListener;
    }

    // 封装方法loginSuccessNext
    private void loginSuccessNext(AVUser avUser, AVIMClient avClient) {
        if (onLoginSuccessListener != null) {
            onLoginSuccessListener.loginSuccess(avUser, avClient);
        }
    }
}
