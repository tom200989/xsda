package xsda.xsda.helper;
/*
 * Created by qianli.ma on 2018/7/31 0031.
 */

import android.app.Activity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

public class LoginHelper {

    private Activity activity;

    public LoginHelper(Activity activity) {
        this.activity = activity;
    }

    public void login(String phoneNum, String password) {
        // 登陆存储
        AVUser.loginByMobilePhoneNumberInBackground(phoneNum, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                // TODO: 2018/8/1 0001  
                if (e == null) {
                    // 登陆client
                } else {
                    
                }
            }
        });
    }
}
