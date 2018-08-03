package xsda.xsda.ue.frag;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;

import butterknife.Bind;
import xsda.xsda.R;

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


    @Override
    public int onInflateLayout() {
        return R.layout.frag_neterror;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
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
