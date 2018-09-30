package xsda.xsda.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import xsda.xsda.R;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    private String TAG = "XSDA_WXEntryActivity";
    private int errCode = -1000;// 默认-1000为首次进入
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
        // TODO: 2018/9/30 0030 植入监听 
        wxHelper.handlerResp(baseResp);
    }
}
