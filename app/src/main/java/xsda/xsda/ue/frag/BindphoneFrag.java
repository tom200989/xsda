package xsda.xsda.ue.frag;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;

import butterknife.Bind;
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

/*
 * Created by qianli.ma on 2018/9/25 0025.
 */
public class BindphoneFrag extends RootFrag {


    @Bind(R.id.iv_bindphone_back)
    ImageView ivBindphoneBack;// 返回
    @Bind(R.id.et_bindphone_phonenum)
    EditText etBindphonePhonenum;// 输入手机号码
    @Bind(R.id.et_bindphone_password)
    EditText etBindphonePassword;// 输入备用密码
    @Bind(R.id.et_bindphone_verify)
    EditText etBindphoneVerify;// 输入验证码
    @Bind(R.id.tv_bindphone_getVerify)
    TextView tvBindphoneGetVerify;// 获取验证码按钮
    @Bind(R.id.tv_bindphone_commit)
    TextView tvBindphoneCommit;// 提交验证码

    private long currentServerDate;
    private final long COUNTDOWN = 120;
    private long countdown = COUNTDOWN;// 默认间隔120秒才可重复获取验证码
    private long limitVerify = 120 * 1000;// 限制2分钟以内不可再点击
    private TimerHelper timer;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_bindphone;
    }

    @Override
    public boolean isReloadData() {
        return true;
    }

    @Override
    public void onNexts(Object o, View view, String s) {
        // 获取服务器时间并校验验证码按钮是否可点
        getCurrentServerDate();
        // 点击回退
        ivBindphoneBack.setOnClickListener(v -> exit());
        // 点击获取
        tvBindphoneGetVerify.setOnClickListener(v -> getVerifyCode());
        // 点击提交
        tvBindphoneCommit.setOnClickListener(v -> matchRule());
    }

    /**
     * 获取服务器时间并校验验证码按钮是否可点
     */
    private void getCurrentServerDate() {
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
        long lastServerDate = Sgg.getInstance(getActivitys()).getLong(Cons.SP_SERVER_DATE_FOR_BIND, 0);

        // 时间正常
        if (currentServerDate > lastServerDate) {
            // 2分钟已过
            if (currentServerDate - lastServerDate > limitVerify) {
                countdown = COUNTDOWN;
                tvBindphoneGetVerify.setClickable(true);
            } else {// 2分钟没过
                tvBindphoneGetVerify.setClickable(false);
                // 计算出剩余时间
                countdown = (limitVerify - (currentServerDate - lastServerDate)) / 1000;
                // 启动倒数定时器
                countDown();
            }
        } else {// 时间不正常(当前服务器时间 < 上次记录的时间)
            tvBindphoneGetVerify.setClickable(false);
            Tgg.show(activity, getString(R.string.register_time_error), 2500);
            exit();
        }
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
                    tvBindphoneGetVerify.setClickable(false);
                    tvBindphoneGetVerify.setTextColor(getResources().getColor(R.color.colorGrayDark));
                    tvBindphoneGetVerify.setText(String.valueOf(countdown-- + "s"));
                } else {
                    tvBindphoneGetVerify.setClickable(true);
                    tvBindphoneGetVerify.setTextColor(getResources().getColor(R.color.colorCompanyDark));
                    tvBindphoneGetVerify.setText(getString(R.string.register_get_verifycode));
                    countdown = COUNTDOWN;
                    timer.stop();
                }
            }
        };
        timer.start(0, 1000);
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode() {
        String phoneNum = etBindphonePhonenum.getText().toString();
        String password = etBindphonePassword.getText().toString();
        
        boolean isMatchPhoneNum = Ogg.matchPhoneReg(phoneNum);
        if (TextUtils.isEmpty(phoneNum)) {
            Tgg.show(activity, R.string.login_wechat_bindphone_phone_not_empty, 2500);
            return;
        }
        if (!isMatchPhoneNum) {
            Tgg.show(activity, R.string.register_username_tip, 2500);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Tgg.show(activity, R.string.login_wechat_bindphone_password, 2500);
            return;
        }
        if (password.length() < 8 | password.length() > 16) {
            Tgg.show(activity, R.string.register_password_tip, 2500);
            return;
        }

        /* 申请获取验证码 */
        VerifyCodeHelper verifyCodeHelper = new VerifyCodeHelper(getActivity());
        verifyCodeHelper.setOnGetServerDateErrorListener(e -> Tgg.show(activity, R.string.register_timeout, 2000));
        verifyCodeHelper.setOnUserHadExistListener(() -> Tgg.show(activity, R.string.register_user_exist_tip, 2000));
        verifyCodeHelper.setOnGetVerifyErrorListener(e -> {
            if (e.getCode() == Egg.CANT_SEND_SMS_TOO_FREQUENTLY) {
                // 验证码获取频繁
                Tgg.show(activity, R.string.register_frequently_tip, 2000);
            } else {
                Tgg.show(activity, R.string.register_timeout, 2000);
            }
        });
        verifyCodeHelper.setOnGetVerifySuccessListener(() -> {
            // 提示留意短信
            Tgg.show(activity, R.string.register_success, 2000);
            // 显示倒计时
            Lgg.t(Cons.TAG).ii("begin to countdown 120s");
            countDown();
        });

        verifyCodeHelper.getVerifyCode(phoneNum, password);
        // TODO: 2018/9/26 0026  
    }

    /**
     * 匹配是否编辑域是否符合
     */
    private void matchRule() {
        String phoneNum = etBindphonePhonenum.getText().toString();
        String password = etBindphonePassword.getText().toString();
        String verifyCode = etBindphoneVerify.getText().toString();
        boolean isMatchPhoneNum = Ogg.matchPhoneReg(phoneNum);
        // 判断手机号规则
        if (TextUtils.isEmpty(phoneNum)) {
            Tgg.show(activity, R.string.login_wechat_bindphone_phone_not_empty, 2500);
            return;
        }
        if (!isMatchPhoneNum) {
            Tgg.show(activity, R.string.register_username_tip, 2500);
            return;
        }
        // 备用密码规则
        if (TextUtils.isEmpty(password)) {
            Tgg.show(activity, R.string.login_wechat_bindphone_password, 2500);
            return;
        }
        if (password.length() < 8 | password.length() > 16) {
            Tgg.show(activity, R.string.register_password_tip, 2500);
            return;
        }
        // 验证码规则
        if (TextUtils.isEmpty(verifyCode)) {
            Tgg.show(activity, R.string.register_not_verify_empty, 2500);
            return;
        }
        if (verifyCode.length() != 6) {
            Tgg.show(activity, R.string.register_commit_verify_failed, 2500);
            return;
        }
        // TODO: 2018/9/26 0026  校验验证码
    }

    @Override
    public boolean onBackPresss() {
        // 返回登录界面
        exit();
        return true;
    }

    private void exit() {
        if (timer != null) {
            timer.stop();
        }
        toFrag(getClass(), LoginFrag.class, null, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.stop();
        }
    }
}
