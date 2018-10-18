package xsda.xsda.helper;
/*
 * Created by qianli.ma on 2018/7/31 0031.
 */

import android.app.Activity;
import android.os.Handler;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

import xsda.xsda.ue.app.XsdaApplication;
import xsda.xsda.utils.Avfield;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;

public class LoginOrOutHelper {

    private Activity activity;
    private String TAG = "LoginOrOutHelper";

    public LoginOrOutHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 登出
     */
    public void logout() {
        AVUser avUser = AVUser.getCurrentUser();
        String phoneNum = avUser.getMobilePhoneNumber();
        queryLoginByPhone(avUser, phoneNum, false);
    }

    /**
     * 登陆
     *
     * @param phoneNum 号码
     * @param password 密码
     */
    public void login(String phoneNum, String password) {
        checkUserIsExist(phoneNum, password);
    }

    /**
     * 0.判断用户是否存在
     *
     * @param phoneNum 号码
     * @param password 密码
     */
    private void checkUserIsExist(String phoneNum, String password) {
        Lgg.t(Cons.TAG).vv("Method--> " + getClass().getSimpleName() + ":checkUserIsExist()");
        loginPrepareNext();
        new Handler().postDelayed(() -> {
            UserExistHelper userExistHelper = new UserExistHelper(activity);
            userExistHelper.setOnExceptionListener(e -> activity.runOnUiThread(() -> {
                Lgg.t(TAG).ee("Method--> " + getClass().getSimpleName() + ":checkUserIsExist(): err: " + e.getMessage());
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
     * 1.调用登陆接口
     *
     * @param phoneNum 号码
     * @param password 密码
     */
    private void toLogin(String phoneNum, String password) {
        // 1.1.调用接口
        Lgg.t(Cons.TAG).vv("Method--> " + getClass().getSimpleName() + ":toLogin()");
        AVUser.loginByMobilePhoneNumberInBackground(phoneNum, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                activity.runOnUiThread(() -> {
                    if (e == null) {
                        // 2.登陆成功--> 查询登陆对象及其状态
                        queryLoginByPhone(avUser, phoneNum, true);
                        Lgg.t(Cons.TAG).ii("Method--> " + "LoginOrOutHelper: loginByMobilePhoneNumberInBackground" + "() success");
                    } else {
                        Lgg.t(TAG).ee("Method--> " + getClass().getSimpleName() + ":toLogin()--> error: " + e.getMessage());
                        loginErrorNext(e);
                        loginAfterNext();
                        Lgg.t(Cons.TAG).ee("Method--> " + "LoginOrOutHelper: loginByMobilePhoneNumberInBackground" + "() failed");
                    }
                });
            }
        });
    }

    /**
     * 2.通过手机号查询登陆|登出对象
     *
     * @param avUser    用户对象
     * @param phoneNum  号码
     * @param isToLogin true:执行登陆操作 false:执行登出操作
     */
    private void queryLoginByPhone(AVUser avUser, String phoneNum, boolean isToLogin) {
        // 2.1.查询LoginStatus是否存在手机号
        Lgg.t(Cons.TAG).vv("Method--> " + getClass().getSimpleName() + ":queryLoginByPhone()");
        AVQuery<AVObject> query = new AVQuery<>(Avfield.LoginStatus.classname);
        query.whereEqualTo(Avfield.LoginStatus.phoneNum, phoneNum);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list == null || list.size() <= 0) {
                        // 2.2.没有登陆过--> 首次创建登陆对象
                        createLoginStateToServer(avUser, phoneNum, isToLogin);
                    } else {
                        // 2.2.曾经登陆过--> 更新登陆对象状态
                        updateLoginNewStateToServer(avUser, list.get(0).getObjectId(), isToLogin);
                    }
                } else {
                    if (isToLogin) {
                        Lgg.t(TAG).ee("Method--> " + getClass().getSimpleName() + ":queryLoginByPhone()--> error: " + e.getMessage());
                        loginErrorNext(e);
                    } else {
                        logOutFailedNext(e);
                    }
                    loginAfterNext();
                    Egg.print(getClass().getSimpleName(), ":queryLoginByPhone()", e, isToLogin ? "login" : "logout");
                    Lgg.t(Cons.TAG).ee("Method--> " + getClass().getSimpleName() + ":queryLoginByPhone()" + (isToLogin ? "login" : "logout"));
                }
            }
        });

    }

    /**
     * 3.创建登陆状态对象到服务器
     *
     * @param avUser    用户对象
     * @param phoneNum  号码
     * @param isToLogin true:执行登陆操作 false:执行登出操作
     */
    private void createLoginStateToServer(AVUser avUser, String phoneNum, boolean isToLogin) {
        Lgg.t(Cons.TAG).vv("Method--> " + getClass().getSimpleName() + ":createLoginStateToServer()");
        AVObject avo = new AVObject(Avfield.LoginStatus.classname);
        avo.put(Avfield.LoginStatus.phoneNum, phoneNum);
        avo.put(Avfield.LoginStatus.deviceId, XsdaApplication.deviceId);
        avo.put(Avfield.LoginStatus.state, isToLogin);
        avo.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    if (isToLogin) {
                        loginSuccessNext(avUser);
                    } else {
                        AVUser.logOut();
                        logOutSuccessNext();
                    }
                    Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":createLoginStateToServer() " + "success" + (isToLogin ? "login" : "logout"));
                } else {
                    if (isToLogin) {
                        AVUser.logOut();
                        Lgg.t(TAG).ee("Method--> " + getClass().getSimpleName() + ":createLoginStateToServer()--> error: " + e.getMessage());
                        loginErrorNext(e);
                    } else {
                        logOutFailedNext(e);
                    }
                    Lgg.t(Cons.TAG).ee("Method--> " + getClass().getSimpleName() + ":createLoginStateToServer() failed" + (isToLogin ? "login" : "logout"));
                    Egg.print(getClass().getSimpleName(), ":createLoginStateToServer()", e, isToLogin ? "login" : "logout");
                }
                loginAfterNext();
            }
        });
    }

    /**
     * 3.更新已知的设备ID和登陆状态到服务器
     *
     * @param avUser    用户对象
     * @param objectId  已存在的objectId
     * @param isToLogin true:执行登陆操作 false:执行登出操作
     */
    private void updateLoginNewStateToServer(AVUser avUser, String objectId, boolean isToLogin) {
        Lgg.t(Cons.TAG).vv("Method--> " + getClass().getSimpleName() + ":updateLoginNewStateToServer() objectId: " + objectId);
        AVObject avo = AVObject.createWithoutData(Avfield.LoginStatus.classname, objectId);
        avo.put(Avfield.LoginStatus.deviceId, XsdaApplication.deviceId);
        avo.put(Avfield.LoginStatus.state, isToLogin);
        avo.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    if (isToLogin) {
                        loginSuccessNext(avUser);
                    } else {
                        AVUser.logOut();
                        logOutSuccessNext();
                    }
                    Lgg.t(Cons.TAG).ii("Method--> " + getClass().getSimpleName() + ":updateLoginNewStateToServer() success" + (isToLogin ? "login" : "logout"));
                } else {
                    if (isToLogin) {
                        AVUser.logOut();
                        Lgg.t(TAG).ee("Method--> " + getClass().getSimpleName() + ":updateLoginNewStateToServer()--> error: " + e.getMessage());
                        loginErrorNext(e);
                    } else {
                        logOutFailedNext(e);
                    }
                    Lgg.t(Cons.TAG).ee("Method--> " + getClass().getSimpleName() + ":updateLoginNewStateToServer() failed" + (isToLogin ? "login" : "logout"));
                    Egg.print(getClass().getSimpleName(), ":updateLoginNewStateToServer()", e, isToLogin ? "login" : "logout");
                }
                loginAfterNext();
            }
        });
    }

    /* -------------------------------------------- impl -------------------------------------------- */

    private OnLogOutFailedListener onLogOutFailedListener;

    // Inteerface--> 接口OnLogOutFailedListener
    public interface OnLogOutFailedListener {
        void logOutFailed(AVException e);
    }

    // 对外方式setOnLogOutFailedListener
    public void setOnLogOutFailedListener(OnLogOutFailedListener onLogOutFailedListener) {
        this.onLogOutFailedListener = onLogOutFailedListener;
    }

    // 封装方法logOutFailedNext
    private void logOutFailedNext(AVException e) {
        if (onLogOutFailedListener != null) {
            onLogOutFailedListener.logOutFailed(e);
        }
    }

    private OnLogOutSuccessListener onLogOutSuccessListener;

    // Inteerface--> 接口OnLogOutSuccessListener
    public interface OnLogOutSuccessListener {
        void logOutSuccess();
    }

    // 对外方式setOnLogOutSuccessListener
    public void setOnLogOutSuccessListener(OnLogOutSuccessListener onLogOutSuccessListener) {
        this.onLogOutSuccessListener = onLogOutSuccessListener;
    }

    // 封装方法logOutSuccessNext
    private void logOutSuccessNext() {
        if (onLogOutSuccessListener != null) {
            onLogOutSuccessListener.logOutSuccess();
        }
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
        void loginSuccess(AVUser avUser);
    }

    // 对外方式setOnLoginSuccessListener
    public void setOnLoginSuccessListener(OnLoginSuccessListener onLoginSuccessListener) {
        this.onLoginSuccessListener = onLoginSuccessListener;
    }

    // 封装方法loginSuccessNext
    private void loginSuccessNext(AVUser avUser) {
        if (onLoginSuccessListener != null) {
            onLoginSuccessListener.loginSuccess(avUser);
        }
    }
}
