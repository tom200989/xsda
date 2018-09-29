package xsda.xsda.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import xsda.xsda.R;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Tgg;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private String TAG = "XSDA_WXEntryActivity";
    private int errCode = -1000;// 默认-1000为首次进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx);
        // 1.初始化微信API
        api = WXAPIFactory.createWXAPI(this, "wxfb11cdec3869a117", true);
        api.registerApp("wxfb11cdec3869a117");
        api.handleIntent(getIntent(), this);
        // 2.立即请求
        final SendAuth.Req req = new SendAuth.Req();
        req.transaction = "wxfb11cdec3869a117";
        req.scope = "snsapi_userinfo";
        req.state = TAG;
        api.sendReq(req);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":onResume()");
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        // 3.得到code
        Lgg.t(TAG).ii("WX had responce");
        SendAuth.Resp res = (SendAuth.Resp) baseResp;
        errCode = res.errCode;
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
                Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_cancel, 2500);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_reject, 2500);
                finish();
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_error, 2500);
                finish();
                break;
            default:
                Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_no_effect, 2500);
                finish();
                break;
        }
    }

    /**
     * 使用code进行请求获取access token
     *
     * @param code code
     */
    private void getAccessToken(String code) {
        // https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" // 基本网址
                             + "appid=wxfb11cdec3869a117" // appid
                             + "&secret=fe5bd0899662b15f47c4cbbdfa8ff0b5" // appsecret
                             + "&code=" + code // code
                             + "&grant_type=authorization_code";// grant_type
        RequestParams rp = new RequestParams(url);
        x.http().get(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String tokenInfo) {
                Lgg.t(TAG).ii(tokenInfo);
                if (tokenInfo.contains("errcode")) {
                    Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_error, 2500);
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
                Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_error, 2500);
                finish();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_cancel, 2500);
                finish();
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
        // https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
        String url = "https://api.weixin.qq.com/sns/userinfo?"// url
                             + "access_token=" + accessToken // token
                             + "&openid=" + openid;// openid
        RequestParams rp = new RequestParams(url);
        x.http().get(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String userInfo) {
                Lgg.t(TAG).ii(userInfo);
                // TODO: 2018/9/29 0029  
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_error, 2500);
                finish();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_cancel, 2500);
                finish();
            }

            @Override
            public void onFinished() {

            }
        });
    }


}
