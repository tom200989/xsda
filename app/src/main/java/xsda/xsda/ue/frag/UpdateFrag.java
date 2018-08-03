package xsda.xsda.ue.frag;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;
import com.zhy.android.percent.support.PercentRelativeLayout;

import butterknife.Bind;
import xsda.xsda.R;
import xsda.xsda.bean.UpdateBean;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Sgg;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class UpdateFrag extends RootFrag {

    @Bind(R.id.iv_update_bg)
    ImageView ivUpdateBg;
    @Bind(R.id.tv_update_title)
    TextView tvUpdateTitle;
    @Bind(R.id.tv_update_des)
    TextView tvUpdateDes;
    @Bind(R.id.tv_update_cancel)
    TextView tvUpdateCancel;
    @Bind(R.id.tv_update_ok)
    TextView tvUpdateOk;
    @Bind(R.id.rl_update_content)
    PercentRelativeLayout rlUpdateContent;

    private View inflate;
    private UpdateBean updateBean;


    @Override
    public int onInflateLayout() {
        return R.layout.frag_update;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        this.inflate = view;
        this.updateBean = (UpdateBean) yourBean;

        // 设置描述
        tvUpdateDes.setText(updateBean.getNewVersionFix());
        // 设置点击事件
        ivUpdateBg.setOnClickListener(v -> {
        });
        rlUpdateContent.setOnClickListener(v -> {
        });
        tvUpdateCancel.setOnClickListener(v -> toGuideOrLogin());
        tvUpdateOk.setOnClickListener(v -> {
            // 前往下载页 
            toFrag(getClass(), DownFrag.class, updateBean, true);
        });
    }

    @Override
    public boolean onBackPresss() {
        toGuideOrLogin();
        return true;
    }

    /**
     * 向导页|登录页
     */
    private void toGuideOrLogin() {
        // 向导页|登录页
        if (Sgg.getInstance(activity).getBoolean(Cons.SP_GUIDE, false)) {
            // 进入登录页
            toFrag(getClass(), LoginFrag.class, null, false);
            Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":toGuideOrLogin()" + "to login fragment");
        } else {
            // 进入向导页
            toFrag(getClass(), GuideFrag.class, null, false);
            Lgg.t(Cons.TAG).ii(getClass().getSimpleName() + ":toGuideOrLogin()" + "to guide fragment");
        }
    }

}
