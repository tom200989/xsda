package xsda.xsda.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xsda.xsda.R;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.widget.LoginWidget;
import xsda.xsda.widget.NetErrorWidget;
import xsda.xsda.widget.RegisterWidget;
import xsda.xsda.widget.WaitingWidget;

public class LoginActivity extends RootActivity {

    @Bind(R.id.widget_login)
    LoginWidget widgetLogin;
    @Bind(R.id.wideget_register)
    RegisterWidget widegetRegister;
    @Bind(R.id.widget_neterror)
    NetErrorWidget widgetNeterror;
    @Bind(R.id.widget_waiting)
    WaitingWidget widgetWaiting;

    private List<View> widgets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initViews();
        initEvent();
    }

    private void initViews() {
        widgets = new ArrayList<>();
        widgets.add(widgetLogin);
        widgets.add(widegetRegister);
        widgets.add(widgetNeterror);
        widgets.add(widgetWaiting);
    }

    private void initEvent() {
        // 点击注册按钮
        widgetLogin.setOnClickRegisterListener(() -> {
            widgetWaiting.setVisibleByAnim();
            new Handler().postDelayed(() -> toView(widegetRegister), 1000);
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
        for (View widget : widgets) {
            String clickViewName = clickView.getClass().getSimpleName();
            String widgetsName = widget.getClass().getSimpleName();
            boolean isCurrentView = clickViewName.equalsIgnoreCase(widgetsName);
            widget.setVisibility(isCurrentView ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (widgetLogin.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else if (widegetRegister.getVisibility() == View.VISIBLE) {
            toView(widgetLogin);
        } else if (widgetNeterror.getVisibility() == View.VISIBLE) {
            toView(widgetLogin);
        } else if (widgetWaiting.getVisibility() == View.VISIBLE) {
            Lgg.t(Cons.TAG).ii("waiting view showing");
        } else {
            super.onBackPressed();
        }
    }
}
