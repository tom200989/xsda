package xsda.xsda.utils;

/**
 * Created by qianli.ma on 2018/7/17 0017.
 */

public class Avfield {

    /**
     * 用户
     */
    public static class User {
        public static String classname = "_User";
        public static String objectId = "objectId";
        public static String ACL = "ACL";
        public static String createdAt = "createdAt";
        public static String updatedAt = "updatedAt";
        public static String salt = "salt";
        public static String email = "email";
        public static String session_token = "session_token";
        public static String password = "password";
        public static String username = "username";
        public static String nickname = "nickname";
        public static String headurl = "headurl";
        public static String emailVerified = "emailVerified";
        public static String mobilePhoneNumber = "mobilePhoneNumber";
        public static String authData = "authData";
        public static String mobilePhoneVerified = "mobilePhoneVerified";
    }

    /**
     * 更新
     */
    public static class update {
        public static String classname = "update";
        public static String objectId = "objectId";
        public static String ACL = "ACL";
        public static String createdAt = "createdAt";
        public static String updatedAt = "updatedAt";
        public static String newVersionFix = "newVersionFix";
        public static String newVersionFile = "newVersionFile";
        public static String newVersionSize = "newVersionSize";
        public static String newVersionCode = "newVersionCode";
    }

    /**
     * 用户验证码
     */
    public static class UserVerify {
        public static String classname = "UserVerify";
        public static String objectId = "objectId";
        public static String ACL = "ACL";
        public static String createdAt = "createdAt";
        public static String updatedAt = "updatedAt";
        public static String username = "username";
        public static String isPhoneVerify = "isPhoneVerify";
        public static String openid = "openid";
    }

    /**
     * 登陆状态
     */
    public static class LoginStatus {
        public static String classname = "LoginStatus";
        public static String objectId = "objectId";
        public static String ACL = "ACL";
        public static String createdAt = "createdAt";
        public static String updatedAt = "updatedAt";
        public static String deviceId = "deviceId";
        public static String phoneNum = "phoneNum";
        public static String state = "state";
    }
}
