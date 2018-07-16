package xsda.xsda.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;

/**
 * Created by qianli.ma on 2018/7/16 0016.
 */

public class Egg {

    // 手机号码已存在
    public static final long MOBILE_PHONE_NUMBER_HAS_ALREADY_BEEN_TAKEN = 214;
    // 发送验证码过快
    public static final long CANT_SEND_SMS_TOO_FREQUENTLY = 601;
    // 无效的验证码
    public static final long INVALID_SMS_CODE = 603;
    


    public static void print(String className, String methodName, AVException e, @Nullable String other) {
        if (e != null) {
            int errorCode = e.getCode();
            String cause = e.getMessage();
            Lgg.t(Cons.TAG).ee(className + ":" + methodName + "()--> error code: " + errorCode + ";cause: " + cause);
            if (!TextUtils.isEmpty(other)) {
                Lgg.t(Cons.TAG).ee(className + ":" + methodName + "()-->" + other);
            }
        }
    }
}
