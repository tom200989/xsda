package xsda.xsda.helper;

import android.app.Activity;
import android.os.Handler;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

import xsda.xsda.ue.app.XsdaApplication;
import xsda.xsda.utils.Avfield;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Sgg;

public class VerifyCodeHelper {
    private Activity activity;

    public VerifyCodeHelper(Activity activity) {
        this.activity = activity;
    }
    
    /* -------------------------------------------- A.申请验证码 -------------------------------------------- */

    /**
     * A1.发起验证申请
     *
     * @param phoneNum 电话
     * @param password 密码
     */
    public void getVerifyCode(String phoneNum, String password) {

        // 1.1.从服务器获取时间 
        GetServerDateHelper getServerDateHelper = new GetServerDateHelper();
        getServerDateHelper.setOnGetServerErrorListener(this::getServerDateErrorNext);
        getServerDateHelper.setOnGetServerDateLongSuccessListener(millute -> {
            commitVerifyPrepareNext();
            new Handler().postDelayed(() -> {
                // 1.2.判断用户是否存在
                isUserExist(phoneNum, password, millute);
            }, 2000);

        });
        getServerDateHelper.get();
    }

    /**
     * A2.判断用户是否存在
     *
     * @param username 用户名(手机号)
     * @param password 密码
     * @param millute  服务器时间
     */
    private void isUserExist(String username, String password, long millute) {
        Lgg.t(Cons.TAG).ii("isUserExist");
        UserExistHelper userExistHelper = new UserExistHelper(activity);
        userExistHelper.setOnExceptionListener(this::requestVerifyErrorNext);
        userExistHelper.setOnUserNotExistListener(() -> createUserAndRequestVerify(username, password, millute));
        userExistHelper.setOnUserHadExistListener(this::userHadExistNext);
        userExistHelper.setOnGetUserExistAfterListener(this::commitVerifyAfterNext);
        userExistHelper.isExist(username);
    }

    /**
     * A2.创建用户并调起注册请求
     *
     * @param phoneNum 手机号
     * @param password 密码
     * @param millute  服务器时间
     */
    private void createUserAndRequestVerify(String phoneNum, String password, long millute) {
        Lgg.t(Cons.TAG).ii("createUserAndRequestVerify");
        AVUser user = new AVUser();
        user.setUsername(phoneNum);
        user.setPassword(password);
        // 2.3.其他属性可以像其他AVObject对象一样使用put方法添加
        user.put(Avfield.User.mobilePhoneNumber, phoneNum);
        user.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                Egg.print(getClass().getSimpleName(), "createUserAndRequestVerify", e, null);
                if (e != null) {
                    // 2.4.如果号码已经存在
                    if (e.getCode() == Egg.MOBILE_PHONE_NUMBER_HAS_ALREADY_BEEN_TAKEN) {
                        // 2.5.直接申请验证码
                        directToGetVerifyCode(phoneNum, millute);
                    } else {
                        // 2.6.申请失败
                        requestVerifyErrorNext(e);
                        commitVerifyAfterNext();
                    }
                    return;
                }
                // 2.7.申请成功
                requestVerifySuccess(millute);
                commitVerifyAfterNext();
            }
        });
    }

    /**
     * A3.直接调用请求验证码接口
     *
     * @param phoneNum 手机号码
     * @param millute  服务器时间
     */
    private void directToGetVerifyCode(String phoneNum, long millute) {
        Lgg.t(Cons.TAG).ii("directToGetVerifyCode");
        AVUser.requestMobilePhoneVerifyInBackground(phoneNum, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                Egg.print(getClass().getSimpleName(), "directToGetVerifyCode", e, null);
                if (e != null) {
                    // 3.1.申请失败
                    requestVerifyErrorNext(e);
                    commitVerifyAfterNext();
                    return;
                }
                // 3.2.申请验证码成功
                requestVerifySuccess(millute);
                commitVerifyAfterNext();
            }
        });
    }

    /* -------------------------------------------- B.提交验证码 -------------------------------------------- */

    /**
     * B1.提交验证码
     *
     * @param username   用户名(手机号)
     * @param verifyCode 验证码
     */
    public void commitVerifyCode(String username, String verifyCode) {
        Lgg.t(Cons.TAG).ii("commitVerifyCode");
        commitVerifyPrepareNext();
        new Handler().postDelayed(() -> {
            UserExistHelper userExistHelper = new UserExistHelper(activity);
            userExistHelper.setOnExceptionListener(this::requestVerifyErrorNext);
            userExistHelper.setOnUserNotExistListener(() -> verifyMobilePhone(username, verifyCode));
            userExistHelper.setOnUserHadExistListener(this::userHadExistNext);
            userExistHelper.setOnGetUserExistAfterListener(this::commitVerifyAfterNext);
            userExistHelper.isExist(username);
        }, 2000);

    }

    /**
     * B1.1.提交验证码
     *
     * @param username   用户名(手机号)
     * @param verifyCode 验证码
     */
    private void verifyMobilePhone(String username, String verifyCode) {
        // 1.1.提交验证码
        AVUser.verifyMobilePhoneInBackground(verifyCode, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                Egg.print(getClass().getSimpleName(), "verifyMobilePhone", e, "校验验证码失败");
                if (e != null) {
                    commitVerifyErrorNext(e);
                } else {
                    // 1.2.成功后提交验证成功信息
                    putVerifyUserInfo(username, true);
                }
                commitVerifyAfterNext();
            }
        });
    }

    /**
     * B2.成功后提交验证成功信息
     *
     * @param username 手机号
     * @param isVerify 是否成功验证
     */
    private void putVerifyUserInfo(String username, boolean isVerify) {
        Lgg.t(Cons.TAG).ii("putVerifyUserInfo");
        // 2.1.form: UserVerify
        AVObject userVerify = new AVObject(Avfield.UserVerify.classname);
        // 2.2.用户名(手机号)
        userVerify.put(Avfield.UserVerify.username, username);
        // 2.3.是否已经验证
        userVerify.put(Avfield.UserVerify.isPhoneVerify, isVerify);
        // 2.4.执行上传
        userVerify.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                Egg.print(getClass().getSimpleName(), "putVerifyUserInfo", e, null);
                // 2.5.无论提交信息是否成功--> 只要验证码步骤成功即算成功
                commitVerifySuccessNext();
                if (e == null) {
                    Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":" + "putVerifyUserInfo():" + "提交验证信息成功");
                } else {
                    Lgg.t(Cons.TAG).ee(getClass().getSimpleName() + ":" + "putVerifyUserInfo():" + "提交验证信息失败");
                }
            }
        });
    }
    
    /* -------------------------------------------- 回调 -------------------------------------------- */

    private OnCommitVerifyPrepareListener onCommitVerifyPrepareListener;

    // 接口OnCommitVerifyPrepareListener
    public interface OnCommitVerifyPrepareListener {
        void commitVerifyPrepare();
    }

    // 对外方式setOnCommitVerifyPrepareListener
    public void setOnCommitVerifyPrepareListener(OnCommitVerifyPrepareListener onCommitVerifyPrepareListener) {
        this.onCommitVerifyPrepareListener = onCommitVerifyPrepareListener;
    }

    // 封装方法commitVerifyPrepareNext
    private void commitVerifyPrepareNext() {
        if (onCommitVerifyPrepareListener != null) {
            onCommitVerifyPrepareListener.commitVerifyPrepare();
        }
    }

    private OnCommitVerifyAfterListener onCommitVerifyAfterListener;

    // 接口OnCommitVerifyAfterListener
    public interface OnCommitVerifyAfterListener {
        void commitVerifyAfter();
    }

    // 对外方式setOnCommitVerifyAfterListener
    public void setOnCommitVerifyAfterListener(OnCommitVerifyAfterListener onCommitVerifyAfterListener) {
        this.onCommitVerifyAfterListener = onCommitVerifyAfterListener;
    }

    // 封装方法commitVerifyAfterNext
    private void commitVerifyAfterNext() {
        if (onCommitVerifyAfterListener != null) {
            onCommitVerifyAfterListener.commitVerifyAfter();
        }
    }

    private OnUserHadExistListener onUserHadExistListener;

    // 接口OnUserHadExistListener
    public interface OnUserHadExistListener {
        void userHadExist();
    }

    // 对外方式setOnUserHadExistListener
    public void setOnUserHadExistListener(OnUserHadExistListener onUserHadExistListener) {
        this.onUserHadExistListener = onUserHadExistListener;
    }

    // 封装方法userHadExistNext
    private void userHadExistNext() {
        if (onUserHadExistListener != null) {
            onUserHadExistListener.userHadExist();
        }
    }


    /**
     * 验证成功
     *
     * @param millute 服务器时间
     */
    private void requestVerifySuccess(long millute) {
        // 5.1.得到时间存到sp中
        Sgg.getInstance(XsdaApplication.getApp()).putLong(Cons.SP_SERVER_DATE, millute);
        // 5.2.验证成功
        verifySuccessNext();
    }

    private OnGetServerDateErrorListener onGetServerDateErrorListener;

    // 接口OnGetServerDateErrorListener
    public interface OnGetServerDateErrorListener {
        void getServerDateError(Exception e);
    }

    // 对外方式setOnGetServerDateErrorListener
    public void setOnGetServerDateErrorListener(OnGetServerDateErrorListener onGetServerDateErrorListener) {
        this.onGetServerDateErrorListener = onGetServerDateErrorListener;
    }

    // 封装方法getServerDateErrorNext
    private void getServerDateErrorNext(Exception e) {
        activity.runOnUiThread(() -> {
            if (onGetServerDateErrorListener != null) {
                onGetServerDateErrorListener.getServerDateError(e);
            }
        });
    }

    private OnVerifyErrorListener onVerifyErrorListener;

    // 接口OnVerifyErrorListener
    public interface OnVerifyErrorListener {
        void verifyError(AVException e);
    }

    // 对外方式setOnVerifyErrorListener
    public void setOnGetVerifyErrorListener(OnVerifyErrorListener onVerifyErrorListener) {
        this.onVerifyErrorListener = onVerifyErrorListener;
    }

    // 封装方法verifyErrorNext
    private void requestVerifyErrorNext(AVException e) {
        activity.runOnUiThread(() -> {
            if (onVerifyErrorListener != null) {
                onVerifyErrorListener.verifyError(e);
            }
        });
    }

    private OnVerifySuccessListener onVerifySuccessListener;

    // 接口OnVerifySuccessListener
    public interface OnVerifySuccessListener {
        void verifySuccess();
    }

    // 对外方式setOnVerifySuccessListener
    public void setOnGetVerifySuccessListener(OnVerifySuccessListener onVerifySuccessListener) {
        this.onVerifySuccessListener = onVerifySuccessListener;
    }

    // 封装方法verifySuccessNext
    private void verifySuccessNext() {
        activity.runOnUiThread(() -> {
            if (onVerifySuccessListener != null) {
                onVerifySuccessListener.verifySuccess();
            }
        });
    }

    private OnCommitVerifyErrorListener onCommitVerifyErrorListener;

    // 接口OnCommitVerifyErrorListener
    public interface OnCommitVerifyErrorListener {
        void commitVerifyError(AVException e);
    }

    // 对外方式setOnCommitVerifyErrorListener
    public void setOnCommitVerifyErrorListener(OnCommitVerifyErrorListener onCommitVerifyErrorListener) {
        this.onCommitVerifyErrorListener = onCommitVerifyErrorListener;
    }

    // 封装方法commitVerifyErrorNext
    private void commitVerifyErrorNext(AVException e) {
        activity.runOnUiThread(() -> {
            if (onCommitVerifyErrorListener != null) {
                onCommitVerifyErrorListener.commitVerifyError(e);
            }
        });
    }

    private OnCommitVerifySuccessListener onCommitVerifySuccessListener;

    // 接口OnCommitVerifySuccessListener
    public interface OnCommitVerifySuccessListener {
        void commitVerifySuccess();
    }

    // 对外方式setOnCommitVerifySuccessListener
    public void setOnCommitVerifySuccessListener(OnCommitVerifySuccessListener onCommitVerifySuccessListener) {
        this.onCommitVerifySuccessListener = onCommitVerifySuccessListener;
    }

    // 封装方法commitVerifySuccessNext
    private void commitVerifySuccessNext() {
        activity.runOnUiThread(() -> {
            if (onCommitVerifySuccessListener != null) {
                onCommitVerifySuccessListener.commitVerifySuccess();
            }
        });
    }
}
