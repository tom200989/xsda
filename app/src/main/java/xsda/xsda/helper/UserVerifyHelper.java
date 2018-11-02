package xsda.xsda.helper;

import android.app.Activity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

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
