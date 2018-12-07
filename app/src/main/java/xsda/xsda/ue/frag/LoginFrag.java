package xsda.xsda.ue.frag;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.hiber.tools.layout.PercentRelativeLayout;
import com.john.waveview.WaveView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import xsda.xsda.R;
import xsda.xsda.bean.LoginBean;
import xsda.xsda.helper.LoginOrOutHelper;
import xsda.xsda.ue.activity.SplashActivity;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.AuthorizedLoadWidget;
import xsda.xsda.widget.WaitingWidget;
import xsda.xsda.wxapi.WXEntryActivity;
import xsda.xsda.wxapi.WXHelper;
import xsda.xsda.wxapi.WechatInfo;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class LoginFrag extends BaseFrag {

    @BindView(R.id.rl_login_input_username)
    PercentRelativeLayout rlUsername;
    @BindView(R.id.et_login_input_username)
    EditText etUserName;
    @BindView(R.id.v_login_input_username_line)
    View vUserNameLine;

    @BindView(R.id.rl_login_input_password)
    PercentRelativeLayout rlPassword;
    @BindView(R.id.et_login_input_password)
    EditText etPassword;
    @BindView(R.id.iv_login_eye)
    ImageView ivEye;
    @BindView(R.id.v_login_input_password_line)
    View vPasswordLine;

    @BindView(R.id.iv_login_wechat)
    ImageView ivWechat;
    @BindView(R.id.wv_login)
    WaveView wvLogin;
    @BindView(R.id.tv_login_click)
    TextView tvLoginClick;
    @BindView(R.id.tv_login_register)
    TextView tvRegisterClick;
    @BindView(R.id.tv_login_forgot)
    TextView tvForgot;

    @BindView(R.id.widget_login_waitting)
    WaitingWidget widgetLoginWaitting;

    @BindView(R.id.rl_login_remember)
    PercentRelativeLayout rlLoginRemember;
    @BindView(R.id.iv_login_remember_checkbox)
    ImageView ivLoginRememberCheckbox;

    @BindView(R.id.widget_login_authorized)
    AuthorizedLoadWidget widgetLoginAuthorized;


    private int colorFocus;
    private int colorUnFocus;
    private Drawable visibleEye;
    private Drawable unVisibleEye;
    private Drawable checked;
    private Drawable uncheck;
    private List<String> needAuthorizedLoadList;
    private String TAG = "LoginFrag";
    private Platform platform;
    private WechatInfo wechatInfo;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_login;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        initRes();
        onClickEvent();
    }

    /**
     * 使用传统的微信登陆接入
     */
    private void userWechatOriToAuthorize() {
        // 判断微信是否登陆
        boolean wxInstall = WXHelper.isWXInstall(activity);
        if (!wxInstall) {
            Tgg.show(activity, R.string.login_wechat_install, 2500);
        } else {
            widgetLoginAuthorized.setVisibility(View.GONE);
            toActivity(activity, WXEntryActivity.class, false);
        }
    }

    /**
     * 获取到微信信息
     *
     * @param wechatInfo 微信信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getWXInfo(WechatInfo wechatInfo) {
        this.wechatInfo = wechatInfo;
        if (wechatInfo != null) {
            String attach = wechatInfo.getAttach();
            if (attach.contains(Cons.ATTACH_GO_TO_BINDPHONE)) {
                Lgg.t(TAG).ii("to bindphone");
                toFrag(this.getClass(), BindphoneFrag.class, null, true);
            } else if (attach.contains(Cons.ATTACH_GO_TO_MAIN)) {
                Lgg.t(TAG).ii("to main");
                toFrag(this.getClass(), MainFrag.class, null, true);
            } else if (attach.contains(Cons.ATTACH_GO_TO_ERROR)) {
                Lgg.t(TAG).ii("wx error");
                toast(R.string.login_wechat_authorized_openid_error, 2500);
            }
        }
    }

    @Override
    public boolean isReloadData() {
        // 页面切换回来不重载数据
        return false;
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
        LoginBean loginBean = Ogg.readLoginJson(activity);
        // 是否显示密码
        if (loginBean.isRemember()) {
            etUserName.setText(loginBean.getPhoneNum());
            etPassword.setText(loginBean.getPassword());
        }

        if (AVUser.getCurrentUser() != null) {
            String username = AVUser.getCurrentUser().getUsername();
            Lgg.t(TAG).ii("current user name: " + username);
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

    @Override
    public boolean onBackPresss() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void weChatLogin() {
        // 微信登陆逻辑
        widgetLoginAuthorized.setVisibility(View.VISIBLE);
        userWechatOriToAuthorize();
    }

    /**
     * 普通登陆
     */
    public void normalLogin() {

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
                    Ogg.saveLoginJson(activity, phoneNum, password, true);
                } else {
                    Ogg.saveLoginJson(activity, "", "", false);
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
