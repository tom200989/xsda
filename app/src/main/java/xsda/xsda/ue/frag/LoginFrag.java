package xsda.xsda.ue.frag;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;
import com.john.waveview.WaveView;
import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import xsda.xsda.R;
import xsda.xsda.helper.LoginHelper;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.WaitingWidget;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class LoginFrag extends RootFrag {

    @Bind(R.id.rl_login_input_username)
    PercentRelativeLayout rlUsername;
    @Bind(R.id.et_login_input_username)
    EditText etUserName;
    @Bind(R.id.v_login_input_username_line)
    View vUserNameLine;

    @Bind(R.id.rl_login_input_password)
    PercentRelativeLayout rlPassword;
    @Bind(R.id.et_login_input_password)
    EditText etPassword;
    @Bind(R.id.iv_login_eye)
    ImageView ivEye;
    @Bind(R.id.v_login_input_password_line)
    View vPasswordLine;

    @Bind(R.id.iv_login_wechat)
    ImageView ivWechat;
    @Bind(R.id.wv_login)
    WaveView wvLogin;
    @Bind(R.id.tv_login_click)
    TextView tvLoginClick;
    @Bind(R.id.tv_login_register)
    TextView tvRegisterClick;
    @Bind(R.id.tv_login_forgot)
    TextView tvForgot;

    @Bind(R.id.widget_login_waitting)
    WaitingWidget widgetLoginWaitting;

    private int colorFocus;
    private int colorUnFocus;
    private Drawable visibleEye;
    private Drawable unVisibleEye;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_login;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        initRes();
        onClickEvent();
    }


    public void onClickEvent() {
        // 设置焦点控制底线颜色
        etUserName.setOnFocusChangeListener((v, hasFocus) -> vUserNameLine.setBackgroundColor(hasFocus ? colorFocus : colorUnFocus));
        etPassword.setOnFocusChangeListener((v, hasFocus) -> vPasswordLine.setBackgroundColor(hasFocus ? colorFocus : colorUnFocus));
        // 可视|不可视切换
        ivEye.setOnClickListener(v -> {
            etPassword.setInputType(etPassword.getInputType() == 0x90 ? 0x81 : 0x90);
            // setInputType后设定请求焦点, 否则setSelection会失败
            etPassword.requestFocus();
            etPassword.setSelection(etPassword.getText().toString().length());
            ivEye.setImageDrawable(ivEye.getDrawable() == visibleEye ? unVisibleEye : visibleEye);
        });
        // 微信登陆
        ivWechat.setOnClickListener(v -> weChat());
        // 普通登陆
        tvLoginClick.setOnClickListener(v -> normalLogin());
        // 注册
        tvRegisterClick.setOnClickListener(v -> register());
        // 忘记密码
        tvForgot.setOnClickListener(v -> forgotPassword());
    }

    @Override
    public boolean onBackPresss() {
        return false;
    }

    private void initRes() {
        colorFocus = getResources().getColor(R.color.colorCompanyDark);
        colorUnFocus = getResources().getColor(R.color.colorCompany);
        visibleEye = getResources().getDrawable(R.drawable.visible_eye);
        unVisibleEye = getResources().getDrawable(R.drawable.unvisible_eye);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void weChat() {
        // TODO: 2018/7/6 0006  微信登陆逻辑
    }

    /**
     * 普通登陆
     */
    public void normalLogin() {
        // TODO: 2018/7/6 0006  常规登陆逻辑
        String phoneNum = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        // 匹配规则
        boolean isMatcher = matchRule(phoneNum, password);
        if (isMatcher) {
            LoginHelper loginHelper = new LoginHelper(getActivity());
            loginHelper.setOnLoginPrepareListener(() -> widgetLoginWaitting.setVisibleText(getString(R.string.logining)));
            loginHelper.setOnLoginAfterListener(() -> widgetLoginWaitting.setGone());
            loginHelper.setOnLoginErrorListener(ex -> Tgg.show(getActivity(), R.string.login_failed, 2500));
            loginHelper.setOnLoginUserNotExistListener(() -> );
            loginHelper.login(phoneNum, password);
        }
    }

    /**
     * 匹配规则
     *
     * @param phoneNum 号码
     * @param password 密码
     * @return 是否匹配
     */
    private boolean matchRule(String phoneNum, String password) {
        if (!Ogg.matchPhoneReg(phoneNum)) {
            Tgg.show(getActivity(), getString(R.string.register_username_tip), 2500);
            return false;
        } else if (password.length() < 8 | password.length() > 16) {
            Tgg.show(getActivity(), getString(R.string.register_password_tip), 2500);
            return false;
        }
        return true;
    }

    /**
     * 忘记密码
     */
    public void forgotPassword() {
        // 忘记密码
        toFrag(getClass(), ForgotPsdFrag.class, null, true);
    }

    /**
     * 注册
     */
    private void register() {
        // 前往登陆界面
        toFrag(getClass(), RegisterFrag.class, null, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
