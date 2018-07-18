package xsda.xsda.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.zhy.android.percent.support.PercentRelativeLayout;

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

/**
 * Created by Administrator on 2018/7/8 0008.
 */

public class RegisterWidget extends RelativeLayout {

    private View rlRegisterAll;
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
    private WaitingWidget widget_waiting;

    private int color_checked;
    private int color_unchecked;
    private String text_timeout;
    private String text_frequently;
    private String text_user_exist;
    private String text_Verify_error;
    private String text_Verify_success;

    private EditText[] ets;
    private View[] lines;
    private String text_success;
    private VerifyCodeHelper verifyCodeHelper;
    private TimerHelper timer;
    private final long COUNTDOWN = 120;
    private long countdown = COUNTDOWN;// 默认间隔120秒才可重复获取验证码
    private long currentServerDate;// 服务器当前时间
    private long limitVerify = countdown * 1000;// 限制2分钟以内不可再点击


    private void initViews(Context context) {
        View.inflate(context, R.layout.widget_register, this);
        rlRegisterAll = findViewById(R.id.rl_register_all);
        rlRegisterAll.setOnClickListener(v -> Lgg.t(Cons.TAG).ii("click register empty"));
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
        tvRegisterGetVerifyCode = findViewById(R.id.tv_register_getVerifyCode);// 验证码按钮
        tvRegisterGetVerifyCode.setClickable(false);// 验证码设置默认不可点
        etRegisterInputVerifyCode = findViewById(R.id.et_register_input_verifyCode);
        vRegisterVerifyCodeLine = findViewById(R.id.v_register_verifyCode_line);
        tvRegisterCommit = findViewById(R.id.tv_register_commit);
        widget_waiting = findViewById(R.id.widget_waiting);

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
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            initRes(changedView.getContext());
            getServerDate(changedView.getContext());
        } else {
            initViews(changedView.getContext());
        }
    }

    /**
     * 获取服务器时间
     *
     * @param context
     */
    private void getServerDate(Context context) {
        GetServerDateHelper getServerDateHelper = new GetServerDateHelper();
        getServerDateHelper.setOnGetServerErrorListener(this::netErrorNext);
        getServerDateHelper.setOnGetServerDateLongSuccessListener(currentServerDate -> {
            // 得到服务器当前时间
            this.currentServerDate = currentServerDate;
            initViews(context);
            initEvent(context);
            initHelper(context);
            initFinishNext();
        });
        getServerDateHelper.get();
    }

    private void initHelper(Context context) {
        /* 获取验证码 */
        verifyCodeHelper = new VerifyCodeHelper();
        verifyCodeHelper.setOnGetServerDateErrorListener(e -> Tgg.show(context, text_timeout, 2000));
        verifyCodeHelper.setOnUserHadExistListener(() -> Tgg.show(context, text_user_exist, 2000));
        verifyCodeHelper.setOnGetVerifyErrorListener(e -> {
            if (e.getCode() == Egg.CANT_SEND_SMS_TOO_FREQUENTLY) {
                // 验证码获取频繁
                Tgg.show(context, text_frequently, 2000);
            } else {
                Tgg.show(context, text_timeout, 2000);
            }
        });
        verifyCodeHelper.setOnGetVerifySuccessListener(() -> {
            // 提示留意短信
            Tgg.show(context, text_success, 2000);
            // 显示倒计时
            Lgg.t(Cons.TAG).ii("begin to countdown 120s");
            countDown(context);
        });
        
        /* 提交验证码 */
        verifyCodeHelper.setOnCommitVerifyPrepareListener(() -> widget_waiting.setVisibleByAnim());
        verifyCodeHelper.setOnCommitVerifyAfterListener(() -> widget_waiting.setGone());
        verifyCodeHelper.setOnCommitVerifyErrorListener(e -> Tgg.show(context, text_Verify_error, 2000));
        verifyCodeHelper.setOnCommitVerifySuccessListener(() -> {
            // 提示验证成功
            Tgg.show(context, text_Verify_success, 2000);
            new Handler().postDelayed(this::exit, 1000);
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
                    ((Activity) context).runOnUiThread(() -> {
                        if (countdown >= 1) {
                            tvRegisterGetVerifyCode.setClickable(false);
                            tvRegisterGetVerifyCode.setTextColor(context.getResources().getColor(R.color.colorGrayDark));
                            tvRegisterGetVerifyCode.setText(String.valueOf(countdown-- + "s"));
                        } else {
                            tvRegisterGetVerifyCode.setClickable(true);
                            tvRegisterGetVerifyCode.setTextColor(context.getResources().getColor(R.color.colorCompanyDark));
                            tvRegisterGetVerifyCode.setText(context.getString(R.string.register_get_verifycode));
                            countdown = COUNTDOWN;
                            timer.stop();
                        }
                    });
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
                countdown = COUNTDOWN;
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
            Tgg.show(context, context.getString(R.string.register_time_error), 3000);
        }
    }

    private void initEvent(Context context) {
        // 获取验证码按钮的初始状态
        toGetVerifyInit(context);
        // E.设定焦点监听
        for (int i = 0; i < ets.length; i++) {
            int finalI = i;
            ets[i].setOnFocusChangeListener((v, hasFocus) -> {
                Lgg.t(Cons.TAG).ii("position: " + finalI + ";hasfocus: " + hasFocus);
                lines[finalI].setBackgroundColor(hasFocus ? color_checked : color_unchecked);
            });
        }

        // E.回退
        ivRegisterBack.setOnClickListener(v -> {
            setVisibility(GONE);
            clickbackNext();
        });

        // E.获取验证码
        tvRegisterGetVerifyCode.setOnClickListener(v -> {
            // 校验手机号和密码
            if (matchEdittext(context, false)) {
                // 符合规则的开始请求验证码
                String phoneNum = etRegisterInputUsername.getText().toString();
                String password = etRegisterInputPassword.getText().toString();
                Tgg.show(context, context.getString(R.string.register_begin2getverify_tip), 2000);
                verifyCodeHelper.getVerifyCode(phoneNum, password);
            }
        });

        // E.提交注册
        tvRegisterCommit.setOnClickListener(v -> {
            Lgg.t(Cons.TAG).ii("点击提交验证码");
            // 校验手机号密码验证码等等
            if (matchEdittext(context, true)) {
                String phoneName = etRegisterInputUsername.getText().toString();
                String verifyCode = etRegisterInputVerifyCode.getText().toString();
                verifyCodeHelper.commitVerifyCode(phoneName, verifyCode);
            }
        });
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

    private void initRes(Context context) {
        color_checked = getResources().getColor(R.color.colorCompanyDark);
        color_unchecked = getResources().getColor(R.color.colorCompany);
        text_timeout = context.getString(R.string.register_timeout);
        text_frequently = context.getString(R.string.register_frequently_tip);
        text_user_exist = context.getString(R.string.register_user_exist_tip);
        text_success = context.getString(R.string.register_success);
        text_Verify_error = context.getString(R.string.register_commit_verify_failed);
        text_Verify_success = context.getString(R.string.register_commit_verify_success);
    }

    /**
     * 退出当前界面
     */
    public void exit() {
        setVisibility(GONE);
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private OnClickBackListener onClickBackListener;

    // 接口OnClickBackListener
    public interface OnClickBackListener {
        void clickback();
    }

    // 对外方式setOnClickBackListener
    public void setOnClickBackListener(OnClickBackListener onClickBackListener) {
        this.onClickBackListener = onClickBackListener;
    }

    // 封装方法clickbackNext
    private void clickbackNext() {
        if (onClickBackListener != null) {
            onClickBackListener.clickback();
        }
    }

    private OnInitFinishListener onInitFinishListener;

    // 接口OnInitFinishListener
    public interface OnInitFinishListener {
        void initFinish();
    }

    // 对外方式setOnInitFinishListener
    public void setOnInitFinishListener(OnInitFinishListener onInitFinishListener) {
        this.onInitFinishListener = onInitFinishListener;
    }

    // 封装方法initFinishNext
    private void initFinishNext() {
        if (onInitFinishListener != null) {
            onInitFinishListener.initFinish();
        }
    }

    private OnNetErrorListener onNetErrorListener;

    // 接口OnNetErrorListener
    public interface OnNetErrorListener {
        void netError(AVException e);
    }

    // 对外方式setOnNetErrorListener
    public void setOnNetErrorListener(OnNetErrorListener onNetErrorListener) {
        this.onNetErrorListener = onNetErrorListener;
    }

    // 封装方法netErrorNext
    private void netErrorNext(AVException e) {
        if (onNetErrorListener != null) {
            onNetErrorListener.netError(e);
        }
    }
}
