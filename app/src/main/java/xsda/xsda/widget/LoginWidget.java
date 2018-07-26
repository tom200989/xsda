package xsda.xsda.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.john.waveview.WaveView;
import com.zhy.android.percent.support.PercentRelativeLayout;

import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/7/6 0006.
 */

public class LoginWidget extends RelativeLayout {

    private PercentRelativeLayout rlUserName;
    private EditText etUserName;
    private View vUserNameLine;
    private PercentRelativeLayout rlPassword;
    private EditText etPassword;
    private ImageView ivEye;
    private View vPasswordLine;
    private ImageView ivWechat;
    private WaveView wvLogin;
    private TextView tvLoginClick;
    private TextView tvRegisterClick;
    private TextView tvForgot;

    private int colorFocus;
    private int colorUnFocus;
    private Drawable visibleEye;
    private Drawable unVisibleEye;

    public LoginWidget(Context context) {
        this(context, null, 0);
    }

    public LoginWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoginWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRes();
        initView(context);
        initEvent();
    }

    private void initRes() {
        colorFocus = getResources().getColor(R.color.colorCompanyDark);
        colorUnFocus = getResources().getColor(R.color.colorCompany);
        visibleEye = getResources().getDrawable(R.drawable.visible_eye);
        unVisibleEye = getResources().getDrawable(R.drawable.unvisible_eye);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.widget_login, this);
        rlUserName = findViewById(R.id.rl_login_input_username);
        etUserName = findViewById(R.id.et_login_input_username);
        vUserNameLine = findViewById(R.id.v_login_input_username_line);
        rlPassword = findViewById(R.id.rl_login_input_password);
        etPassword = findViewById(R.id.et_login_input_password);
        ivEye = findViewById(R.id.iv_login_eye);
        vPasswordLine = findViewById(R.id.v_login_input_password_line);
        ivWechat = findViewById(R.id.iv_login_wechat);
        wvLogin = findViewById(R.id.wv_login);
        tvLoginClick = findViewById(R.id.tv_login_click);
        tvRegisterClick = findViewById(R.id.tv_login_register);
        tvForgot = findViewById(R.id.tv_login_forgot);
    }

    private void initEvent() {
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
    }

    /**
     * 注册
     */
    private void register() {
        clickRegisterNext();
    }

    /**
     * 微信登陆
     */
    public void weChat() {
        // TODO: 2018/7/6 0006  微信登陆逻辑
    }

    /**
     * 普通登陆
     */
    public void normalLogin() {
        // TODO: 2018/7/6 0006  常规登陆逻辑
    }

    /**
     * 忘记密码
     */
    public void forgotPassword() {
        // TODO: 2018/7/8 0008  忘记密码逻辑
    }

    private OnClickRegisterListener onClickRegisterListener;

    // 接口OnClickRegisterListener
    public interface OnClickRegisterListener {
        void clickRegister();
    }

    // 对外方式setOnClickRegisterListener
    public void setOnClickRegisterListener(OnClickRegisterListener onClickRegisterListener) {
        this.onClickRegisterListener = onClickRegisterListener;
    }

    // 封装方法clickRegisterNext
    private void clickRegisterNext() {
        if (onClickRegisterListener != null) {
            onClickRegisterListener.clickRegister();
        }
    }

}
