package xsda.xsda.ue.frag;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hiber.hiber.RootFrag;
import com.hiber.tools.layout.PercentRelativeLayout;
import com.p_recycler.p_recycler.core.RcvMAWidget;
import com.p_recycler.p_recycler.core.RcvRefreshWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.adapter.PicListAdapter;
import xsda.xsda.bean.PicListItemBean;

/*
 * Created by qianli.ma on 2019/5/7 0007.
 */
public class PicFrag extends RootFrag {


    @BindView(R.id.rl_pic_banner)
    PercentRelativeLayout rlBanner;// 标题栏
    @BindView(R.id.et_pic_search_input)
    EditText etSearchInput;// 输入框
    @BindView(R.id.iv_pic_search_logo)
    ImageView ivSearchLogo;// 搜索按钮
    @BindView(R.id.rf_pic)
    RcvRefreshWidget rfPic;// 下拉上拉控件
    private RcvMAWidget rcv;// 列表

    @Override
    public int onInflateLayout() {
        return R.layout.frag_pic;
    }

    @Override
    public void initViewFinish(View inflateView) {
        super.initViewFinish(inflateView);
        initView();
    }

    private void initView() {

        // TODO: 2019/5/22 0022  测试设置数据
        List<PicListItemBean> piclists = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PicListItemBean itemBean = new PicListItemBean();
            itemBean.setGoodNum(String.valueOf(130 + i));
            itemBean.setHead(((BitmapDrawable) getResources().getDrawable(R.drawable.pic_test_1)).getBitmap());
            itemBean.setMainPic(((BitmapDrawable) getResources().getDrawable(R.drawable.pic_test_2)).getBitmap());
            piclists.add(itemBean);
        }

        rcv = rfPic.getRcv();
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false);
        rcv.setLayoutManager(layoutManager);
        rcv.setAdapter(new PicListAdapter(activity, piclists));
    }

    @Override
    public void onNexts(Object o, View view, String s) {

    }

    @Override
    public boolean onBackPresss() {
        return false;
    }
}
