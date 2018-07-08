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
    private TextView ivRegisterGetVerifyCode;
    private EditText etRegisterInputVerifyCode;
    private View vRegisterVerifyCodeLine;
    private TextView tvRegisterCommit;

    private int color_checked;
    private int color_unchecked;

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
        ivRegisterGetVerifyCode = (TextView) findViewById(R.id.iv_register_getVerifyCode);
        etRegisterInputVerifyCode = (EditText) findViewById(R.id.et_register_input_verifyCode);
        vRegisterVerifyCodeLine = findViewById(R.id.v_register_verifyCode_line);
        tvRegisterCommit = (TextView) findViewById(R.id.tv_register_commit);
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
        initEvent();
    }

    private void initEvent() {
        // TODO: 2018/7/8 0008  
    }

    private void initRes() {
        color_checked = getResources().getColor(R.color.colorCompanyDark);
        color_unchecked = getResources().getColor(R.color.colorCompany);
    }
}
