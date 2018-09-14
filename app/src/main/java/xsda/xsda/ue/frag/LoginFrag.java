package xsda.xsda.ue.frag;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hiber.tools.RootEncrypt;
import com.john.waveview.WaveView;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import xsda.xsda.R;
import xsda.xsda.bean.LoginBean;
import xsda.xsda.helper.LoginOrOutHelper;
import xsda.xsda.helper.wechat.WechatHelper;
import xsda.xsda.ue.activity.SplashActivity;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.AuthorizedLoadWidget;
import xsda.xsda.widget.WaitingWidget;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class LoginFrag extends BaseFrag {

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

    @Bind(R.id.rl_login_remember)
    PercentRelativeLayout rlLoginRemember;
    @Bind(R.id.iv_login_remember_checkbox)
    ImageView ivLoginRememberCheckbox;

    @Bind(R.id.widget_login_authorized)
    AuthorizedLoadWidget widgetLoginAuthorized;


    private int colorFocus;
    private int colorUnFocus;
    private Drawable visibleEye;
    private Drawable unVisibleEye;
    private Drawable checked;
    private Drawable uncheck;
    private List<String> needAuthorizedLoadList;
    private WechatHelper wechatHelper;
    private String TAG = "LoginFrag";
    private int initCount = 0;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_login;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        initRes();
        onClickEvent();
        authorized(whichFragmentStart);
    }

    private void initRes() {
        needAuthorizedLoadList = new ArrayList<>();
        needAuthorizedLoadList.add(SplashFrag.class.getSimpleName());
        needAuthorizedLoadList.add(GuideFrag.class.getSimpleName());
        needAuthorizedLoadList.add(UpdateFrag.class.getSimpleName());
        colorFocus = getResources().getColor(R.color.colorCompanyDark);
        colorUnFocus = getResources().getColor(R.color.colorCompany);
        visibleEye = getResources().getDrawable(R.drawable.visible_eye);
        unVisibleEye = getResources().getDrawable(R.drawable.unvisible_eye);
        checked = getResources().getDrawable(R.drawable.privacy_checkbox_checked);
        uncheck = getResources().getDrawable(R.drawable.privacy_checkbox_uncheck);
    }

    public void onClickEvent() {
        // 从shareprefrence获取登陆信息
        LoginBean loginBean = getLoginBeanFromShare();
        // 是否显示密码
        if (loginBean.isRemember()) {
            etUserName.setText(loginBean.getPhoneNum());
            etPassword.setText(loginBean.getPassword());
        }
        // 记住密码图标
        ivLoginRememberCheckbox.setImageDrawable(loginBean.isRemember() ? checked : uncheck);
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
        ivWechat.setOnClickListener(v -> weChatLogin());
        // 普通登陆
        tvLoginClick.setOnClickListener(v -> normalLogin());
        // 注册
        tvRegisterClick.setOnClickListener(v -> register());
        // 忘记密码
        tvForgot.setOnClickListener(v -> forgotPassword());
        // 记住密码
        rlLoginRemember.setOnClickListener(v -> ivLoginRememberCheckbox.setImageDrawable(ivLoginRememberCheckbox.getDrawable() == checked ? uncheck : checked));
    }

    /**
     * 针对指定跳转过来的页面进行微信登陆
     *
     * @param whichFragmentStart 由哪个界面跳转过来
     */
    private void authorized(String whichFragmentStart) {
        boolean isNeedAuthorizedLoadUi = isNeedShowAuthorizedUi(whichFragmentStart);
        // widgetLoginAuthorized.setVisibility(isNeedAuthorizedLoadUi ? View.VISIBLE : View.GONE);
        // TODO: 2018/9/12 0012  开始认证
        if (initCount == 0) {
            wechatHelper = new WechatHelper(getActivity());
            wechatHelper.setOnWeChatNotInstallListener(() -> Tgg.show(activity, R.string.login_wechat_install, 2500));
            wechatHelper.setOnNoAuthorizedListener(wechatBean -> Tgg.show(activity, "有微信但还没有认证", 2500));
            wechatHelper.setOnHadAuthorizedListener(wechatBean -> Tgg.show(activity, "有微信并且已经认证", 2500));
            wechatHelper.setOnAuthorizedCompleteListener(wechatBean -> {
                Tgg.show(activity, "认证完成", 2500);
                Lgg.t(TAG).ii(wechatBean.toString());
            });
            wechatHelper.setOnAuthorizedCancelListener(wechatBean -> Tgg.show(activity, "认证取消", 2500));
            wechatHelper.setOnAuthorizedErrorListener((wechatBean, throwable) -> Tgg.show(activity, "认证错误", 2500));
            wechatHelper.initCheckAuthorized();
            initCount++;
        }
    }

    @Override
    public boolean onBackPresss() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void weChatLogin() {
        // TODO: 2018/7/6 0006  微信登陆逻辑
        wechatHelper.clickAuthorized();
    }

    /**
     * 普通登陆
     */
    public void normalLogin() {

        if (true) {
            Platform plat = ShareSDK.getPlatform(Wechat.NAME);
            plat.removeAccount(true);
            Tgg.show(activity, "退出微信成功", 1000);
            return;
        }

        String phoneNum = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        // 匹配规则
        boolean isMatcher = matchRule(phoneNum, password);
        if (isMatcher) {
            LoginOrOutHelper loginHelper = new LoginOrOutHelper(getActivity());
            loginHelper.setOnLoginPrepareListener(() -> widgetLoginWaitting.setVisibleText(getString(R.string.logining)));
            loginHelper.setOnLoginAfterListener(() -> widgetLoginWaitting.setGone());
            loginHelper.setOnLoginErrorListener(ex -> Tgg.show(getActivity(), R.string.login_failed, 2500));
            loginHelper.setOnLoginUserNotExistListener(() -> Tgg.show(getActivity(), R.string.login_user_not_exist, 2500));
            loginHelper.setOnLoginSuccessListener(avUser -> {
                // 保存用户对象以及即时通讯对象
                ((SplashActivity) getActivity()).avUser = avUser;
                // 提示
                Tgg.show(getActivity(), R.string.login_success, 2500);
                // 保存用户信息到临时集合
                if (ivLoginRememberCheckbox.getDrawable() == checked) {
                    LoginBean loginBean = new LoginBean();
                    loginBean.setPhoneNum(phoneNum);
                    loginBean.setPassword(password);
                    loginBean.setRemember(ivLoginRememberCheckbox.getDrawable() == checked);
                    String loginJson = JSONObject.toJSONString(loginBean);
                    loginJson = RootEncrypt.des_encrypt(loginJson);// 加密
                    Sgg.getInstance(getActivity()).putString(Cons.SP_LOGIN_INFO, loginJson);
                } else {
                    String loginJson = JSONObject.toJSONString(new LoginBean());
                    loginJson = RootEncrypt.des_encrypt(loginJson);// 加密
                    Sgg.getInstance(getActivity()).putString(Cons.SP_LOGIN_INFO, loginJson);
                }
                // 封装数据并跳转
                Ogg.hideKeyBoard(getActivity());
                toFrag(getClass(), MainFrag.class, null, false);
                Lgg.t(Cons.TAG).ii("login success to main fragment");
            });
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
        Ogg.hideKeyBoard(getActivity());
        toFrag(getClass(), ForgotPsdFrag.class, null, true);
    }

    /**
     * 注册
     */
    private void register() {
        // 前往登陆界面
        Ogg.hideKeyBoard(getActivity());
        toFrag(getClass(), RegisterFrag.class, null, true);
    }

    /**
     * 从shareprefrence获取登陆信息
     *
     * @return loginbean
     */
    private LoginBean getLoginBeanFromShare() {
        String loginJson = Sgg.getInstance(getActivity()).getString(Cons.SP_LOGIN_INFO, "");
        loginJson = RootEncrypt.des_descrypt(loginJson);// 解密
        LoginBean loginBean;
        if (TextUtils.isEmpty(loginJson)) {
            loginBean = new LoginBean();
        } else {
            loginBean = JSONObject.parseObject(loginJson, LoginBean.class);
        }
        return loginBean;
    }

    /**
     * 是否需要显示权限申请界面
     *
     * @param whichFragmentStart 由哪个界面跳转过来
     * @return 是否需要显示权限申请界面
     */
    private boolean isNeedShowAuthorizedUi(String whichFragmentStart) {
        for (String fragment : needAuthorizedLoadList) {
            if (whichFragmentStart.contains(fragment)) {
                return true;
            }
        }
        return false;
    }
}
