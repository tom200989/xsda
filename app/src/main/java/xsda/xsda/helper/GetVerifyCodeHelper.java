package xsda.xsda.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;

import xsda.xsda.ui.XsdaApplication;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Sgg;

/**
 * Created by Administrator on 2018/7/8 0008.
 */

public class GetVerifyCodeHelper {

    /**
     * 发起验证请求
     *
     * @param phoneNum 电话
     * @param password 密码
     */
    public void get(String phoneNum, String password) {

        // 1.从服务器获取时间 
        GetServerDateHelper getServerDateHelper = new GetServerDateHelper();
        getServerDateHelper.setOnGetServerErrorListener(this::getServerDateErrorNext);
        getServerDateHelper.setOnGetServerDateLongSuccessListener(millute -> {
            // 2.创建用户并调起注册请求
            createUserAndRequestVerify(phoneNum, password, millute);
        });
        getServerDateHelper.get();
    }

    /**
     * 创建用户并调起注册请求
     *
     * @param phoneNum 手机号
     * @param password 密码
     * @param millute  服务器时间
     */
    private void createUserAndRequestVerify(String phoneNum, String password, long millute) {
        AVUser user = new AVUser();
        user.setUsername(phoneNum);
        user.setPassword(password);
        // 3.其他属性可以像其他AVObject对象一样使用put方法添加
        user.put("mobilePhoneNumber", phoneNum);
        user.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                Egg.print(e);
                if (e != null) {
                    // 3.1.如果号码已经存在
                    if (e.getCode() == Egg.MOBILE_PHONE_NUMBER_HAS_ALREADY_BEEN_TAKEN) {
                        // 3.2.直接请求验证码
                        directToGetVerifyCode(phoneNum, millute);
                    } else {
                        // 4.验证失败
                        verifyErrorNext(e);
                    }
                    return;
                }
                // 5.验证成功
                verifySuccess(millute);
            }
        });
    }

    /**
     * 直接调用请求验证码接口
     *
     * @param phoneNum 手机号码
     * @param millute  服务器时间
     */
    private void directToGetVerifyCode(String phoneNum, long millute) {
        AVUser.requestMobilePhoneVerifyInBackground(phoneNum, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                Egg.print(e);
                if (e != null) {
                    // 4.验证失败
                    verifyErrorNext(e);
                    return;
                }
                // 5.验证成功
                verifySuccess(millute);
            }
        });
    }

    /**
     * 验证成功
     *
     * @param millute 服务器时间
     */
    private void verifySuccess(long millute) {
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
        if (onGetServerDateErrorListener != null) {
            onGetServerDateErrorListener.getServerDateError(e);
        }
    }

    private OnVerifyErrorListener onVerifyErrorListener;

    // 接口OnVerifyErrorListener
    public interface OnVerifyErrorListener {
        void verifyError(AVException e);
    }

    // 对外方式setOnVerifyErrorListener
    public void setOnVerifyErrorListener(OnVerifyErrorListener onVerifyErrorListener) {
        this.onVerifyErrorListener = onVerifyErrorListener;
    }

    // 封装方法verifyErrorNext
    private void verifyErrorNext(AVException e) {
        if (onVerifyErrorListener != null) {
            onVerifyErrorListener.verifyError(e);
        }
    }

    private OnVerifySuccessListener onVerifySuccessListener;

    // 接口OnVerifySuccessListener
    public interface OnVerifySuccessListener {
        void verifySuccess();
    }

    // 对外方式setOnVerifySuccessListener
    public void setOnVerifySuccessListener(OnVerifySuccessListener onVerifySuccessListener) {
        this.onVerifySuccessListener = onVerifySuccessListener;
    }

    // 封装方法verifySuccessNext
    private void verifySuccessNext() {
        if (onVerifySuccessListener != null) {
            onVerifySuccessListener.verifySuccess();
        }
    }
}
