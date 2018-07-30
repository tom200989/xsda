package xsda.xsda.ue.frag;
/*
 * Created by qianli.ma on 2018/7/30 0030.
 */

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.Bind;
import xsda.xsda.R;
import xsda.xsda.ue.root.RootFrag;
import xsda.xsda.widget.WaitingWidget;

public class ForgotPsdFrag extends RootFrag {

    @Bind(R.id.iv_forgot_back)
    ImageView ivForgotBack;
    @Bind(R.id.rl_forgot_banner)
    PercentRelativeLayout rlForgotBanner;
    @Bind(R.id.iv_forgot_logo)
    ImageView ivForgotLogo;
    @Bind(R.id.iv_forgot_username_logo)
    ImageView ivForgotUsernameLogo;
    @Bind(R.id.et_forgot_input_username)
    EditText etForgotInputUsername;
    @Bind(R.id.v_forgot_username_line)
    View vForgotUsernameLine;
    @Bind(R.id.rl_forgot_username)
    PercentRelativeLayout rlForgotUsername;
    @Bind(R.id.iv_forgot_password_logo)
    ImageView ivForgotPasswordLogo;
    @Bind(R.id.et_forgot_input_password)
    EditText etForgotInputPassword;
    @Bind(R.id.v_forgot_password_line)
    View vForgotPasswordLine;
    @Bind(R.id.rl_forgot_password)
    PercentRelativeLayout rlForgotPassword;
    @Bind(R.id.iv_forgot_confirmPassword_logo)
    ImageView ivForgotConfirmPasswordLogo;
    @Bind(R.id.et_forgot_input_confirmPassword)
    EditText etForgotInputConfirmPassword;
    @Bind(R.id.v_forgot_confirmPassword_line)
    View vForgotConfirmPasswordLine;
    @Bind(R.id.rl_forgot_confirmPassword)
    PercentRelativeLayout rlForgotConfirmPassword;
    @Bind(R.id.iv_forgot_verifyCode_logo)
    ImageView ivForgotVerifyCodeLogo;
    @Bind(R.id.tv_forgot_getVerifyCode)
    TextView tvForgotGetVerifyCode;
    @Bind(R.id.et_forgot_input_verifyCode)
    EditText etForgotInputVerifyCode;
    @Bind(R.id.v_forgot_verifyCode_line)
    View vForgotVerifyCodeLine;
    @Bind(R.id.rl_forgot_verifyCode)
    PercentRelativeLayout rlForgotVerifyCode;
    @Bind(R.id.tv_forgot_commit)
    TextView tvForgotCommit;
    @Bind(R.id.rl_forgot_all)
    PercentRelativeLayout rlForgotAll;
    @Bind(R.id.widget_waiting)
    WaitingWidget widgetWaiting;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_forgotpsd;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        // TODO: 2018/7/30 0030  
    }

    @Override
    public boolean onBackPresss() {
        toFrag(getClass(), LoginFrag.class, null, false);
        return true;
    }
}
