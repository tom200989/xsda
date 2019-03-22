package xsda.xsda.ue.frag;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.helper.GetServerDateHelper;
import xsda.xsda.helper.LoginOrOutHelper;
import xsda.xsda.helper.TimerHelper;
import xsda.xsda.helper.UserVerifyHelper;
import xsda.xsda.helper.VerifyCodeHelper;
import xsda.xsda.ue.activity.SplashActivity;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Egg;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.WaitingWidget;
import xsda.xsda.wxapi.WechatInfo;

/*
 * Created by qianli.ma on 2018/9/25 0025.
 */
public class BindphoneFrag extends RootFrag {


    @BindView(R.id.iv_bindphone_back)
    ImageView ivBindphoneBack;// 返回
    @BindView(R.id.et_bindphone_phonenum)
    EditText etBindphonePhonenum;// 输入手机号码
    @BindView(R.id.et_bindphone_password)
    EditText etBindphonePassword;// 输入备用密码
    @BindView(R.id.et_bindphone_verify)
    EditText etBindphoneVerify;// 输入验证码
    @BindView(R.id.tv_bindphone_getVerify)
    TextView tvBindphoneGetVerify;// 获取验证码按钮
    @BindView(R.id.tv_bindphone_commit)
    TextView tvBindphoneCommit;// 提交验证码
    @BindView(R.id.widget_waiting)
    WaitingWidget widgetWaiting;// 等待

    private long currentServerDate;
    private final long COUNTDOWN = 120;
    private long countdown = COUNTDOWN;// 默认间隔120秒才可重复获取验证码
    private long limitVerify = 120 * 1000;// 限制2分钟以内不可再点击
    private TimerHelper timer;
    private String nickname;
    private String openid;
    private int OP_MUST_BINDPHONE = -1;// 提示绑定手机
    private int OP_CAN_EXIT = 0;// 返回登录页
    private int current_state = OP_MUST_BINDPHONE;// 记录当前状态
    private String TAG = "BindphoneFrag";

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
        ivBindphoneBack.setOnClickListener(v -> exit(current_state));
        // 点击获取
        tvBindphoneGetVerify.setOnClickListener(v -> getVerifyCode());
        // 点击提交
        tvBindphoneCommit.setOnClickListener(v -> commitClick());
    }

    /**
     * 接收来自WXactivity的信息
     *
     * @param wechatInfo 微信用户信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getWechatInfo(WechatInfo wechatInfo) {
        nickname = wechatInfo.getNickname();
        openid = wechatInfo.getOpenid();
        Lgg.t(TAG).ii("nickname from bindphone: " + nickname);
        Lgg.t(TAG).ii("openid from bindphone: " + openid);
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
        long lastServerDate = Sgg.getInstance(activity).getLong(Cons.SP_SERVER_DATE_FOR_BIND, 0);

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
            exit(OP_CAN_EXIT);
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
        widgetWaiting.setVisibleByAnim();
        VerifyCodeHelper verifyCodeHelper = new VerifyCodeHelper(getActivity());
        verifyCodeHelper.setOnGetServerDateErrorListener(e -> {
            current_state = OP_CAN_EXIT;// 修改状态为「可退出」
            Tgg.show(activity, R.string.register_timeout, 2000);
        });
        verifyCodeHelper.setOnUserHadExistListener(() -> {
            current_state = OP_CAN_EXIT;// 修改状态为「可退出」
            Tgg.show(activity, R.string.register_user_exist_tip, 2000);
        });
        verifyCodeHelper.setOnGetVerifyErrorListener(e -> {
            current_state = OP_CAN_EXIT;// 修改状态为「可退出」
            if (e.getCode() == Egg.CANT_SEND_SMS_TOO_FREQUENTLY) {
                // 验证码获取频繁
                Tgg.show(activity, R.string.register_frequently_tip, 2000);
                exit(OP_CAN_EXIT);
            } else {
                Tgg.show(activity, R.string.register_timeout, 2000);
                exit(OP_CAN_EXIT);
            }
        });
        verifyCodeHelper.setOnGetVerifySuccessListener(() -> {
            // 提示留意短信
            Tgg.show(activity, R.string.register_success, 2000);
            // 显示倒计时
            Lgg.t(Cons.TAG).ii("begin to countdown 120s");
            countDown();
        });
        verifyCodeHelper.setOnCommitVerifyAfterListener(() -> widgetWaiting.setGone());
        verifyCodeHelper.getVerifyCode(phoneNum, password, nickname);
    }

    /**
     * 匹配是否编辑域是否符合
     */
    private void commitClick() {
        // 匹配规则
        if (!matchRule()) {
            return;
        }
        // 提交
        commit();
    }

    /**
     * 匹配规则
     *
     * @return 是否匹配
     */
    private boolean matchRule() {
        String phoneNum = etBindphonePhonenum.getText().toString();
        String password = etBindphonePassword.getText().toString();
        String verifyCode = etBindphoneVerify.getText().toString();
        boolean isMatchPhoneNum = Ogg.matchPhoneReg(phoneNum);
        // 判断手机号规则
        if (TextUtils.isEmpty(phoneNum)) {
            Tgg.show(activity, R.string.login_wechat_bindphone_phone_not_empty, 2500);
            return false;
        }
        if (!isMatchPhoneNum) {
            Tgg.show(activity, R.string.register_username_tip, 2500);
            return false;
        }
        // 备用密码规则
        if (TextUtils.isEmpty(password)) {
            Tgg.show(activity, R.string.login_wechat_bindphone_password, 2500);
            return false;
        }
        if (password.length() < 8 | password.length() > 16) {
            Tgg.show(activity, R.string.register_password_tip, 2500);
            return false;
        }
        // 验证码规则
        if (TextUtils.isEmpty(verifyCode)) {
            Tgg.show(activity, R.string.register_not_verify_empty, 2500);
            return false;
        }
        if (verifyCode.length() != 6) {
            Tgg.show(activity, R.string.register_commit_verify_failed, 2500);
            return false;
        }
        return true;
    }

    /**
     * 点击提交
     */
    private void commit() {
        Ogg.hideKeyBoard(activity);// 隐藏键盘
        if (matchEdittext(activity, true)) {
            String phoneName = etBindphonePhonenum.getText().toString();
            String password = etBindphonePassword.getText().toString();
            String verifyCode = etBindphoneVerify.getText().toString();
            if (currentServerDate != -1) {
                /* 提交验证码 */
                VerifyCodeHelper verifyCodeHelper = new VerifyCodeHelper(getActivity());
                verifyCodeHelper.setOnCommitVerifyPrepareListener(() -> widgetWaiting.setVisibleByAnim());
                verifyCodeHelper.setOnCommitVerifyAfterListener(() -> widgetWaiting.setGone());
                verifyCodeHelper.setOnCommitVerifyErrorListener(e -> {
                    current_state = OP_CAN_EXIT;// 修改状态为「可修改」
                    Tgg.show(activity, R.string.register_commit_verify_failed, 2000);
                });
                verifyCodeHelper.setOnCommitVerifySuccessListener(() -> {
                    // 提示验证成功
                    Tgg.show(activity, R.string.register_commit_verify_success, 2500);
                    new Handler().postDelayed(() -> {
                        // 保存登陆信息到本地
                        Ogg.saveLoginJson(activity, phoneName, password, true);
                        // 保存openid到数据库
                        saveOpenId();
                        // 直接登陆
                        toLogin(phoneName, password);
                    }, 1000);
                });
                verifyCodeHelper.commitVerifyCode(phoneName, password, verifyCode);
            }
        }
    }

    /**
     * 保存openid到数据库
     */
    private void saveOpenId() {
        if (!TextUtils.isEmpty(openid)) {
            String phoneName = etBindphonePhonenum.getText().toString();
            UserVerifyHelper userVerifyHelper = new UserVerifyHelper(activity);
            userVerifyHelper.setOnExceptionListener(e -> Tgg.show(activity, R.string.login_wechat_authorized_bindphone_openid_failed, 2500));
            userVerifyHelper.setOnUserNotExistListener(() -> Tgg.show(activity, R.string.login_wechat_authorized_bindphone_openid_failed, 2500));
            userVerifyHelper.saveOpenId(phoneName, openid);
        }
    }

    /**
     * 去登陆
     *
     * @param phoneNum 手机号
     * @param password 密码
     */
    private void toLogin(String phoneNum, String password) {
        LoginOrOutHelper loginHelper = new LoginOrOutHelper(getActivity());
        loginHelper.setOnLoginPrepareListener(() -> widgetWaiting.setVisibleText(getString(R.string.logining)));
        loginHelper.setOnLoginAfterListener(() -> widgetWaiting.setGone());
        loginHelper.setOnLoginErrorListener(ex -> {
            current_state = OP_CAN_EXIT;// 修改状态为「可修改」
            Tgg.show(getActivity(), R.string.login_failed, 2500);
            Ogg.saveLoginJson(activity, "", "", false);
            exit(OP_CAN_EXIT);
        });
        loginHelper.setOnLoginUserNotExistListener(() -> {
            current_state = OP_CAN_EXIT;// 修改状态为「可修改」
            Tgg.show(getActivity(), R.string.login_user_not_exist, 2500);
        });
        loginHelper.setOnLoginSuccessListener(avUser -> {
            // 保存用户对象以及即时通讯对象
            ((SplashActivity) getActivity()).avUser = avUser;
            // 提示
            Tgg.show(getActivity(), R.string.login_success, 2500);
            // 保存用户信息到临时集合
            Ogg.saveLoginJson(activity, phoneNum, password, true);
            // 封装数据并跳转
            Ogg.hideKeyBoard(getActivity());
            toFrag(getClass(), MainFrag.class, null, false);
            Lgg.t(Cons.TAG).ii("login success to main fragment");
        });
        loginHelper.login(phoneNum, password);
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
        if (!Ogg.matchPhoneReg(etBindphonePhonenum.getText().toString())) {
            etBindphonePhonenum.requestFocus();
            Tgg.show(context, context.getString(R.string.register_username_tip), 2000);
            return false;
        } else if (etBindphonePassword.getText().toString().length() < 8 // 密码位数
                           | etBindphonePassword.getText().toString().length() > 16) {// 密码位数
            etBindphonePassword.requestFocus();
            Tgg.show(context, context.getString(R.string.register_password_tip), 2000);
            return false;
        } else if (TextUtils.isEmpty(etBindphoneVerify.getText().toString()) & isVerify) {
            etBindphoneVerify.requestFocus();
            Tgg.show(context, context.getString(R.string.register_not_verify_empty), 2000);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onBackPresss() {
        // 返回登录界面
        exit(OP_MUST_BINDPHONE);
        return true;
    }

    /**
     * 退出
     *
     * @param tag 标记位
     */
    private void exit(int tag) {
        if (timer != null) {
            timer.stop();
        }

        // 强制绑定手机提示
        if (tag == OP_MUST_BINDPHONE) {
            Tgg.show(activity, R.string.login_wechat_authorized_bindphone_tip, 2500);
            return;
        }

        // 退回登录页
        if (tag == OP_CAN_EXIT) {
            toFrag(getClass(), LoginFrag.class, null, true);
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.stop();
        }
    }
}
