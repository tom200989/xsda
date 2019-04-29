package xsda.xsda.ue.frag;
/*
 * Created by qianli.ma on 2018/7/30 0030.
 */

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;
import com.hiber.tools.layout.PercentRelativeLayout;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.helper.GetServerDateHelper;
import xsda.xsda.helper.ResetPasswordHelper;
import xsda.xsda.helper.TimerHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.WaitingWidget;

public class ForgotPsdFrag extends RootFrag {

    // 全体
    @BindView(R.id.rl_forgot_all)
    PercentRelativeLayout rlForgotAll;

    // 导航
    @BindView(R.id.rl_forgot_banner)
    PercentRelativeLayout rlForgotBanner;
    @BindView(R.id.iv_forgot_back)
    ImageView ivForgotBack;

    // logo
    @BindView(R.id.iv_forgot_logo)
    ImageView ivForgotLogo;

    // 用户名
    @BindView(R.id.rl_forgot_username)
    PercentRelativeLayout rlForgotUsername;
    @BindView(R.id.iv_forgot_username_logo)
    ImageView ivForgotUsernameLogo;
    @BindView(R.id.et_forgot_input_username)
    EditText etForgotInputUsername;
    @BindView(R.id.v_forgot_username_line)
    View vForgotUsernameLine;

    // 密码
    @BindView(R.id.rl_forgot_password)
    PercentRelativeLayout rlForgotPassword;
    @BindView(R.id.iv_forgot_password_logo)
    ImageView ivForgotPasswordLogo;
    @BindView(R.id.et_forgot_input_password)
    EditText etForgotInputPassword;
    @BindView(R.id.v_forgot_password_line)
    View vForgotPasswordLine;

    // 确认密码
    @BindView(R.id.rl_forgot_confirmPassword)
    PercentRelativeLayout rlForgotConfirmPassword;
    @BindView(R.id.iv_forgot_confirmPassword_logo)
    ImageView ivForgotConfirmPasswordLogo;
    @BindView(R.id.et_forgot_input_confirmPassword)
    EditText etForgotInputConfirmPassword;
    @BindView(R.id.v_forgot_confirmPassword_line)
    View vForgotConfirmPasswordLine;

    // 验证码
    @BindView(R.id.rl_forgot_verifyCode)
    PercentRelativeLayout rlForgotVerifyCode;
    @BindView(R.id.iv_forgot_verifyCode_logo)
    ImageView ivForgotVerifyCodeLogo;
    @BindView(R.id.tv_forgot_getVerifyCode)
    TextView tvForgotGetVerifyCode;
    @BindView(R.id.et_forgot_input_verifyCode)
    EditText etForgotInputVerifyCode;
    @BindView(R.id.v_forgot_verifyCode_line)
    View vForgotVerifyCodeLine;

    // 提交
    @BindView(R.id.tv_forgot_commit)
    TextView tvForgotCommit;

    // 等待
    @BindView(R.id.widget_waiting)
    WaitingWidget widgetWaiting;

    private int color_checked;
    private int color_unchecked;
    private String text_timeout;
    private String text_frequently;
    private String text_success;
    private String text_user_exist;
    private String text_Verify_error;
    private String text_Verify_success;

    private EditText[] ets;
    private View[] lines;
    private TimerHelper timer;
    private final long COUNTDOWN = 120;
    private long countdown = COUNTDOWN;// 默认间隔120秒才可重复获取验证码
    private long currentServerDate = -1;// 服务器当前时间
    private long limitVerify = countdown * 1000;// 限制2分钟以内不可再点击

    @Override
    public int onInflateLayout() {
        return R.layout.frag_forgotpsd;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        initRes();
        // 封装控件
        ets = new EditText[]{etForgotInputUsername, etForgotInputPassword, etForgotInputConfirmPassword, etForgotInputVerifyCode};
        lines = new View[]{vForgotUsernameLine, vForgotPasswordLine, vForgotConfirmPasswordLine, vForgotVerifyCodeLine};
        onClickEvent();
        // 获取服务器时间
        getServerDate();
    }

    private void initRes() {
        color_checked = getResources().getColor(R.color.colorCompanyDark);
        color_unchecked = getResources().getColor(R.color.colorCompany);
        text_timeout = getString(R.string.register_timeout);
        text_frequently = getString(R.string.register_frequently_tip);
        text_user_exist = getString(R.string.register_user_exist_tip);
        text_success = getString(R.string.register_success);
        text_Verify_error = getString(R.string.register_commit_verify_failed);
        text_Verify_success = getString(R.string.forgot_user_reset_psd_success);
    }

    /**
     * 获取服务器时间
     */
    private void getServerDate() {
        GetServerDateHelper getServerDateHelper = new GetServerDateHelper();
        getServerDateHelper.setOnGetServerErrorListener(e -> toFrag(getClass(), NetErrFrag.class, null, false));
        getServerDateHelper.setOnGetServerDateLongSuccessListener(currentServerDate -> {
            // 得到服务器当前时间
            this.currentServerDate = currentServerDate;
            // 刷新验证码按钮状态
            toGetVerifyInit();
        });
        getServerDateHelper.get();
    }

    /**
     * 获取验证码按钮的初始状态
     */
    private void toGetVerifyInit() {
        // 获取上次获取验证码成功的时间
        long lastServerDate = Sgg.getInstance(activity).getLong(Cons.SP_RESET_SERVER_DATE, 0);

        // 时间正常
        if (currentServerDate > lastServerDate) {
            // 2分钟已过
            if (currentServerDate - lastServerDate > limitVerify) {
                countdown = COUNTDOWN;
                tvForgotGetVerifyCode.setClickable(true);
            } else {// 2分钟没过
                tvForgotGetVerifyCode.setClickable(false);
                // 计算出剩余时间
                countdown = (limitVerify - (currentServerDate - lastServerDate)) / 1000;
                // 启动倒数定时器
                countDown();
            }
        } else {// 时间不正常(当前服务器时间 < 上次记录的时间)
            tvForgotGetVerifyCode.setClickable(false);
            onBackPresss();
            Tgg.show(activity, getString(R.string.register_time_error), 3000);
        }
    }

    public void onClickEvent() {
        // E.设定焦点切换监听
        for (int i = 0; i < ets.length; i++) {
            int finalI = i;
            ets[i].setOnFocusChangeListener((v, hasFocus) -> {
                lines[finalI].setBackgroundColor(hasFocus ? color_checked : color_unchecked);
            });
        }
        ets[0].requestFocus();

        // E.回退
        ivForgotBack.setOnClickListener(v -> onBackPresss());

        // E.获取验证码
        tvForgotGetVerifyCode.setOnClickListener(v -> {
            // 校验手机号和密码
            if (matchEdittext(activity, false)) {
                // 符合规则的开始请求验证码
                String phoneNum = etForgotInputUsername.getText().toString();
                String password = etForgotInputPassword.getText().toString();
                Tgg.show(activity, getString(R.string.register_begin2getverify_tip), 2000);
                if (currentServerDate != -1) {
                    getVerifyCode(phoneNum, password);
                }
            }
        });

        // E.提交注册
        tvForgotCommit.setOnClickListener(v -> {
            Lgg.t(Cons.TAG).ii("点击提交重置密码验证码");
            // 校验手机号密码验证码等等
            if (matchEdittext(activity, true)) {
                String password = etForgotInputConfirmPassword.getText().toString();
                String verifyCode = etForgotInputVerifyCode.getText().toString();
                if (currentServerDate != -1) {
                    /* 提交验证码 */
                    ResetPasswordHelper resetPasswordHelper = new ResetPasswordHelper(activity);
                    resetPasswordHelper.setOnPrepareListener(() -> widgetWaiting.setVisibleByAnim());
                    resetPasswordHelper.setOnAfterListener(() -> widgetWaiting.setGone());
                    resetPasswordHelper.setOnResetGetVerifyErrorListener(err -> Tgg.show(activity, text_Verify_error, 2000));
                    resetPasswordHelper.setOnCommitResetVerifySuccessListener(() -> {
                        // 提示验证成功
                        Tgg.show(activity, text_Verify_success, 2000);
                        new Handler().postDelayed(this::onBackPresss, 1500);
                    });
                    resetPasswordHelper.commitResetPasswordVerify(verifyCode, password);
                }
            }
        });
    }

    /**
     * 申请验证码逻辑
     *
     * @param phoneNum 号码
     * @param password 密码
     */
    private void getVerifyCode(String phoneNum, String password) {

        /* 申请获取重置验证码 */
        ResetPasswordHelper resetPasswordHelper = new ResetPasswordHelper(activity);
        resetPasswordHelper.setOnPrepareListener(() -> widgetWaiting.setVisibleByAnim());
        resetPasswordHelper.setOnAfterListener(() -> widgetWaiting.setGone());
        resetPasswordHelper.setOnResetGetVerifyErrorListener(err -> {
            if (err.getCode() == Egg.CANT_SEND_SMS_TOO_FREQUENTLY) {
                // 验证码获取频繁
                Tgg.show(activity, text_frequently, 2000);
            } else {
                Tgg.show(activity, text_timeout, 2000);
            }
        });
        resetPasswordHelper.setOnUserHadNotExistListener(() -> Tgg.show(activity, R.string.forgot_user_not_exist, 2500));
        resetPasswordHelper.setOnRequestPasswordResetSuccessListener(serverTime -> {
            // 提示留意短信
            Tgg.show(activity, text_success, 2000);
            // 显示倒计时
            countDown();
        });
        resetPasswordHelper.getResetVerify(phoneNum);
    }

    /**
     * 显示倒计时
     */
    private void countDown() {
        // 先停止
        if (timer != null) {
            timer.stop();
        }

        // 后创建
        timer = new TimerHelper(activity) {
            @Override
            public void doSomething() {
                if (countdown >= 1) {
                    tvForgotGetVerifyCode.setClickable(false);
                    tvForgotGetVerifyCode.setTextColor(getResources().getColor(R.color.colorGrayDark));
                    tvForgotGetVerifyCode.setText(String.valueOf(countdown-- + "s"));
                } else {
                    tvForgotGetVerifyCode.setClickable(true);
                    tvForgotGetVerifyCode.setTextColor(getResources().getColor(R.color.colorCompanyDark));
                    tvForgotGetVerifyCode.setText(getString(R.string.register_get_verifycode));
                    countdown = COUNTDOWN;
                    timer.stop();
                }
            }
        };
        timer.start(0, 1000);
    }

    /**
     * 校验手机号密码规则
     *
     * @param context  环境
     * @param isVerify 是否检查验证码框
     * @return true:符合规则
     */
    private boolean matchEdittext(Context context, boolean isVerify) {
        // 手机号是否匹配正则
        if (!Ogg.matchPhoneReg(etForgotInputUsername.getText().toString())) {
            etForgotInputUsername.requestFocus();
            Tgg.show(context, context.getString(R.string.register_username_tip), 2000);
            return false;
        } else if (etForgotInputPassword.getText().toString().length() < 8 // 密码位数
                           | etForgotInputPassword.getText().toString().length() > 16) {// 密码位数
            etForgotInputPassword.requestFocus();
            Tgg.show(context, context.getString(R.string.register_password_tip), 2000);
            return false;
        } else if (etForgotInputConfirmPassword.getText().toString().length() < 8 // 密码位数
                           | etForgotInputConfirmPassword.getText().toString().length() > 16) { // 密码位数
            etForgotInputConfirmPassword.requestFocus();
            Tgg.show(context, context.getString(R.string.register_confirmpassword_tip), 2000);
            return false;
        } else if (!etForgotInputPassword.getText().toString()// 第一次密码
                            .equals(etForgotInputConfirmPassword.getText().toString())) {// 第二次密码
            etForgotInputConfirmPassword.requestFocus();
            Tgg.show(context, context.getString(R.string.register_passwordnotsame_tip), 2000);
            return false;
        } else if (TextUtils.isEmpty(etForgotInputVerifyCode.getText().toString()) & isVerify) {
            etForgotInputVerifyCode.requestFocus();
            Tgg.show(context, context.getString(R.string.register_not_verify_empty), 2000);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onBackPresss() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        Ogg.hideKeyBoard(activity);
        toFrag(getClass(), LoginFrag.class, null, false);
        return true;
    }
}
