package xsda.xsda.ue.frag;

import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hiber.hiber.RootFrag;
import com.hiber.tools.layout.PercentRelativeLayout;
import com.p_recycler.p_recycler.core.RcvMAWidget;
import com.p_recycler.p_recycler.core.RcvRefreshWidget;

import java.util.List;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.adapter.PicListAdapter;
import xsda.xsda.bean.PicListItemBean;
import xsda.xsda.test.PicListTest;
import xsda.xsda.widget.WaitingWidget;

/*
 * Created by qianli.ma on 2019/5/7 0007.
 */
public class PicFrag extends RootFrag {

    @BindView(R.id.wv_pic)
    WaitingWidget wvPic;// 等待控件
    @BindView(R.id.rl_pic_banner)
    PercentRelativeLayout rlBanner;// 标题栏
    @BindView(R.id.et_pic_search_input)
    EditText etSearchInput;// 输入框
    @BindView(R.id.iv_pic_search_logo)
    ImageView ivSearchLogo;// 搜索按钮
    @BindView(R.id.rf_pic)
    RcvRefreshWidget rfPic;// 下拉上拉控件

    private RcvMAWidget rcv;// 列表
    private PicListAdapter picListAdapter;
    private List<PicListItemBean> picListItemBeans;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_pic;
    }

    @Override
    public void initViewFinish(View inflateView) {
        super.initViewFinish(inflateView);
        initAdapter();// 初始化适配器
        initData();// 初始化数据
    }


    /**
     * 初始化适配器
     */
    private void initAdapter() {
        rcv = rfPic.getRcv();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);// 如果采用瀑布流, 建议加上这个, 用于防止item闪烁
        rcv.setLayoutManager(layoutManager);
        picListAdapter = new PicListAdapter(activity, picListItemBeans);
        rcv.setAdapter(picListAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        // TODO: 2019/5/24 0024  建议获取网络图片的方式是: ［统一下载图片后再显示］而不要用xutils的在线显示模式
        // 显示等待
        wvPic.setDescritionText(getString(R.string.pic_loading_text));
        new Thread(() -> {
            picListItemBeans = PicListTest.testData(activity);
            // 测试数据
            activity.runOnUiThread(() -> {
                picListAdapter.notifys(picListItemBeans);
                wvPic.setGone();
            });
        }).start();

    }


    @Override
    public void onNexts(Object o, View view, String s) {

    }

    @Override
    public boolean onBackPresss() {
        killAllActivitys();
        kill();
        return true;
    }
}
