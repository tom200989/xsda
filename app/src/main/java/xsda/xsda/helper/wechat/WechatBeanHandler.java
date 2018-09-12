package xsda.xsda.helper.wechat;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformDb;

/*
 * Created by qianli.ma on 2018/9/7 0007.
 */
public class WechatBeanHandler {
    /**
     * 封装
     *
     * @param userInfo 属性Map集合
     * @param platform 平台对象
     */
    public static WechatBean handle(@Nullable HashMap<String, Object> userInfo, Platform platform) {
        // 1.初始化
        WechatBean wechatBean = new WechatBean();
        PlatformDb platformDb = platform.getDb();
        // 2.封装平台信息
        wechatBean.setUserIcon(platformDb.getUserIcon());
        wechatBean.setUserName(platformDb.getUserName());
        wechatBean.setUserId(platformDb.getUserId());
        wechatBean.setToken(platformDb.getToken());
        wechatBean.setUserGender(platformDb.getUserGender());
        // 3.封装个人信息
        if (userInfo != null) {
            for (Object info : userInfo.entrySet()) {
                Map.Entry entry = (Map.Entry) info;
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                if ("sex".equalsIgnoreCase(key)) {
                    wechatBean.setSex(value);
                } else if ("nickname".equalsIgnoreCase(key)) {
                    wechatBean.setNickname(value);
                } else if ("province".equalsIgnoreCase(key)) {
                    wechatBean.setProvince(value);
                } else if ("language".equalsIgnoreCase(key)) {
                    wechatBean.setLanguage(value);
                } else if ("headimgurl".equalsIgnoreCase(key)) {
                    wechatBean.setHeadimgurl(value);
                } else if ("city".equalsIgnoreCase(key)) {
                    wechatBean.setCity(value);
                } else if ("country".equalsIgnoreCase(key)) {
                    wechatBean.setCountry(value);
                }
            }
        }

        return wechatBean;
    }
}
