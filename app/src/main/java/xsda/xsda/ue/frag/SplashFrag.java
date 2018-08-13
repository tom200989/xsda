package xsda.xsda.ue.frag;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import butterknife.Bind;
import xsda.xsda.R;
import xsda.xsda.bean.UpdateBean;
import xsda.xsda.helper.GetServerDateHelper;
import xsda.xsda.helper.GetUpdateHelper;
import xsda.xsda.helper.PingHelper;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;
import xsda.xsda.utils.Sgg;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class SplashFrag extends BaseFrag {


    @Bind(R.id.iv_splash_logo)
    ImageView ivSplashLogo;// 图标
    @Bind(R.id.iv_splash_name)
    TextView ivSplashName;// 标题
    @Bind(R.id.tv_splash_copyright)
    TextView tvSplashCopyright;// 版权
    @Bind(R.id.pg_splash_loagding)
    NumberProgressBar pgSplashLoagding;// 进度条
    @Bind(R.id.tv_splash_loading_text)
    TextView tvSplashLoadingText;// 进度文本

    public static String DEFAULT_TEXT = "";
    private String check_net;
    private String loading_text;
    private String loading_success;
    private UpdateBean updateBean;
    private Handler handler;

    @Override
    public int onInflateLayout() {
        handler = new Handler();
        return R.layout.frag_splash;
    }

    @Override
    public void initViewFinish() {
        initRes();
        handler.postDelayed(this::ping, 2000);
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {

    }

    @Override
    public boolean onBackPresss() {
        kill();
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
                long lastTime = Sgg.getInstance(getActivity()).getLong(Cons.SP_LAST_CANCEL_UPDATE_DATE, 0);
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
            } else if (progress > 50 & progress <= 75) {
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
            // 进入登录页 
            toFrag(getClass(), LoginFrag.class, null, false);
        } else {
            // 进入向导页
            toFrag(getClass(), GuideFrag.class, null, false);
        }
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
