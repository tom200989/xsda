package xsda.xsda.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

import xsda.xsda.ui.XsdaApplication;
import xsda.xsda.utils.Cons;
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
            // 2.调起注册请求
            AVUser user = new AVUser();
            user.setUsername(phoneNum);
            user.setPassword(password);
            // 3.其他属性可以像其他AVObject对象一样使用put方法添加
            user.put("mobilePhoneNumber", phoneNum);
            user.signUpInBackground(new SignUpCallback() {
                public void done(AVException e) {
                    if (e != null) {
                        // 4.验证失败
                        verifyErrorNext(e);
                        return;
                    }
                    // 4.得到时间存到sp中
                    Sgg.getInstance(XsdaApplication.getApp()).putLong(Cons.SP_SERVER_DATE, millute);
                    // 5.验证成功
                    verifySuccessNext();
                }
            });
        });
        getServerDateHelper.get();
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
        void verifyError(Exception e);
    }

    // 对外方式setOnVerifyErrorListener
    public void setOnVerifyErrorListener(OnVerifyErrorListener onVerifyErrorListener) {
        this.onVerifyErrorListener = onVerifyErrorListener;
    }

    // 封装方法verifyErrorNext
    private void verifyErrorNext(Exception e) {
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
