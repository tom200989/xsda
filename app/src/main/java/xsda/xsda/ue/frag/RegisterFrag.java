package xsda.xsda.ue.frag;
/*
 * Created by qianli.ma on 2018/7/25 0025.
 */

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiber.tools.layout.PercentRelativeLayout;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.helper.GetServerDateHelper;
import xsda.xsda.helper.TimerHelper;
import xsda.xsda.helper.VerifyCodeHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.WaitingWidget;

public class RegisterFrag extends BaseFrag {

    @BindView(R.id.rl_register_all)
    PercentRelativeLayout rlRegisterAll;

    @BindView(R.id.rl_register_banner)
    PercentRelativeLayout rlRegisterBanner;
    @BindView(R.id.iv_register_back)
    ImageView ivRegisterBack;

    @BindView(R.id.iv_register_logo)
    ImageView ivRegisterLogo;
    // phonename
    @BindView(R.id.rl_register_username)
    PercentRelativeLayout rlRegisterUsername;
    @BindView(R.id.iv_register_username_logo)
    ImageView ivRegisterUsernameLogo;
    @BindView(R.id.et_register_input_username)
    EditText etRegisterInputUsername;
    @BindView(R.id.v_register_username_line)
    View vRegisterUsernameLine;
    // nickname
    @BindView(R.id.rl_register_nickname)
    PercentRelativeLayout rlRegisterNickname;
    @BindView(R.id.iv_register_nickname_logo)
    ImageView ivRegisterNicknameLogo;
    @BindView(R.id.et_register_input_nickname)
    EditText etRegisterInputNickname;
    @BindView(R.id.v_register_nickname_line)
    View vRegisterNicknameLine;
    // password
    @BindView(R.id.rl_register_password)
    PercentRelativeLayout rlRegisterPassword;
    @BindView(R.id.iv_register_password_logo)
    ImageView ivRegisterPasswordLogo;
    @BindView(R.id.et_register_input_password)
    EditText etRegisterInputPassword;
    @BindView(R.id.v_register_password_line)
    View vRegisterPasswordLine;
    // confirm password
    @BindView(R.id.rl_register_confirmPassword)
    PercentRelativeLayout rlRegisterConfirmPassword;
    @BindView(R.id.iv_register_confirmPassword_logo)
    ImageView ivRegisterConfirmPasswordLogo;
    @BindView(R.id.et_register_input_confirmPassword)
    EditText etRegisterInputConfirmPassword;
    @BindView(R.id.v_register_confirmPassword_line)
    View vRegisterConfirmPasswordLine;
    // verifycode
    @BindView(R.id.rl_register_verifyCode)
    PercentRelativeLayout rlRegisterVerifyCode;
    @BindView(R.id.iv_register_verifyCode_logo)
    ImageView ivRegisterVerifyCodeLogo;
    @BindView(R.id.tv_register_getVerifyCode)
    TextView tvRegisterGetVerifyCode;
    @BindView(R.id.et_register_input_verifyCode)
    EditText etRegisterInputVerifyCode;
    @BindView(R.id.v_register_verifyCode_line)
    View vRegisterVerifyCodeLine;
    // commit
    @BindView(R.id.tv_register_commit)
    TextView tvRegisterCommit;

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
        initRes();
        return R.layout.frag_register;
    }

    @Override
    public boolean isReloadData() {
        return true;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        // 封装控件
        ets = new EditText[]{etRegisterInputUsername, etRegisterInputPassword, etRegisterInputConfirmPassword, etRegisterInputVerifyCode, etRegisterInputNickname};
        lines = new View[]{vRegisterUsernameLine, vRegisterPasswordLine, vRegisterConfirmPasswordLine, vRegisterVerifyCodeLine, vRegisterNicknameLine};
        onClickEvent();
        // 获取服务器时间
        getServerDate();
    }

    public void onClickEvent() {
        // E.设定焦点切换监听
        for (int i = 0; i < ets.length; i++) {
            int finalI = i;
            ets[i].setOnFocusChangeListener((v, hasFocus) -> lines[finalI].setBackgroundColor(hasFocus ? color_checked : color_unchecked));
        }
        ets[0].requestFocus();

        // E.回退
        ivRegisterBack.setOnClickListener(v -> onBackPresss());

        // E.获取验证码
        tvRegisterGetVerifyCode.setOnClickListener(v -> {
            // 校验手机号和密码
            if (matchEdittext(activity, false)) {
                // 符合规则的开始请求验证码
                String phoneNum = etRegisterInputUsername.getText().toString();
                String password = etRegisterInputPassword.getText().toString();
                Tgg.show(activity, getString(R.string.register_begin2getverify_tip), 2000);
                if (currentServerDate != -1) {
                    getVerifyCode(phoneNum, password);
                }
            }
        });

        // E.提交注册
        tvRegisterCommit.setOnClickListener(v -> {
            Lgg.t(Cons.TAG).ii("点击提交验证码");
            // 校验手机号密码验证码等等
            if (matchEdittext(activity, true)) {
                String phoneName = etRegisterInputUsername.getText().toString();
                String password = etRegisterInputPassword.getText().toString();
                String verifyCode = etRegisterInputVerifyCode.getText().toString();

                if (currentServerDate != -1) {
                    /* 提交验证码 */
                    VerifyCodeHelper verifyCodeHelper = new VerifyCodeHelper(getActivity());
                    verifyCodeHelper.setOnCommitVerifyPrepareListener(() -> widgetWaiting.setVisibleByAnim());
                    verifyCodeHelper.setOnCommitVerifyAfterListener(() -> widgetWaiting.setGone());
                    verifyCodeHelper.setOnCommitVerifyErrorListener(e -> Tgg.show(activity, text_Verify_error, 2000));
                    verifyCodeHelper.setOnCommitVerifySuccessListener(() -> {
                        // 提示验证成功
                        Tgg.show(activity, text_Verify_success, 2000);
                        new Handler().postDelayed(this::exit, 1500);
                    });
                    verifyCodeHelper.commitVerifyCode(phoneName, password, verifyCode);
                }
            }
        });
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
        long lastServerDate = Sgg.getInstance(activity).getLong(Cons.SP_SERVER_DATE, 0);

        // 时间正常
        if (currentServerDate > lastServerDate) {
            // 2分钟已过
            if (currentServerDate - lastServerDate > limitVerify) {
                countdown = COUNTDOWN;
                tvRegisterGetVerifyCode.setClickable(true);
            } else {// 2分钟没过
                tvRegisterGetVerifyCode.setClickable(false);
                // 计算出剩余时间
                countdown = (limitVerify - (currentServerDate - lastServerDate)) / 1000;
                // 启动倒数定时器
                countDown();
            }
        } else {// 时间不正常(当前服务器时间 < 上次记录的时间)
            tvRegisterGetVerifyCode.setClickable(false);
            exit();
            Tgg.show(activity, getString(R.string.register_time_error), 3000);
        }
    }

    @Override
    public boolean onBackPresss() {
        Ogg.hideKeyBoard(getActivity());
        toFrag(getClass(), LoginFrag.class, null, false);
        return true;
    }

    private void initRes() {
        color_checked = getResources().getColor(R.color.colorCompanyDark);
        color_unchecked = getResources().getColor(R.color.colorCompany);
        text_timeout = getString(R.string.register_timeout);
        text_frequently = getString(R.string.register_frequently_tip);
        text_user_exist = getString(R.string.register_user_exist_tip);
        text_success = getString(R.string.register_success);
        text_Verify_error = getString(R.string.register_commit_verify_failed);
        text_Verify_success = getString(R.string.register_commit_verify_success);
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
        if (!Ogg.matchPhoneReg(etRegisterInputUsername.getText().toString())) {
            etRegisterInputUsername.requestFocus();
            Tgg.show(context, context.getString(R.string.register_username_tip), 2000);
            return false;
        } else if (etRegisterInputPassword.getText().toString().length() < 8 // 密码位数
                           | etRegisterInputPassword.getText().toString().length() > 16) {// 密码位数
            etRegisterInputPassword.requestFocus();
            Tgg.show(context, context.getString(R.string.register_password_tip), 2000);
            return false;
        } else if (etRegisterInputConfirmPassword.getText().toString().length() < 8 // 密码位数
                           | etRegisterInputConfirmPassword.getText().toString().length() > 16) { // 密码位数
            etRegisterInputConfirmPassword.requestFocus();
            Tgg.show(context, context.getString(R.string.register_confirmpassword_tip), 2000);
            return false;
        } else if (!etRegisterInputPassword.getText().toString()// 第一次密码
                            .equals(etRegisterInputConfirmPassword.getText().toString())) {// 第二次密码
            etRegisterInputConfirmPassword.requestFocus();
            Tgg.show(context, context.getString(R.string.register_passwordnotsame_tip), 2000);
            return false;
        } else if (TextUtils.isEmpty(etRegisterInputVerifyCode.getText().toString()) & isVerify) {
            etRegisterInputVerifyCode.requestFocus();
            Tgg.show(context, context.getString(R.string.register_not_verify_empty), 2000);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 退出当前界面
     */
    public void exit() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        toFrag(getClass(), LoginFrag.class, null, false);
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
                    tvRegisterGetVerifyCode.setClickable(false);
                    tvRegisterGetVerifyCode.setTextColor(getResources().getColor(R.color.colorGrayDark));
                    tvRegisterGetVerifyCode.setText(String.valueOf(countdown-- + "s"));
                } else {
                    tvRegisterGetVerifyCode.setClickable(true);
                    tvRegisterGetVerifyCode.setTextColor(getResources().getColor(R.color.colorCompanyDark));
                    tvRegisterGetVerifyCode.setText(getString(R.string.register_get_verifycode));
                    countdown = COUNTDOWN;
                    timer.stop();
                }
            }
        };
        timer.start(0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * 申请验证码逻辑
     *
     * @param phoneNum 号码
     * @param password 密码
     */
    private void getVerifyCode(String phoneNum, String password) {

        /* 申请获取验证码 */
        VerifyCodeHelper verifyCodeHelper = new VerifyCodeHelper(getActivity());
        verifyCodeHelper.setOnGetServerDateErrorListener(e -> Tgg.show(activity, text_timeout, 2000));
        verifyCodeHelper.setOnUserHadExistListener(() -> Tgg.show(activity, text_user_exist, 2000));
        verifyCodeHelper.setOnGetVerifyErrorListener(e -> {
            if (e.getCode() == Egg.CANT_SEND_SMS_TOO_FREQUENTLY) {
                // 验证码获取频繁
                Tgg.show(activity, text_frequently, 2000);
            } else {
                Tgg.show(activity, text_timeout, 2000);
            }
        });
        verifyCodeHelper.setOnGetVerifySuccessListener(() -> {
            // 提示留意短信
            Tgg.show(activity, text_success, 2000);
            // 显示倒计时
            Lgg.t(Cons.TAG).ii("begin to countdown 120s");
            countDown();
        });

        // 获取昵称
        String nickName = etRegisterInputNickname.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            nickName = phoneNum;
        }
        verifyCodeHelper.getVerifyCode(phoneNum, password, nickName);
    }
}
