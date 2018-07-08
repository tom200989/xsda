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
import xsda.xsda.utils.Ogg;
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
    private EditText[] ets;
    private View[] lines;

    private void initViews(Context context) {
        View.inflate(context, R.layout.widget_register, this);
        rlRegisterBanner = (PercentRelativeLayout) findViewById(R.id.rl_register_banner);
        ivRegisterBack = (ImageView) findViewById(R.id.iv_register_back);
        ivRegisterLogo = (ImageView) findViewById(R.id.iv_register_logo);
        rlRegisterUsername = (PercentRelativeLayout) findViewById(R.id.rl_register_username);
        ivRegisterUsernameLogo = (ImageView) findViewById(R.id.iv_register_username_logo);
        etRegisterInputUsername = (EditText) findViewById(R.id.et_register_input_username);
        vRegisterUsernameLine = findViewById(R.id.v_register_username_line);
        rlRegisterPassword = (PercentRelativeLayout) findViewById(R.id.rl_register_password);
        ivRegisterPasswordLogo = (ImageView) findViewById(R.id.iv_register_password_logo);
        etRegisterInputPassword = (EditText) findViewById(R.id.et_register_input_password);
        vRegisterPasswordLine = findViewById(R.id.v_register_password_line);
        rlRegisterConfirmPassword = (PercentRelativeLayout) findViewById(R.id.rl_register_confirmPassword);
        ivRegisterConfirmPasswordLogo = (ImageView) findViewById(R.id.iv_register_confirmPassword_logo);
        etRegisterInputConfirmPassword = (EditText) findViewById(R.id.et_register_input_confirmPassword);
        vRegisterConfirmPasswordLine = findViewById(R.id.v_register_confirmPassword_line);
        rlRegisterVerifyCode = (PercentRelativeLayout) findViewById(R.id.rl_register_verifyCode);
        ivRegisterVerifyCodeLogo = (ImageView) findViewById(R.id.iv_register_verifyCode_logo);
        tvRegisterGetVerifyCode = (TextView) findViewById(R.id.tv_register_getVerifyCode);
        etRegisterInputVerifyCode = (EditText) findViewById(R.id.et_register_input_verifyCode);
        vRegisterVerifyCodeLine = findViewById(R.id.v_register_verifyCode_line);
        tvRegisterCommit = (TextView) findViewById(R.id.tv_register_commit);
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
        initRes();
        initViews(context);
        initEvent(context);
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
                // TODO: 2018/7/8 0008 符合规则的开始请求验证码
            }
        });
        // 提交注册
        tvRegisterCommit.setOnClickListener(v -> {
            // TODO: 2018/7/8 0008  
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

    private void initRes() {
        color_checked = getResources().getColor(R.color.colorCompanyDark);
        color_unchecked = getResources().getColor(R.color.colorCompany);
    }
}
