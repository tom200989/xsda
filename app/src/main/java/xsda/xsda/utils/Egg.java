package xsda.xsda.utils;

import com.avos.avoscloud.AVException;

/**
 * Created by qianli.ma on 2018/7/16 0016.
 */

public class Egg {

    // 手机号码已存在
    public static final long MOBILE_PHONE_NUMBER_HAS_ALREADY_BEEN_TAKEN = 214;
    // 发送验证码过快
    public static final long CANT_SEND_SMS_TOO_FREQUENTLY = 601;


    public static void print(AVException e) {
        if (e != null) {
            int errorCode = e.getCode();
            String cause = e.getMessage();
            Lgg.t(Cons.TAG).ee("error code: " + errorCode + ";cause: " + cause);
        }
    }
}
