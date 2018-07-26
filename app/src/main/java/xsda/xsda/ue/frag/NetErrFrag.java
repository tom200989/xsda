package xsda.xsda.ue.frag;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import xsda.xsda.R;
import xsda.xsda.ue.activity.SplashActivity;
import xsda.xsda.ue.root.FragBean;
import xsda.xsda.ue.root.RootFrag;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class NetErrFrag extends RootFrag {

    @Bind(R.id.rl_neterror_all)
    RelativeLayout rlNeterrorAll;// 总布局
    @Bind(R.id.tv_neterror_des)
    TextView tvNeterrorDes;// 图标
    @Bind(R.id.tv_neterror_logo)
    TextView tvNeterrorLogo;// 描述
    @Bind(R.id.tv_neterror_retry)
    TextView tvNeterrorRetry;// 重试
    @Bind(R.id.tv_neterror_back)
    TextView tvNeterrorBack;// 退出

    private SplashActivity activity;

    @Override
    public int onInflateLayout() {
        activity = (SplashActivity) getActivity();
        return R.layout.widget_neterror;
    }

    @Override
    public void onCreatViews(View inflate) {

    }

    @Override
    public void onCreates(FragBean bean) {

    }

    @Override
    public void onClickEvent() {
        rlNeterrorAll.setOnClickListener(v -> {
        });
        tvNeterrorRetry.setOnClickListener(v -> {
            // 返回启动页 
            toFrag(getClass(), SplashFrag.class, null, true);
        });
        tvNeterrorBack.setOnClickListener(v -> onBackPresss());
    }

    @Override
    public boolean onBackPresss() {
        finish();
        kill();
        return true;
    }
}
