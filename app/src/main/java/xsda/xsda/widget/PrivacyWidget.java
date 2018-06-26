package xsda.xsda.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.OnClick;
import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/6/21 0021.
 */

public class PrivacyWidget extends RelativeLayout {

    PercentRelativeLayout rlSplashDataPrivacy;// 隐私条款总布局
    TextView tvSplashPrivacyTitle;// 标题头
    ImageView ivSplashPrivacyBack;// 返回键(点击Html后出现)
    TextView tvSplashPrivacyDes;// 描述
    PercentRelativeLayout rlSplashDataPrivacyCheckbox;// 勾选框总布局
    ImageView ivSplashCheckbox;// 勾选框
    TextView tvSplashClickReadPrivacy;// 跳转到隐私html
    TextView tvSplashPrivacyUnAgree;// 不同意
    TextView tvSplashPrivacyAgree;// 同意
    WebView wvSplashPrivacy;// 隐私条款HTML

    public Drawable privacy_checkbox_checked;// 隐私条款勾选
    public Drawable privacy_checkbox_uncheck;// 隐私条款不勾选
    private int colorGrayDark;// 灰色
    private int colorCompanyDark;// 深色
    private WebSettings wv_setting;

    public PrivacyWidget(Context context) {
        this(context, null, 0);
    }

    public PrivacyWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrivacyWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化资源
        initRes(context);
        // 加载控件视图
        initView(context);
    }

    /**
     * 加载控件视图
     *
     * @param context
     */
    private void initView(Context context) {
        View.inflate(context, R.layout.widget_privacy, this);
        rlSplashDataPrivacy = findViewById(R.id.rl_splash_data_Privacy);
        tvSplashPrivacyTitle = findViewById(R.id.tv_splash_privacy_title);
        ivSplashPrivacyBack = findViewById(R.id.iv_splash_privacy_back);
        tvSplashPrivacyDes = findViewById(R.id.tv_splash_privacy_des);
        rlSplashDataPrivacyCheckbox = findViewById(R.id.rl_splash_data_privacy_checkbox);
        ivSplashCheckbox = findViewById(R.id.iv_splash_checkbox);
        tvSplashClickReadPrivacy = findViewById(R.id.tv_splash_click_read_privacy);
        tvSplashPrivacyUnAgree = findViewById(R.id.tv_splash_privacy_unAgree);
        tvSplashPrivacyAgree = findViewById(R.id.tv_splash_privacy_agree);
        wvSplashPrivacy = findViewById(R.id.wv_splash_privacy);
        rlSplashDataPrivacy.setOnClickListener(this::onViewClicked);
        ivSplashPrivacyBack.setOnClickListener(this::onViewClicked);
        rlSplashDataPrivacyCheckbox.setOnClickListener(this::onViewClicked);
        tvSplashClickReadPrivacy.setOnClickListener(this::onViewClicked);
        tvSplashPrivacyUnAgree.setOnClickListener(this::onViewClicked);
        tvSplashPrivacyAgree.setOnClickListener(this::onViewClicked);
    }

    /**
     * 初始化资源
     */
    private void initRes(Context context) {
        privacy_checkbox_checked = getResources().getDrawable(R.drawable.privacy_checkbox_checked);
        privacy_checkbox_uncheck = getResources().getDrawable(R.drawable.privacy_checkbox_uncheck);
        colorGrayDark = context.getResources().getColor(R.color.colorGrayDark);
        colorCompanyDark = context.getResources().getColor(R.color.colorCompanyDark);
    }

    /**
     * 加载webview数据
     */
    private void loadPrivacyHtml() {
        wvSplashPrivacy.loadUrl("file:///android_res/" + "raw/privacy.html");
        // 4.获取wb的设置对象
        wv_setting = wvSplashPrivacy.getSettings();
        // 5.设置js效果(此处为false:设置该步会影响webview加载完成的监听, 建议无特殊效果要求不要使用)
        wv_setting.setJavaScriptEnabled(false);
        //  6.设置类似于地图的缩放触碰块效果(此处为false:会影响用户体验, 一般设置双击放大即可)
        wv_setting.setBuiltInZoomControls(false);
        //  7.设置双击放大缩小(此处为false)
        wv_setting.setUseWideViewPort(false);
        // 8.设置webview自适应屏幕
        wv_setting.setLoadWithOverviewMode(true);
        // 9.设置webview布局规则
        wv_setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 10.设置适应Html5
        wv_setting.setDomStorageEnabled(true);
    }

    @OnClick({R.id.rl_splash_data_Privacy,// 隐私条款总布局
                     R.id.iv_splash_privacy_back,// 点击html后的回退键
                     R.id.rl_splash_data_privacy_checkbox,// 勾选框布局
                     R.id.tv_splash_click_read_privacy,// 跳转到隐私Html
                     R.id.tv_splash_privacy_unAgree,// 不同意
                     R.id.tv_splash_privacy_agree})// 同意
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_splash_data_Privacy:// 隐私条款总布局
                break;
            case R.id.iv_splash_privacy_back:// 点击html后的回退键
                ivSplashPrivacyBack.setVisibility(GONE);
                wvSplashPrivacy.setVisibility(INVISIBLE);
                break;
            case R.id.rl_splash_data_privacy_checkbox:// 勾选框布局
                clickCheckBox();
                break;
            case R.id.tv_splash_click_read_privacy:// 跳转到隐私Html
                ivSplashPrivacyBack.setVisibility(VISIBLE);
                wvSplashPrivacy.setVisibility(VISIBLE);
                // 加载html数据
                loadPrivacyHtml();
                break;
            case R.id.tv_splash_privacy_unAgree:// 不同意
                clickUnAgree();
                break;
            case R.id.tv_splash_privacy_agree:// 同意
                clickAgree();
                break;
        }
    }

    /**
     * 点击同意
     */
    private void clickAgree() {
        setVisibility(GONE);
        // 判断用户是否勾选了隐私同意条例
        if (ivSplashCheckbox.getDrawable() == privacy_checkbox_checked) {
            clickAgreeNext();
        }
    }

    /**
     * 点击不同意
     */
    private void clickUnAgree() {
        setVisibility(GONE);
        clickNotAgreeNext();
        Process.killProcess(Process.myPid());
    }

    /**
     * 点击checkbox
     */
    private void clickCheckBox() {
        // 切换勾选与非勾选
        boolean isCheckLast = ivSplashCheckbox.getDrawable() == privacy_checkbox_checked;
        ivSplashCheckbox.setImageDrawable(isCheckLast ? privacy_checkbox_uncheck : privacy_checkbox_checked);
        // 切换[同意]按钮样式
        boolean isCheckNow = ivSplashCheckbox.getDrawable() == privacy_checkbox_checked;
        tvSplashPrivacyAgree.setTextColor(isCheckNow ? colorCompanyDark : colorGrayDark);
    }

    private OnClickNotAgreeListener onClickNotAgreeListener;

    // 接口OnClickNotAgreeListener
    public interface OnClickNotAgreeListener {
        void clickNotAgree();
    }

    // 对外方式setOnClickNotAgreeListener
    public void setOnClickNotAgreeListener(OnClickNotAgreeListener onClickNotAgreeListener) {
        this.onClickNotAgreeListener = onClickNotAgreeListener;
    }

    // 封装方法clickNotAgreeNext
    private void clickNotAgreeNext() {
        if (onClickNotAgreeListener != null) {
            onClickNotAgreeListener.clickNotAgree();
        }
    }

    private OnClickAgreeListener onClickAgreeListener;

    // 接口OnClickAgreeListener
    public interface OnClickAgreeListener {
        void clickAgree();
    }

    // 对外方式setOnClickAgreeListener
    public void setOnClickAgreeListener(OnClickAgreeListener onClickAgreeListener) {
        this.onClickAgreeListener = onClickAgreeListener;
    }

    // 封装方法clickAgreeNext
    private void clickAgreeNext() {
        if (onClickAgreeListener != null) {
            onClickAgreeListener.clickAgree();
        }
    }
}
