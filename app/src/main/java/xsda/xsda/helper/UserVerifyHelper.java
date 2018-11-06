package xsda.xsda.helper;

import android.app.Activity;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.hiber.tools.RootEncrypt;

import java.util.List;

import xsda.xsda.utils.Avfield;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;

/*
 * Created by qianli.ma on 2018/11/2 0002.
 */
public class UserVerifyHelper {

    private Activity activity;
    private String TAG = "UserVerifyHelper";

    public UserVerifyHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 保存openid
     *
     * @param phonenum 手机号
     * @param openId   微信的openid
     */
    public void saveOpenId(String phonenum, String openId) {
        AVQuery<AVObject> query = new AVQuery<>(Avfield.UserVerify.classname);
        query.whereEqualTo(Avfield.UserVerify.username, phonenum);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                activity.runOnUiThread(() -> {
                    Egg.print(getClass().getSimpleName(), "isExist", e, null);
                    if (e == null) {
                        if (list == null) {// 用户不存在
                            Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":" + "isExist()--> list == null");
                            userNotExistNext();
                            return;
                        }
                        if (list.size() <= 0) {// 用户不存在
                            Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":" + "isExist()--> list.size() <= 0");
                            userNotExistNext();
                            return;
                        }

                        // 判断同名
                        AVObject targetO = null;
                        for (AVObject avObject : list) {
                            String username_db = avObject.getString(Avfield.UserVerify.username);
                            boolean isPhoneVerify = avObject.getBoolean(Avfield.UserVerify.isPhoneVerify);
                            Lgg.t(Cons.TAG).ii("username_db: " + username_db + ";isPhoneVerify: " + isPhoneVerify);
                            if (phonenum.equals(username_db)) {
                                Lgg.t(Cons.TAG).ii("isUserExist()--> isUserExist = true");
                                targetO = avObject;
                                break;
                            }
                        }

                        // 回调用户存在
                        if (targetO != null) {
                            // 用户存在
                            Lgg.t(Cons.TAG).ii("userHadExistNext()");
                            targetO.put(Avfield.UserVerify.openid, openId);
                            targetO.saveInBackground();
                        } else {
                            // 用户不存在
                            Lgg.t(Cons.TAG).ii("userNotExistNext()");
                            userNotExistNext();
                        }
                    } else {
                        // 出错
                        exceptionNext(e);
                    }
                    getUserExistAfterNext();
                });
            }
        });
    }

    /**
     * 验证当前的Openid是否存在
     *
     * @param currentId 当前获取到的ID
     */
    public void isOpenidExist(String currentId) {
        Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":isOpenidExist() : current openid : " + currentId);
        // 根据当前的openid查询对应用户
        AVQuery<AVObject> query = new AVQuery<>(Avfield.UserVerify.classname);
        query.whereEqualTo(Avfield.UserVerify.openid, currentId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {

                    // 没有搜索到
                    if (list == null || list.size() <= 0) {
                        Lgg.t(TAG).ii(":isOpenidExist(): openidNotExist");
                        openidNotExistNext();
                        return;
                    }

                    AVObject avObject = list.get(0);
                    String openid = avObject.getString(Avfield.UserVerify.openid);
                    String phoneNum = avObject.getString(Avfield.UserVerify.username);
                    String password = RootEncrypt.des_descrypt(avObject.getString(Avfield.UserVerify.password));

                    // openid为空
                    if (TextUtils.isEmpty(openid)) {
                        Lgg.t(TAG).ii(":isOpenidExist(): openidNotExist--> openid empty");
                        openidNotExistNext();
                        return;
                    }

                    // 判断是否存在
                    if (openid.contains(currentId)) {
                        Lgg.t(TAG).ii(":isOpenidExist(): openid.contains(currentId)");
                        openidExistNext(phoneNum, password);
                    } else {
                        Lgg.t(TAG).ii(":isOpenidExist(): openid.not contains(currentId)");
                        openidNotExistNext();
                    }

                } else {
                    Lgg.t(TAG).ee(":isOpenidExist(): openid error--> " + e.getMessage());
                    exceptionNext(e);
                }
            }
        });
    }

    /* -------------------------------------------- impl -------------------------------------------- */

    private OnOpenidNotExistListener onOpenidNotExistListener;

    // Inteerface--> 接口OnOpenidNotExistListener
    public interface OnOpenidNotExistListener {
        void openidNotExist();
    }

    // 对外方式setOnOpenidNotExistListener
    public void setOnOpenidNotExistListener(OnOpenidNotExistListener onOpenidNotExistListener) {
        this.onOpenidNotExistListener = onOpenidNotExistListener;
    }

    // 封装方法openidNotExistNext
    private void openidNotExistNext() {
        if (onOpenidNotExistListener != null) {
            onOpenidNotExistListener.openidNotExist();
        }
    }

    private OnOpenIdExistListener onOpenIdExistListener;

    // Inteerface--> 接口OnOpenIdExistListener
    public interface OnOpenIdExistListener {
        void openidExist(String phone, String password);
    }

    // 对外方式setOnOpenIdExistListener
    public void setOnOpenIdExistListener(OnOpenIdExistListener onOpenIdExistListener) {
        this.onOpenIdExistListener = onOpenIdExistListener;
    }

    // 封装方法openidExistNext
    private void openidExistNext(String phone, String password) {
        if (onOpenIdExistListener != null) {
            onOpenIdExistListener.openidExist(phone, password);
        }
    }

    private OnGetUserExistAfterListener onGetUserExistAfterListener;

    // Inteerface--> 接口OnGetUserExistAfterListener
    public interface OnGetUserExistAfterListener {
        void getUserExistAfter();
    }

    // 对外方式setOnGetUserExistAfterListener
    public void setOnGetUserExistAfterListener(OnGetUserExistAfterListener onGetUserExistAfterListener) {
        this.onGetUserExistAfterListener = onGetUserExistAfterListener;
    }

    // 封装方法getUserExistAfterNext
    private void getUserExistAfterNext() {
        if (onGetUserExistAfterListener != null) {
            onGetUserExistAfterListener.getUserExistAfter();
        }
    }

    private OnExceptionListener onExceptionListener;

    // Inteerface--> 接口OnExceptionListener
    public interface OnExceptionListener {
        void exception(Exception e);
    }

    // 对外方式setOnExceptionListener
    public void setOnExceptionListener(OnExceptionListener onExceptionListener) {
        this.onExceptionListener = onExceptionListener;
    }

    // 封装方法exceptionNext
    private void exceptionNext(Exception e) {
        if (onExceptionListener != null) {
            onExceptionListener.exception(e);
        }
    }

    private OnUserNotExistListener onUserNotExistListener;

    // Inteerface--> 接口OnUserNotExistListener
    public interface OnUserNotExistListener {
        void userNotExist();
    }

    // 对外方式setOnUserNotExistListener
    public void setOnUserNotExistListener(OnUserNotExistListener onUserNotExistListener) {
        this.onUserNotExistListener = onUserNotExistListener;
    }

    // 封装方法userNotExistNext
    private void userNotExistNext() {
        if (onUserNotExistListener != null) {
            onUserNotExistListener.userNotExist();
        }
    }
}
