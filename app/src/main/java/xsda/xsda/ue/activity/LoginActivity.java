package xsda.xsda.ue.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xsda.xsda.R;
import xsda.xsda.ue.root.RootActivity;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.widget.LoginWidget;
import xsda.xsda.widget.NetErrorWidget;
import xsda.xsda.widget.RegisterWidget;
import xsda.xsda.widget.WaitingWidget;

public class LoginActivity extends RootActivity {

    @Bind(R.id.rl_login_activity)
    RelativeLayout rlLoginActivity;

    private List<View> widgets;
    private LoginWidget widgetLogin;
    private RegisterWidget widegetRegister;
    private NetErrorWidget widgetNeterror;
    private WaitingWidget widgetWaiting;

    @Override
    public int onCreateLayout() {
        return R.layout.activity_login;
    }

    @Override
    public String NoSaveInstanceStateActivityName() {
        return null;
    }

    @Override
    public int onCreateContain() {
        return 0;
    }

    @Override
    public Class onCreateFirstFragment() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // initViews();
        // initEvent();
    }

    private void initViews() {
        // 1.创建视图
        widgetLogin = new LoginWidget(this);
        widegetRegister = new RegisterWidget(this);
        widgetNeterror = new NetErrorWidget(this);
        widgetWaiting = new WaitingWidget(this);

        // 2.设置全屏
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-1, -1);
        widgetLogin.setLayoutParams(lp);
        widegetRegister.setLayoutParams(lp);
        widgetNeterror.setLayoutParams(lp);
        widgetWaiting.setLayoutParams(lp);

        // 3.加到集合
        widgets = new ArrayList<>();
        widgets.add(widgetLogin);
        widgets.add(widegetRegister);
        widgets.add(widgetNeterror);
        widgets.add(widgetWaiting);

        // 4.初始化首屏视图
        rlLoginActivity.addView(widgets.get(0));
    }

    private void initEvent() {
        // 点击注册按钮
        widgetLogin.setOnClickRegisterListener(() -> {
            widgetWaiting.setVisibleByAnim();
            new Handler().postDelayed(() -> toView(widegetRegister), 0);
        });

        // 注册界面初始化完成
        widegetRegister.setOnInitFinishListener(() -> widgetWaiting.setGone());
        // 注册界面回退
        widegetRegister.setOnClickBackListener(() -> toView(widgetLogin));
        // 注册界面断网
        widegetRegister.setOnNetErrorListener(e -> toView(widgetNeterror));

        // 断网界面点击重试
        widgetNeterror.setOnNetErrorRetryListener(() -> toView(widgetLogin));
        // 断网界面点击回退
        widgetNeterror.setOnNetErrorBackListener(() -> toView(widgetLogin));
    }

    /**
     * 根据传递对象进行切换视图
     *
     * @param clickView 目标视图对象
     */
    private void toView(View clickView) {
        clickView.setVisibility(View.VISIBLE);
        Lgg.t(Cons.TAG).ii("click view: " + clickView.getClass().getSimpleName());
        rlLoginActivity.removeAllViews();
        try {
            RegisterWidget registerWidget = new RegisterWidget(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-1, -1);
            registerWidget.setLayoutParams(lp);
            rlLoginActivity.addView(registerWidget);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // for (View widget : widgets) {
        //     String clickViewName = clickView.getClass().getSimpleName();
        //     String widgetsName = widget.getClass().getSimpleName();
        //     boolean isCurrentView = clickViewName.equalsIgnoreCase(widgetsName);
        //     // widget.setVisibility(isCurrentView ? View.VISIBLE : View.GONE);
        //     if (isCurrentView) {
        //         rlLoginActivity.removeAllViews();
        //         rlLoginActivity.addView(widget);
        //         break;
        //     }
        //
        // }
    }

    // @Override
    // public void onBackPressed() {
    //     if (widgetLogin.getVisibility() == View.VISIBLE) {
    //         super.onBackPressed();
    //     } else if (widegetRegister.getVisibility() == View.VISIBLE) {
    //         toView(widgetLogin);
    //     } else if (widgetNeterror.getVisibility() == View.VISIBLE) {
    //         toView(widgetLogin);
    //     } else if (widgetWaiting.getVisibility() == View.VISIBLE) {
    //         Lgg.t(Cons.TAG).ii("waiting view showing");
    //     } else {
    //         super.onBackPressed();
    //     }
    // }
}
