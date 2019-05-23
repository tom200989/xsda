package xsda.xsda.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.request.UriRequest;
import org.xutils.x;

import java.util.List;

import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;

/*
 * Created by qianli.ma on 2018/9/30 0030.
 */
public class WXHelper {

    private IWXAPI api;
    private String TAG = "WXHelper";
    private Activity activity;

    public WXHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * 初始化微信
     *
     * @param handler 微信回调
     * @return 微信API
     */
    public IWXAPI initWXAPI(IWXAPIEventHandler handler) {
        // 1.初始化微信API
        Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":initWXAPI()");
        String wxAppId = Cons.WX_APP_ID;
        api = WXAPIFactory.createWXAPI(activity, wxAppId, true);
        api.registerApp(wxAppId);
        api.handleIntent(activity.getIntent(), handler);
        // 2.立即请求
        final SendAuth.Req req = new SendAuth.Req();
        req.transaction = wxAppId;
        req.scope = "snsapi_userinfo";
        req.state = TAG;
        api.sendReq(req);
        return api;
    }

    /**
     * 处理WX回调
     *
     * @param baseResp 响应
     */
    public void handlerResp(BaseResp baseResp) {
        // 3.得到code
        Lgg.t(TAG).ii("WX had responce");
        SendAuth.Resp res = (SendAuth.Resp) baseResp;
        int errCode = res.errCode;
        String code = res.code;
        String state = res.state;
        String lang = res.lang;
        String country = res.country;
        String sendAuth = "error: " + errCode + "\ncode: " + code + "\nstate: " + state + "\nlang: " + lang + "\ncountry: " + country;
        Lgg.t(TAG).ii(sendAuth);

        switch (errCode) {
            case BaseResp.ErrCode.ERR_OK:
                // 4.使用code进行请求获取access token
                getAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                errUserCancelNext();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                errAuthDeniedNext();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                errUnSupportNext();
                break;
            default:
                errAuthNoEffectNext();
                break;
        }
    }

    /**
     * 使用code进行请求获取access token
     *
     * @param code code
     */
    private void getAccessToken(String code) {
        String wxAppId = Cons.WX_APP_ID;
        String wxAppSecret = Cons.WX_APP_SECRECT;
        // 格式: https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" // 基本网址
                             + "appid=" + wxAppId // appid
                             + "&secret=" + wxAppSecret // appsecret
                             + "&code=" + code // code
                             + "&grant_type=authorization_code";// grant_type
        RequestParams rp = new RequestParams(url);
        x.http().get(rp, new Callback.CommonCallback<String>() {
            @Override
            public void responseBody(UriRequest uriRequest) {
                
            }

            @Override
            public void onSuccess(String tokenInfo) {
                Lgg.t(TAG).ii(tokenInfo);
                if (tokenInfo.contains("errcode")) {
                    errUserErrorNext(null);
                } else {
                    // 5.转换json
                    WechatToken wechatToken = JSONObject.parseObject(tokenInfo, WechatToken.class);
                    String accessToken = wechatToken.getAccess_token();
                    String openid = wechatToken.getOpenid();
                    // 6.获取用户信息
                    getUserInfo(accessToken, openid);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                errUserErrorNext(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                errUserCancelNext();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param accessToken token(7200秒)
     * @param openid      openId
     */
    private void getUserInfo(String accessToken, String openid) {
        // 格式: https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
        String url = "https://api.weixin.qq.com/sns/userinfo?"// url
                             + "access_token=" + accessToken // token
                             + "&openid=" + openid;// openid
        RequestParams rp = new RequestParams(url);
        x.http().get(rp, new Callback.CommonCallback<String>() {
            @Override
            public void responseBody(UriRequest uriRequest) {
                
            }

            @Override
            public void onSuccess(String userInfo) {
                activity.runOnUiThread(() -> getWeChatInfoSuccessNext(userInfo));
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                activity.runOnUiThread(() -> errUserErrorNext(ex));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                activity.runOnUiThread(() -> errUserCancelNext());
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 判断微信是否已经安装
     *
     * @param context 上下文
     * @return true: had install
     */
    public static boolean isWXInstall(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, Cons.WX_APP_ID);
        if (api.isWXAppInstalled()) {
            return true;
        } else {
            final PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if (pn.equalsIgnoreCase("com.tencent.mm")) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /* -------------------------------------------- impl -------------------------------------------- */

    private OnGetWeChatInfoSuccessListener onGetWeChatInfoSuccessListener;

    // Inteerface--> 接口OnGetWeChatInfoSuccessListener
    public interface OnGetWeChatInfoSuccessListener {
        void getWeChatInfoSuccess(String userInfo);
    }

    // 对外方式setOnGetWeChatInfoSuccessListener
    public void setOnGetWeChatInfoSuccessListener(OnGetWeChatInfoSuccessListener onGetWeChatInfoSuccessListener) {
        this.onGetWeChatInfoSuccessListener = onGetWeChatInfoSuccessListener;
    }

    // 封装方法getWeChatInfoSuccessNext
    private void getWeChatInfoSuccessNext(String userInfo) {
        if (onGetWeChatInfoSuccessListener != null) {
            onGetWeChatInfoSuccessListener.getWeChatInfoSuccess(userInfo);
        }
    }

    private OnErrUserErrorListener onErrUserErrorListener;

    // Inteerface--> 接口OnErrUserErrorListener
    public interface OnErrUserErrorListener {
        void errUserError(Throwable ex);
    }

    // 对外方式setOnErrUserErrorListener
    public void setOnErrUserErrorListener(OnErrUserErrorListener onErrUserErrorListener) {
        this.onErrUserErrorListener = onErrUserErrorListener;
    }

    // 封装方法errUserErrorNext
    private void errUserErrorNext(Throwable ex) {
        if (onErrUserErrorListener != null) {
            onErrUserErrorListener.errUserError(ex);
        }
    }

    private OnErrUserCancelListener onErrUserCancelListener;

    // Inteerface--> 接口OnErrUserCancelListener
    public interface OnErrUserCancelListener {
        void errUserCancel();
    }

    // 对外方式setOnErrUserCancelListener
    public void setOnErrUserCancelListener(OnErrUserCancelListener onErrUserCancelListener) {
        this.onErrUserCancelListener = onErrUserCancelListener;
    }

    // 封装方法errUserCancelNext
    private void errUserCancelNext() {
        if (onErrUserCancelListener != null) {
            onErrUserCancelListener.errUserCancel();
        }
    }

    private OnErrAuthDeniedListener onErrAuthDeniedListener;

    // Inteerface--> 接口OnErrAuthDeniedListener
    public interface OnErrAuthDeniedListener {
        void errAuthDenied();
    }

    // 对外方式setOnErrAuthDeniedListener
    public void setOnErrAuthDeniedListener(OnErrAuthDeniedListener onErrAuthDeniedListener) {
        this.onErrAuthDeniedListener = onErrAuthDeniedListener;
    }

    // 封装方法errAuthDeniedNext
    private void errAuthDeniedNext() {
        if (onErrAuthDeniedListener != null) {
            onErrAuthDeniedListener.errAuthDenied();
        }
    }

    private OnErrUnsupportListener onErrUnsupportListener;

    // Inteerface--> 接口OnErrUnsupportListener
    public interface OnErrUnsupportListener {
        void errUnSupport();
    }

    // 对外方式setOnErrUnsupportListener
    public void setOnErrUnsupportListener(OnErrUnsupportListener onErrUnsupportListener) {
        this.onErrUnsupportListener = onErrUnsupportListener;
    }

    // 封装方法errSupportNext
    private void errUnSupportNext() {
        if (onErrUnsupportListener != null) {
            onErrUnsupportListener.errUnSupport();
        }
    }

    private OnErrAuthNoEffectListener onErrAuthNoEffectListener;

    // Inteerface--> 接口OnErrAuthNoEffectListener
    public interface OnErrAuthNoEffectListener {
        void errAuthNoEffect();
    }

    // 对外方式setOnErrAuthNoEffectListener
    public void setOnErrAuthNoEffectListener(OnErrAuthNoEffectListener onErrAuthNoEffectListener) {
        this.onErrAuthNoEffectListener = onErrAuthNoEffectListener;
    }

    // 封装方法errAuthNoEffectNext
    private void errAuthNoEffectNext() {
        if (onErrAuthNoEffectListener != null) {
            onErrAuthNoEffectListener.errAuthNoEffect();
        }
    }
}
