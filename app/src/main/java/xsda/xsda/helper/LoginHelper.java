package xsda.xsda.helper;
/*
 * Created by qianli.ma on 2018/7/31 0031.
 */

import android.app.Activity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientOpenOption;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

public class LoginHelper {

    private Activity activity;

    public LoginHelper(Activity activity) {
        this.activity = activity;
    }

    public void login(String phoneNum, String password) {
        loginPrepareNext();
        UserExistHelper userExistHelper = new UserExistHelper(activity);
        userExistHelper.setOnExceptionListener(e -> {
            loginErrorNext(e);
            loginAfterNext();
        });
        userExistHelper.setOnUserNotExistListener(() -> {
            loginUserNotExistNext();
            loginAfterNext();
        });
        userExistHelper.setOnUserHadExistListener(() -> toLogin(phoneNum, password));
        userExistHelper.isExist(phoneNum);
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
                if (e == null) {
                    // 2.连接聊天室
                    connectClient(avUser);
                } else {
                    loginErrorNext(e);
                    loginAfterNext();
                }
            }
        });
    }

    /**
     * 连接聊天室
     *
     * @param avUser 用户对象
     */
    private void connectClient(AVUser avUser) {
        AVIMClientOpenOption option = new AVIMClientOpenOption();
        option.setForceSingleLogin(true);// 设置强制登陆
        AVIMClient client = AVIMClient.getInstance(avUser);
        client.open(option, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    loginSuccessNext(avUser, client);
                } else {
                    AVUser.logOut();
                    loginErrorNext(e);
                }
                loginAfterNext();
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
        if (onLoginPrepareListener != null) {
            onLoginPrepareListener.loginPrepare();
        }
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
        if (onLoginAfterListener != null) {
            onLoginAfterListener.loginAfter();
        }
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
