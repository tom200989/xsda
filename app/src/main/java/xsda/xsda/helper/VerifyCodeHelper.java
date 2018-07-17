package xsda.xsda.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

import java.util.List;

import xsda.xsda.ui.XsdaApplication;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Sgg;

public class VerifyCodeHelper {

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
            // 1.2.判断用户是否存在
            isUserExist(phoneNum, password, millute);
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
        AVQuery<AVObject> query = new AVQuery<>("UserVerify");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                Egg.print(getClass().getSimpleName(), "isUserExist", e, null);
                if (list == null) {
                    // 2.1.创建用户并调起注册请求
                    Lgg.t(Cons.TAG).ii("isUserExist()--> list == null");
                    createUserAndRequestVerify(username, password, millute);
                    return;
                }
                if (list.size() <= 0) {
                    Lgg.t(Cons.TAG).ii("isUserExist()--> list.size() <= 0");
                    // 2.2.创建用户并调起注册请求
                    createUserAndRequestVerify(username, password, millute);
                    return;
                }

                // 2.3.判断同名
                boolean isUserExist = false;
                for (AVObject avObject : list) {
                    String username_db = avObject.getString("username");
                    boolean isPhoneVerify = avObject.getBoolean("isPhoneVerify");
                    Lgg.t(Cons.TAG).ii("username_db: " + username_db + ";isPhoneVerify: " + isPhoneVerify);
                    if (username.equals(username_db)) {
                        Lgg.t(Cons.TAG).ii("isUserExist()--> isUserExist = true");
                        isUserExist = true;
                        break;
                    }
                }

                // 2.4.回调用户存在
                if (isUserExist) {
                    Lgg.t(Cons.TAG).ii("userHadExistNext()");
                    userHadExistNext();
                } else {
                    Lgg.t(Cons.TAG).ii("to createUserAndRequestVerify()");
                    createUserAndRequestVerify(username, password, millute);
                }

            }
        });
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
        user.put("mobilePhoneNumber", phoneNum);
        user.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                Egg.print(getClass().getSimpleName(), "createUserAndRequestVerify", e, null);
                if (e != null) {
                    // 2.4.如果号码已经存在
                    if (e.getCode() == Egg.MOBILE_PHONE_NUMBER_HAS_ALREADY_BEEN_TAKEN) {
                        // 2.5.直接申请验证码
                        directToGetVerifyCode(phoneNum, millute);
                    } else {
                        // 2.6.验证失败
                        verifyErrorNext(e);
                    }
                    return;
                }
                // 2.7.验证成功
                verifySuccess(millute);
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
                    // 3.1.验证失败
                    verifyErrorNext(e);
                    return;
                }
                // 3.2.申请验证码成功
                verifySuccess(millute);
            }
        });
    }

    /**
     * B1.提交验证码
     *
     * @param username   用户名(手机号)
     * @param verifyCode 验证码
     */
    public void commitVerifyCode(String username, String verifyCode) {
        Lgg.t(Cons.TAG).ii("commitVerifyCode");
        // 先查询用户是否存在
        AVQuery<AVObject> query = new AVQuery<>("UserVerify");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                Egg.print(getClass().getSimpleName(), "isUserExist", e, null);
                if (list == null) {
                    // 2.1.提交验证码
                    Lgg.t(Cons.TAG).ii("isUserExist()--> list == null");
                    verifyMobilePhone(username, verifyCode);
                    return;
                }
                if (list.size() <= 0) {
                    Lgg.t(Cons.TAG).ii("isUserExist()--> list.size() <= 0");
                    // 2.2.提交验证码
                    verifyMobilePhone(username, verifyCode);
                    return;
                }

                // 2.3.判断同名
                boolean isUserExist = false;
                for (AVObject avObject : list) {
                    String username_db = avObject.getString("username");
                    boolean isPhoneVerify = avObject.getBoolean("isPhoneVerify");
                    Lgg.t(Cons.TAG).ii("username_db: " + username_db + ";isPhoneVerify: " + isPhoneVerify);
                    if (username.equals(username_db)) {
                        Lgg.t(Cons.TAG).ii("isUserExist()--> isUserExist = true");
                        isUserExist = true;
                        break;
                    }
                }

                // 2.4.回调用户存在
                if (isUserExist) {
                    Lgg.t(Cons.TAG).ii("userHadExistNext()");
                    userHadExistNext();
                } else {
                    Lgg.t(Cons.TAG).ii("to createUserAndRequestVerify()");
                    verifyMobilePhone(username, verifyCode);
                }

            }
        });

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
                Egg.print(getClass().getSimpleName(), "commitVerifyCode", e, "校验验证码失败");
                if (e != null) {
                    commitVerifyErrorNext(e);
                } else {
                    // 1.2.成功后提交验证成功信息
                    putVerifyUserInfo(username, true);
                }
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
        AVObject userVerify = new AVObject("UserVerify");
        // 2.2.用户名(手机号)
        userVerify.put("username", username);
        // 2.3.是否已经验证
        userVerify.put("isPhoneVerify", isVerify);
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
    public void setOnGetVerifyErrorListener(OnVerifyErrorListener onVerifyErrorListener) {
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
    public void setOnGetVerifySuccessListener(OnVerifySuccessListener onVerifySuccessListener) {
        this.onVerifySuccessListener = onVerifySuccessListener;
    }

    // 封装方法verifySuccessNext
    private void verifySuccessNext() {
        if (onVerifySuccessListener != null) {
            onVerifySuccessListener.verifySuccess();
        }
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
        if (onCommitVerifyErrorListener != null) {
            onCommitVerifyErrorListener.commitVerifyError(e);
        }
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
        if (onCommitVerifySuccessListener != null) {
            onCommitVerifySuccessListener.commitVerifySuccess();
        }
    }
}
