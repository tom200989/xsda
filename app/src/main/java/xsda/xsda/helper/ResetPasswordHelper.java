package xsda.xsda.helper;
/*
 * Created by qianli.ma on 2018/7/31 0031.
 */

import android.app.Activity;
import android.os.Handler;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;

import xsda.xsda.app.XsdaApplication;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Sgg;

public class ResetPasswordHelper {
    private Activity activity;

    public ResetPasswordHelper(Activity activity) {
        this.activity = activity;
    }
    
    /* -------------------------------------------- A.申请重置密码的验证码 -------------------------------------------- */

    /**
     * 获取重置密码时的验证码
     *
     * @param phoneNum 手机号码
     */
    public void getResetVerify(String phoneNum) {
        /* 验证用户是否存在 */
        Lgg.t(Cons.TAG).ii("getResetVerify()--> isUserExist");
        prepareNext();
        UserExistHelper userExistHelper = new UserExistHelper(activity);
        userExistHelper.setOnExceptionListener(e -> {// 出错
            activity.runOnUiThread(() -> {
                resetGetVerifyErrorNext(e);
                Egg.print(getClass().getSimpleName(), ":userExistHelper", e, null);
                afterNext();
            });
        });
        userExistHelper.setOnUserNotExistListener(() -> {// 用户不存在
            activity.runOnUiThread(() -> {
                userHadNotExistNext();
                afterNext();
            });
        });
        userExistHelper.setOnUserHadExistListener(() -> getServerDateLong(phoneNum));// 用户存在--> 符合条件
        userExistHelper.isExist(phoneNum);
    }

    /**
     * A1.请求重试获取验证码接口
     *
     * @param phoneNum 手机号
     */
    private void getServerDateLong(String phoneNum) {
        Lgg.t(Cons.TAG).ii("getServerDateLong()");
        GetServerDateHelper getServerDateHelper = new GetServerDateHelper();
        getServerDateHelper.setOnGetServerErrorListener(e -> {
            activity.runOnUiThread(() -> {
                Egg.print(getClass().getSimpleName(), ":getServerDateLong", e, null);
                resetGetVerifyErrorNext(e);
                afterNext();
            });
        });
        getServerDateHelper.setOnGetServerDateLongSuccessListener(serverTime -> requestPasswordResetBySmsCode(phoneNum, serverTime));
        getServerDateHelper.get();
    }

    /**
     * A2.请求「申请重置」验证码接口
     *
     * @param phoneNum   号码
     * @param serverTime 服务器时间
     */
    private void requestPasswordResetBySmsCode(String phoneNum, long serverTime) {
        Lgg.t(Cons.TAG).ii("requestPasswordResetBySmsCode()");
        AVUser.requestPasswordResetBySmsCodeInBackground(phoneNum, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                activity.runOnUiThread(() -> {
                    if (e == null) {
                        // 得到服务器时间存到sp中
                        Lgg.t(Cons.TAG).ii("requestPasswordResetBySmsCode() success");
                        Sgg.getInstance(XsdaApplication.getApp()).putLong(Cons.SP_RESET_SERVER_DATE, serverTime);
                        requestPasswordResetSuccessNext(serverTime);
                        afterNext();
                    } else {
                        Egg.print(getClass().getSimpleName(), ":requestPasswordResetBySmsCode", e, null);
                        resetGetVerifyErrorNext(e);
                        afterNext();
                    }
                });
            }
        });
    }
    
    /* -------------------------------------------- B.提交重置密码的验证码 -------------------------------------------- */

    public void commitResetPasswordVerify(String verifyCode, String password) {
        Lgg.t(Cons.TAG).ii("commitResetPasswordVerify()");
        prepareNext();
        new Handler().postDelayed(() -> {
            AVUser.resetPasswordBySmsCodeInBackground(verifyCode, password, new UpdatePasswordCallback() {
                @Override
                public void done(AVException e) {
                    activity.runOnUiThread(() -> {
                        if (e == null) {
                            Lgg.t(Cons.TAG).ii("commitResetPasswordVerify() success");
                            commitResetVerifySuccessNext();
                            afterNext();
                        } else {
                            Egg.print(getClass().getSimpleName(), ":commitResetPasswordVerify", e, null);
                            resetGetVerifyErrorNext(e);
                            afterNext();
                        }
                    });
                }
            });
        }, 2000);
    }
    
    
    /* -------------------------------------------- impl -------------------------------------------- */

    private OnPrepareListener onPrepareListener;

    // 接口OnPrepareListener
    public interface OnPrepareListener {
        void perpare();
    }

    // 对外方式setOnPrepareListener
    public void setOnPrepareListener(OnPrepareListener onPrepareListener) {
        this.onPrepareListener = onPrepareListener;
    }

    // 封装方法perpareNext
    private void prepareNext() {
        if (onPrepareListener != null) {
            onPrepareListener.perpare();
        }
    }

    private OnAfterListener onAfterListener;

    // 接口OnAfterListener
    public interface OnAfterListener {
        void after();
    }

    // 对外方式setOnAfterListener
    public void setOnAfterListener(OnAfterListener onAfterListener) {
        this.onAfterListener = onAfterListener;
    }

    // 封装方法afterNext
    private void afterNext() {
        if (onAfterListener != null) {
            onAfterListener.after();
        }
    }

    private OnCommitResetVerifySuccessListener onCommitResetVerifySuccessListener;

    // 接口OnCommitResetVerifySuccessListener
    public interface OnCommitResetVerifySuccessListener {
        void commitResetVerifySuccess();
    }

    // 对外方式setOnCommitResetVerifySuccessListener
    public void setOnCommitResetVerifySuccessListener(OnCommitResetVerifySuccessListener onCommitResetVerifySuccessListener) {
        this.onCommitResetVerifySuccessListener = onCommitResetVerifySuccessListener;
    }

    // 封装方法commitResetVerifySuccessNext
    private void commitResetVerifySuccessNext() {
        if (onCommitResetVerifySuccessListener != null) {
            onCommitResetVerifySuccessListener.commitResetVerifySuccess();
        }
    }

    private OnRequestPasswordResetSuccessListener onRequestPasswordResetSuccessListener;

    // 接口OnRequestPasswordResetListener
    public interface OnRequestPasswordResetSuccessListener {
        void requestPasswordReset(long serverTime);
    }

    // 对外方式setOnRequestPasswordResetListener
    public void setOnRequestPasswordResetSuccessListener(OnRequestPasswordResetSuccessListener onRequestPasswordResetSuccessListener) {
        this.onRequestPasswordResetSuccessListener = onRequestPasswordResetSuccessListener;
    }

    // 封装方法requestPasswordResetNext
    private void requestPasswordResetSuccessNext(long serverTime) {
        if (onRequestPasswordResetSuccessListener != null) {
            onRequestPasswordResetSuccessListener.requestPasswordReset(serverTime);
        }
    }

    private OnUserHadNotExistListener onUserHadNotExistListener;

    // 接口OnUserHadNotExistListener
    public interface OnUserHadNotExistListener {
        void userHadNotExist();
    }

    // 对外方式setOnUserHadNotExistListener
    public void setOnUserHadNotExistListener(OnUserHadNotExistListener onUserHadNotExistListener) {
        this.onUserHadNotExistListener = onUserHadNotExistListener;
    }

    // 封装方法userHadNotExistNext
    private void userHadNotExistNext() {
        if (onUserHadNotExistListener != null) {
            onUserHadNotExistListener.userHadNotExist();
        }
    }

    private OnResetGetVerifyErrorListener onResetGetVerifyErrorListener;

    // 接口OnResetGetVerifyErrorListener
    public interface OnResetGetVerifyErrorListener {
        void resetGetVerifyError(AVException err);
    }

    // 对外方式setOnResetGetVerifyErrorListener
    public void setOnResetGetVerifyErrorListener(OnResetGetVerifyErrorListener onResetGetVerifyErrorListener) {
        this.onResetGetVerifyErrorListener = onResetGetVerifyErrorListener;
    }

    // 封装方法resetGetVerifyErrorNext
    private void resetGetVerifyErrorNext(AVException err) {
        if (onResetGetVerifyErrorListener != null) {
            onResetGetVerifyErrorListener.resetGetVerifyError(err);
        }
    }

}
