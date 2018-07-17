package xsda.xsda.utils;

/**
 * Created by qianli.ma on 2018/7/17 0017.
 */

public class Avfield {

    public static class User {
        public static String classname = "_User";
        public static String objectId = "objectId";
        public static String salt = "salt";
        public static String email = "email";
        public static String session_token = "session_token";
        public static String ACL = "ACL";
        public static String password = "password";
        public static String username = "username";
        public static String emailVerified = "emailVerified";
        public static String mobilePhoneNumber = "mobilePhoneNumber";
        public static String authData = "authData";
        public static String mobilePhoneVerified = "mobilePhoneVerified";
        public static String createdAt = "createdAt";
        public static String updatedAt = "updatedAt";
    }

    public static class update {
        public static String classname = "update";
        public static String objectId = "objectId";
        public static String newVersionFix = "newVersionFix";
        public static String ACL = "ACL";
        public static String newVersionFile = "newVersionFile";
        public static String newVersionSize = "newVersionSize";
        public static String newVersionCode = "newVersionCode";
        public static String createdAt = "createdAt";
        public static String updatedAt = "updatedAt";
    }

    public static class UserVerify {
        public static String classname = "UserVerify";
        public static String objectId = "objectId";
        public static String ACL = "ACL";
        public static String username = "username";
        public static String isPhoneVerify = "isPhoneVerify";
        public static String createdAt = "createdAt";
        public static String updatedAt = "updatedAt";

    }
}
