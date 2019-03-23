package xsda.xsda.ue.frag;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.List;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.utils.Avfield;
import xsda.xsda.utils.Tgg;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class NetErrFrag extends BaseFrag {

    @BindView(R.id.rl_neterror_all)
    RelativeLayout rlNeterrorAll;// 总布局
    @BindView(R.id.tv_neterror_des)
    TextView tvNeterrorDes;// 图标
    @BindView(R.id.tv_neterror_logo)
    TextView tvNeterrorLogo;// 描述
    @BindView(R.id.tv_neterror_retry)
    TextView tvNeterrorRetry;// 重试
    @BindView(R.id.tv_neterror_back)
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
            // 如果是主页断网
            if (whichFragmentStart.equalsIgnoreCase(MainFrag.class.getSimpleName())) {
                checkNetWork();
            } else {
                // 返回启动页 
                toFrag(getClass(), SplashFrag.class, null, true);
            }

        });
        tvNeterrorBack.setOnClickListener(v -> onBackPresss());
    }

    /**
     * 检查网络
     */
    private void checkNetWork() {
        AVQuery<AVObject> query = new AVQuery<>(Avfield.update.classname);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    toFrag(getClass(), MainFrag.class, null, false);
                } else {
                    Tgg.show(activity, R.string.base_network_login, 2500);
                }
            }
        });
    }

    @Override
    public boolean onBackPresss() {
        finishActivity();
        kill();
        return true;
    }
}
