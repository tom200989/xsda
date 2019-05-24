package xsda.xsda.ue.frag;

import android.Manifest;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.hiber.bean.PermissBean;
import com.hiber.bean.StringBean;
import com.hiber.hiber.RootFrag;
import com.qianli.NumberProgressBar;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.bean.LoginBean;
import xsda.xsda.bean.UpdateBean;
import xsda.xsda.helper.GetServerDateHelper;
import xsda.xsda.helper.GetUpdateHelper;
import xsda.xsda.helper.LoginOrOutHelper;
import xsda.xsda.helper.PingHelper;
import xsda.xsda.ue.activity.BaseActivity;
import xsda.xsda.ue.activity.MainActivity;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;
import xsda.xsda.utils.Tgg;
import xsda.xsda.widget.NoPassPermissWidget;
import xsda.xsda.widget.WaitingWidget;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class SplashFrag extends RootFrag {

    @BindView(R.id.iv_splash_logo)
    ImageView ivSplashLogo;// 图标
    @BindView(R.id.iv_splash_name)
    TextView ivSplashName;// 标题
    @BindView(R.id.tv_splash_copyright)
    TextView tvSplashCopyright;// 版权
    @BindView(R.id.pg_splash_loagding)
    NumberProgressBar pgSplashLoagding;// 进度条
    @BindView(R.id.tv_splash_loading_text)
    TextView tvSplashLoadingText;// 进度文本
    @BindView(R.id.wd_splash_wait)
    WaitingWidget widgetLoginWaitting;// 等待面板
    @BindView(R.id.wd_splash_no_pass_permiss)
    NoPassPermissWidget wdSplashNoPassPermiss;// 权限未通过面板

    public static String DEFAULT_TEXT = "";
    private String check_net;
    private String loading_text;
    private String loading_success;
    private UpdateBean updateBean;
    private Handler handler;

    private static String[] permissions = {// 填写需要申请的权限
            Manifest.permission.READ_EXTERNAL_STORAGE,// 读取外部存储
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入外部存储
            Manifest.permission.READ_PHONE_STATE// 电话状态
            // .... 需要什么权限, 需要先声明 ....
            // 注意: 非危险权限不需要申请, 一定不能加进来, 否则影响业务逻辑
    };

    @Override
    public int onInflateLayout() {
        handler = new Handler();
        return R.layout.frag_splash;
    }

    @Override
    public void initViewFinish(View inflateView) {

    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        initRes();
        handler.postDelayed(this::ping, 2000);
    }

    @Override
    public String[] initPermissed() {
        setPermissedListener((isAllPass, strings) -> {
            // 弹出提示面板进行提示
            wdSplashNoPassPermiss.setVisibility(View.VISIBLE);
        });
        // 设置权限提醒
        setPermissBean(setPermissContent());
        return permissions;
    }

    /**
     * 设置权限提醒
     *
     * @return 权限permissbean
     */
    private PermissBean setPermissContent() {
        PermissBean permissBean = new PermissBean();
        StringBean stringBean = new StringBean();
        stringBean.setTitle(getString(R.string.splash_permiss_tip_title));
        stringBean.setContent(getString(R.string.splash_permiss_tip_content));
        stringBean.setCancel(getString(R.string.splash_permiss_tip_cancel));
        stringBean.setOk(getString(R.string.splash_permiss_tip_ok));
        stringBean.setColorTitle(getRootColor(R.color.colorCompanyDark));
        stringBean.setColorContent(getRootColor(R.color.colorCompany));
        stringBean.setColorCancel(getRootColor(R.color.colorCompany));
        stringBean.setColorOk(getRootColor(R.color.colorCompanyDark));
        permissBean.setPermissView(null);
        permissBean.setStringBean(stringBean);
        return permissBean;
    }

    @Override
    public boolean onBackPresss() {
        if (wdSplashNoPassPermiss.getVisibility() == View.VISIBLE) {
            Lgg.t(Cons.TAG).ii("splash click no pass permiss backpress");
        } else {
            kill();
        }
        return true;
    }

    /**
     * 初始化资源
     */
    private void initRes() {
        check_net = getString(R.string.check_net);
        loading_text = getString(R.string.loading_text);
        loading_success = getString(R.string.loading_success);
    }

    private void ping() {
        // ping指定地址
        PingHelper pingHelper = new PingHelper();
        pingHelper.setOnPingSuccessListener(msg -> {
            setLoadingText(loading_success);
            // 获取上次用户点了取消的时间
            GetServerDateHelper getServerDateHelper = new GetServerDateHelper();
            getServerDateHelper.setOnGetServerErrorListener(e -> checkUpdate());
            getServerDateHelper.setOnGetServerDateLongSuccessListener(currentTime -> {
                long lastTime = Sgg.getInstance(activity).getLong(Cons.SP_LAST_CANCEL_UPDATE_DATE, 0);
                if (lastTime >= currentTime) {/* 时间被修改过 */
                    checkUpdate();// 检测是否有新版本
                } else if (currentTime - lastTime > 24 * 60 * 60 * 1000) {/* 距离上次点击取消超过24小时 */
                    checkUpdate();// 检测是否有新版本
                } else {
                    toGuideOrLogin();
                }
            });
            getServerDateHelper.get();
        });

        pingHelper.setOnProgressListener(progress -> {
            // 显示进度
            if (progress <= 50) {
                setLoadingText(check_net);
            } else if (progress <= 75) {
                setLoadingText(loading_text);
            } else {
                setLoadingText(loading_success);
            }
            setProgress(progress);
        });

        pingHelper.setOnPingFailedListener(msg -> showErrorNet());
        pingHelper.ping(activity, getString(R.string.ping_address_backup));
    }

    /**
     * 检查新版本
     */
    private void checkUpdate() {

        // 1.获取当前运行APP的版本号
        int currentVersion = Ogg.getLocalVersion(activity);
        // 2.请求LeanClound最新版本
        GetUpdateHelper getUpdateHelper = new GetUpdateHelper();
        getUpdateHelper.setOnGetUpdateListener(updateBean -> {
            // 2.0.全域设置
            this.updateBean = updateBean;
            // 2.1.获取到最新的版本号
            int newVersion = Integer.valueOf(updateBean.getNewVersionCode());
            // 3.有更新版本
            if (newVersion > currentVersion) {
                if (newVersion - currentVersion <= 2) {/* 如果新版本与旧版本的差异在2个版本以内--> 则弹出界面让用户自由选择 */
                    // 切换到下载界面fragment
                    toFrag(getClass(), UpdateFrag.class, updateBean, false);
                } else {/* 如果已经超出2个版本的差异--> 强制跳转到下载界面 */
                    toFrag(getClass(), DownFrag.class, updateBean, true);
                }

            } else {
                // 3.没有新版本--> 切换到下个界面
                toGuideOrLogin();
            }
        });
        getUpdateHelper.setOnExceptionListener(e -> {
            showErrorNet();
            Lgg.t(Cons.TAG).ee("initCheckUpdateLogic() --> error");
        });
        getUpdateHelper.getNewVersion();
    }

    /**
     * 前往错误界面
     */
    private void showErrorNet() {
        toFrag(getClass(), NetErrFrag.class, null, false);
    }

    /**
     * 向导页or登录页
     */
    private void toGuideOrLogin() {
        if (Sgg.getInstance(activity).getBoolean(Cons.SP_GUIDE, false)) {

            // 自动登陆判断: 如果有选择了自动登录, 则直接跳转主页 
            boolean canAutoLogin = Ogg.isCanAutoLogin(activity);
            if (canAutoLogin) {
                if (AVUser.getCurrentUser() == null) {// 没有登陆过
                    // 调用登陆接口
                    toLogin();
                } else {
                    /* 这里一定记得把user信息保存到全域 */
                    BaseActivity.avUser = AVUser.getCurrentUser();
                    // 登陆过直接跳到主页
                    // toFrag(getClass(), MainFrag.class, null, true);
                    toFragActivity(getClass(), MainActivity.class, MainFrag.class, null, true, false, 0);
                }

            } else {
                // 进入登录页 
                toFrag(getClass(), LoginFrag.class, null, false);
            }
        } else {
            // 进入向导页
            toFrag(getClass(), GuideFrag.class, null, false);
        }
    }

    /**
     * 执行登陆操作
     */
    private void toLogin() {

        LoginBean loginBean = Ogg.readLoginJson(activity);
        String phoneNum = loginBean.getPhoneNum();
        String password = loginBean.getPassword();

        LoginOrOutHelper loginHelper = new LoginOrOutHelper(activity);
        loginHelper.setOnLoginPrepareListener(() -> widgetLoginWaitting.setDescritionText(getString(R.string.logining)));
        loginHelper.setOnLoginAfterListener(() -> widgetLoginWaitting.setGone());
        loginHelper.setOnLoginErrorListener(ex -> Tgg.show(activity, R.string.login_failed, 2500));
        loginHelper.setOnLoginUserNotExistListener(() -> Tgg.show(activity, R.string.login_user_not_exist, 2500));
        loginHelper.setOnLoginSuccessListener(avUser -> {
            // 保存用户对象以及即时通讯对象
            BaseActivity.avUser = avUser;
            // 隐藏软键盘
            Ogg.hideKeyBoard(activity);
            // toFrag(getClass(), MainFrag.class, null, false);
            toFragActivity(getClass(), MainActivity.class, MainFrag.class, null, false, false, 0);
            Lgg.t(Cons.TAG).ii("login success to main fragment");
        });
        loginHelper.login(phoneNum, password);
    }

    /**
     * 设置进度条
     *
     * @param progress 进度
     */
    public void setProgress(int progress) {
        if (progress > 100) {
            progress = 100;
        } else if (progress <= 0) {
            progress = 10;
        }
        pgSplashLoagding.setProgress(progress);
    }

    /**
     * 设置正在加载中的提示语
     *
     * @param text 提示文本
     */
    public void setLoadingText(String text) {
        tvSplashLoadingText.setText(TextUtils.isEmpty(text) ? DEFAULT_TEXT : text);
    }
}
