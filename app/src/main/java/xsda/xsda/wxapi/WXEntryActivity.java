package xsda.xsda.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

import xsda.xsda.R;
import xsda.xsda.helper.LoginOrOutHelper;
import xsda.xsda.helper.UserVerifyHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Tgg;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private String TAG = "XSDA_WXEntryActivity";
    private WXHelper wxHelper;// 微信辅助

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx);
        // 初始化微信
        wxHelper = new WXHelper(this);
        api = wxHelper.initWXAPI(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        // 植入监听
        wxHelper.setOnErrAuthDeniedListener(() -> {
            Lgg.t(TAG).ee(":onResp() 授权被拒绝");
            Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_reject, 2500);
            finish();
        });
        wxHelper.setOnErrAuthNoEffectListener(() -> {
            Lgg.t(TAG).ee(":onResp() 授权无效");
            Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_no_effect, 2500);
            finish();
        });
        wxHelper.setOnErrUnsupportListener(() -> {
            Lgg.t(TAG).ee(":onResp() 不支持授权");
            Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_unsupport, 2500);
            finish();
        });
        wxHelper.setOnErrUserCancelListener(() -> {
            Lgg.t(TAG).ee(":onResp() 授权取消");
            Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_cancel, 2500);
            finish();
        });
        wxHelper.setOnErrUserErrorListener(ex -> {
            if (ex != null) {
                Lgg.t(TAG).ee(":onResp() 授权错误: " + ex.getMessage());
            }
            Tgg.show(WXEntryActivity.this, R.string.login_wechat_authorized_err, 2500);
            finish();
        });
        wxHelper.setOnGetWeChatInfoSuccessListener(userInfo -> {
            Lgg.t(TAG).ii(":onResp() 授权成功\n" + userInfo);
            WechatInfo wechatInfo = JSONObject.parseObject(userInfo, WechatInfo.class);
            // 查询数据库是否已经保存过openid
            checkOpenId(wechatInfo);
        });

        wxHelper.handlerResp(baseResp);
    }

    /**
     * 判断openid是否有保存过(用户是否使用过微信登陆)
     *
     * @param wechatInfo 微信信息
     */
    private void checkOpenId(WechatInfo wechatInfo) {
        Lgg.t(TAG).ii("Method--> " + getClass().getSimpleName() + ":checkOpenId()");
        String wxopenid = wechatInfo.getOpenid();
        UserVerifyHelper userVerifyHelper = new UserVerifyHelper(this);
        userVerifyHelper.setOnOpenIdExistListener((phone, password) -> {/* openid存在 */
            // 调用登陆
            Lgg.t(TAG).ii(":checkOpenId(): openid had exist");
            toLogin(wechatInfo, phone, password);
        });
        
        userVerifyHelper.setOnOpenidNotExistListener(() -> {/* openid不存在 */
            Lgg.t(TAG).ii(":checkOpenId(): openid not exist");
            wechatInfo.setAttach(Cons.ATTACH_GO_TO_BINDPHONE);
            // 发送至bindphonefrag.java & loginfrag.java & mainfrag.java
            EventBus.getDefault().postSticky(wechatInfo);
            finish();
        });
        
        userVerifyHelper.setOnExceptionListener(e -> {/* 出错 */
            Lgg.t(TAG).ii(":checkOpenId(): error: " + e.getMessage());
            wechatInfo.setAttach(Cons.ATTACH_GO_TO_ERROR);
            // 发送至bindphonefrag.java & loginfrag.java & mainfrag.java
            EventBus.getDefault().postSticky(wechatInfo);
            finish();
        });
        userVerifyHelper.isOpenidExist(wxopenid);

    }

    /**
     * 登陆(目的是产生AVUser实例, 避免MainFrag.java的avuser空指针)
     *
     * @param phone    用户
     * @param password 密码
     */
    private void toLogin(WechatInfo wechatInfo, String phone, String password) {
        LoginOrOutHelper loginHelper = new LoginOrOutHelper(this);
        loginHelper.setOnLoginErrorListener(ex -> Tgg.show(this, R.string.login_failed, 2500));
        loginHelper.setOnLoginUserNotExistListener(() -> Tgg.show(this, R.string.login_user_not_exist, 2500));
        loginHelper.setOnLoginSuccessListener(avUser -> {
            // 保存密码到本地
            Ogg.saveLoginJson(this, phone, password, true);
            wechatInfo.setAttach(Cons.ATTACH_GO_TO_MAIN);
            // 发送至bindphonefrag.java & loginfrag.java & mainfrag.java
            EventBus.getDefault().postSticky(wechatInfo);
            finish();
        });
        loginHelper.login(phone, password);
    }
}
