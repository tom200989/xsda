package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.android.percent.support.PercentRelativeLayout;

import xsda.xsda.R;
import xsda.xsda.helper.GetServerDateHelper;
import xsda.xsda.helper.GetVerifyCodeHelper;
import xsda.xsda.helper.TimerHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;

/**
 * Created by Administrator on 2018/7/8 0008.
 */

public class RegisterWidget extends RelativeLayout {

    private PercentRelativeLayout rlRegisterBanner;
    private ImageView ivRegisterBack;
    private ImageView ivRegisterLogo;
    private PercentRelativeLayout rlRegisterUsername;
    private ImageView ivRegisterUsernameLogo;
    private EditText etRegisterInputUsername;
    private View vRegisterUsernameLine;
    private PercentRelativeLayout rlRegisterPassword;
    private ImageView ivRegisterPasswordLogo;
    private EditText etRegisterInputPassword;
    private View vRegisterPasswordLine;
    private PercentRelativeLayout rlRegisterConfirmPassword;
    private ImageView ivRegisterConfirmPasswordLogo;
    private EditText etRegisterInputConfirmPassword;
    private View vRegisterConfirmPasswordLine;
    private PercentRelativeLayout rlRegisterVerifyCode;
    private ImageView ivRegisterVerifyCodeLogo;
    private TextView tvRegisterGetVerifyCode;
    private EditText etRegisterInputVerifyCode;
    private View vRegisterVerifyCodeLine;
    private TextView tvRegisterCommit;

    private int color_checked;
    private int color_unchecked;
    private String text_timeout;
    private EditText[] ets;
    private View[] lines;
    private String text_success;
    private GetVerifyCodeHelper getVerifyCodeHelper;
    private TimerHelper timer;
    private long countdown = 120;
    private long currentServerDate;
    private long limitVerify = countdown * 1000;// 限制2分钟以内不可再点击

    private void initViews(Context context) {
        View.inflate(context, R.layout.widget_register, this);
        rlRegisterBanner = findViewById(R.id.rl_register_banner);
        ivRegisterBack = findViewById(R.id.iv_register_back);
        ivRegisterLogo = findViewById(R.id.iv_register_logo);
        rlRegisterUsername = findViewById(R.id.rl_register_username);
        ivRegisterUsernameLogo = findViewById(R.id.iv_register_username_logo);
        etRegisterInputUsername = findViewById(R.id.et_register_input_username);
        vRegisterUsernameLine = findViewById(R.id.v_register_username_line);
        rlRegisterPassword = findViewById(R.id.rl_register_password);
        ivRegisterPasswordLogo = findViewById(R.id.iv_register_password_logo);
        etRegisterInputPassword = findViewById(R.id.et_register_input_password);
        vRegisterPasswordLine = findViewById(R.id.v_register_password_line);
        rlRegisterConfirmPassword = findViewById(R.id.rl_register_confirmPassword);
        ivRegisterConfirmPasswordLogo = findViewById(R.id.iv_register_confirmPassword_logo);
        etRegisterInputConfirmPassword = findViewById(R.id.et_register_input_confirmPassword);
        vRegisterConfirmPasswordLine = findViewById(R.id.v_register_confirmPassword_line);
        rlRegisterVerifyCode = findViewById(R.id.rl_register_verifyCode);
        ivRegisterVerifyCodeLogo = findViewById(R.id.iv_register_verifyCode_logo);
        tvRegisterGetVerifyCode = findViewById(R.id.tv_register_getVerifyCode);
        tvRegisterGetVerifyCode.setClickable(false);
        toGetVerifyInit(context);// 获取验证码按钮的初始状态
        etRegisterInputVerifyCode = findViewById(R.id.et_register_input_verifyCode);
        vRegisterVerifyCodeLine = findViewById(R.id.v_register_verifyCode_line);
        tvRegisterCommit = findViewById(R.id.tv_register_commit);
        ets = new EditText[]{etRegisterInputUsername, etRegisterInputPassword, etRegisterInputConfirmPassword, etRegisterInputVerifyCode};
        lines = new View[]{vRegisterUsernameLine, vRegisterPasswordLine, vRegisterConfirmPasswordLine, vRegisterVerifyCodeLine};
    }

    public RegisterWidget(Context context) {
        this(context, null, 0);
    }

    public RegisterWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegisterWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRes(context);
        initViews(context);
        getServerDate(context);

    }

    /**
     * 获取服务器时间
     *
     * @param context
     */
    private void getServerDate(Context context) {
        GetServerDateHelper getServerDateHelper = new GetServerDateHelper();
        getServerDateHelper.setOnGetServerErrorListener(e -> {
            // TODO: 2018/7/13 0013  回调到外部--> 让外部去处理错误的逻辑
        });
        getServerDateHelper.setOnGetServerDateLongSuccessListener(currentServerDate -> {
            // 得到服务器当前时间
            this.currentServerDate = currentServerDate;
            initEvent(context);
            initHelper(context);
        });
        getServerDateHelper.get();
    }

    private void initHelper(Context context) {
        /* 获取验证码 */
        getVerifyCodeHelper = new GetVerifyCodeHelper();
        getVerifyCodeHelper.setOnGetServerDateErrorListener(e -> Tgg.show(context, text_timeout, 2000));
        getVerifyCodeHelper.setOnVerifyErrorListener(e -> Tgg.show(context, text_timeout, 2000));
        getVerifyCodeHelper.setOnVerifySuccessListener(() -> {
            // 提示留意短信
            Tgg.show(context, text_success, 2000);
            // 显示倒计时
            countDown(context);
        });
    }

    /**
     * 显示倒计时
     *
     * @param context
     */
    private void countDown(Context context) {
        if (timer == null) {
            timer = new TimerHelper(context) {
                @Override
                public void doSomething() {
                    if (countdown >= 1) {
                        tvRegisterGetVerifyCode.setClickable(false);
                        tvRegisterGetVerifyCode.setTextColor(context.getResources().getColor(R.color.colorGrayDark));
                        tvRegisterGetVerifyCode.setText(String.valueOf(countdown-- + "s"));
                    } else {
                        tvRegisterGetVerifyCode.setClickable(true);
                        tvRegisterGetVerifyCode.setTextColor(context.getResources().getColor(R.color.colorCompanyDark));
                        tvRegisterGetVerifyCode.setText(context.getString(R.string.register_get_verifycode));
                        timer.stop();
                    }
                }
            };
        }
        timer.start(0, 1000);
    }

    /**
     * 获取验证码按钮的初始状态
     */
    private void toGetVerifyInit(Context context) {
        // 获取上次获取验证码成功的时间
        long lastServerDate = Sgg.getInstance(context).getLong(Cons.SP_SERVER_DATE, 0);
        // 时间正常
        if (currentServerDate > lastServerDate) {
            // 2分钟已过
            if (currentServerDate - lastServerDate > limitVerify) {
                countdown = 120;
                tvRegisterGetVerifyCode.setClickable(true);
            } else {// 2分钟没过
                tvRegisterGetVerifyCode.setClickable(false);
                // 计算出剩余时间
                countdown = (limitVerify - (currentServerDate - lastServerDate)) / 1000;
                // 启动倒数定时器
                countDown(context);
            }
        } else {// 时间不正常(当前服务器时间 < 上次记录的时间)
            tvRegisterGetVerifyCode.setClickable(false);
            exit();
            Tgg.show(context, context.getString(R.string.register_time_error), 2000);
        }
    }

    private void initEvent(Context context) {
        // 设定焦点监听
        for (int i = 0; i < ets.length; i++) {
            int finalI = i;
            ets[i].setOnFocusChangeListener((v, hasFocus) -> lines[finalI].setBackgroundColor(hasFocus ? color_checked : color_unchecked));
        }
        // 回退
        ivRegisterBack.setOnClickListener(v -> setVisibility(GONE));
        // 获取验证码
        tvRegisterGetVerifyCode.setOnClickListener(v -> {
            // 校验手机号和密码
            if (matchEdittext(context)) {
                // 符合规则的开始请求验证码
                String phoneNum = etRegisterInputUsername.getText().toString();
                String password = etRegisterInputPassword.getText().toString();
                getVerifyCodeHelper.get(phoneNum, password);
            }
        });
        // 提交注册
        tvRegisterCommit.setOnClickListener(v -> {
            // TODO: 2018/7/8 0008  提交注册
        });
    }

    /**
     * 校验手机号密码规则
     *
     * @param context 环境
     * @return true:符合规则
     */
    private boolean matchEdittext(Context context) {
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
        } else {
            return true;
        }
    }

    private void initRes(Context context) {
        color_checked = getResources().getColor(R.color.colorCompanyDark);
        color_unchecked = getResources().getColor(R.color.colorCompany);
        text_timeout = context.getString(R.string.register_timeout);
        text_success = context.getString(R.string.register_success);
    }

    /**
     * 退出当前界面
     */
    public void exit() {
        setVisibility(GONE);
        timer.stop();
        timer = null;
    }
}
