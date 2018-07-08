package xsda.xsda.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.callback.AVServerDateCallback;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/8 0008.
 */

public class GetVerifyCodeHelper {

    public void get(String phoneNum, String password) {
        // TODO: 2018/7/8 0008 从服务器获取时间 
        AVOSCloud.getServerDateInBackground(new AVServerDateCallback() {
            @Override
            public void done(Date date, AVException e) {
                if (e == null) {
                    // TODO: 2018/7/8 0008  得到时间存到sp中
                    AVUser user = new AVUser();
                    user.setUsername(phoneNum);
                    user.setPassword(password);
                    // 其他属性可以像其他AVObject对象一样使用put方法添加
                    user.put("mobilePhoneNumber", phoneNum);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(AVException e) {
                            if (e == null) {
                                // successfully
                                // TODO: 2018/7/8 0008  获取成功
                            } else {
                                // failed
                                // TODO: 2018/7/8 0008 获取失败 
                            }
                        }
                    });
                } else {
                    // TODO: 2018/7/8 0008 获取时间出错 
                }
            }
        });
       
    }
}
