package xsda.xsda.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import xsda.xsda.R;
import xsda.xsda.utils.Lgg;
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
            // finish();
            // TODO: 2018/10/8 0008  授权成功后的处理
        });

        wxHelper.handlerResp(baseResp);
    }
}
