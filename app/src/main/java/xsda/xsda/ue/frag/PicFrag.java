package xsda.xsda.ue.frag;

import android.os.Handler;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hiber.hiber.RootFrag;
import com.hiber.tools.layout.PercentRelativeLayout;
import com.p_historyrecommend.p_historyrecommend.core.HistoryRecommond;
import com.p_recycler.p_recycler.core.RcvMAWidget;
import com.p_recycler.p_recycler.core.RcvRefreshWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.adapter.PicListAdapter;
import xsda.xsda.bean.PicListItemBean;
import xsda.xsda.test.PicListTest;
import xsda.xsda.utils.Ogg;
import xsda.xsda.widget.WaitingWidget;

/*
 * Created by qianli.ma on 2019/5/7 0007.
 */
public class PicFrag extends RootFrag {

    @BindView(R.id.rl_pic_search_banner)
    PercentRelativeLayout rlBanner;// 标题栏
    @BindView(R.id.et_pic_search_input)
    EditText etSearchInput;// 输入框
    @BindView(R.id.iv_pic_search_logo)
    ImageView ivSearchLogo;// 搜索按钮
    @BindView(R.id.rf_pic)
    RcvRefreshWidget rfPic;// 下拉上拉控件
    @BindView(R.id.wv_pic)
    WaitingWidget wvPic;// 等待控件
    @BindView(R.id.hrv_pic)
    HistoryRecommond hrvPic;// 历史记录/推荐

    private RcvMAWidget rcvPic;// 图片列表
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
        initEvent();// 初始化事件
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        rcvPic = rfPic.getRcv();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
        // 如果采用瀑布流, 建议加上这个, 用于防止item闪烁
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rcvPic.setLayoutManager(layoutManager);
        picListAdapter = new PicListAdapter(activity, picListItemBeans);
        picListAdapter.setOnPicItemClickListener(picItemBean -> {
            // TODO: 2019/6/14 0014  跳转到详情页
            toast("click : " + picItemBean.getGoodNum(), 3000);
        });
        rcvPic.setAdapter(picListAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        // TODO: 2019/5/24 0024  初始化获取图片
        // TOAT: 建议获取网络图片的方式是: ［统一下载图片后再显示］而不要用xutils的在线显示模式
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

    /**
     * 初始化事件
     */
    private void initEvent() {

        /* 设置edittext监听 */
        etSearchInput.setOnFocusChangeListener((v, hasFocus) -> {
            // TODO: 2019/6/14 0014 获取历史记录以及推荐 -- 当前为模拟数据 
            if (hasFocus) {
                List<String> hiss = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    hiss.add("hiss: " + i);
                }
                List<String> recoms = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    Random random = new Random(65511);
                    recoms.add("recoms: " + random.nextInt());
                }
                hrvPic.setVisibility(View.VISIBLE);
                hrvPic.setData(hiss, recoms);
            } else {
                hrvPic.setVisibility(View.GONE);
            }
        });

        /* 点击搜索按钮 */
        ivSearchLogo.setOnClickListener(v -> {
            // TODO: 2019/6/14 0014  请求数据库搜索
            Ogg.hideKeyBoard(activity);
            String searchContent = etSearchInput.getText().toString();
            toast("begin to search: " + searchContent, 1500);
        });

        /* 下拉刷新 */
        rfPic.setOnBeginRefreshListener(recyclerView -> {
            // TODO: 2019/6/14 0014  请求网络 -- 重新加载
            new Handler().postDelayed(() -> {
                toast("refresh finish", 1500);
                rfPic.refreshFinish();
            }, 1500);
        });

        /* 上拉加载 */
        rfPic.setOnBeginLoadMoreListener(recyclerView -> {
            // TODO: 2019/6/14 0014 请求网络 -- 加载更多 
            new Handler().postDelayed(() -> {
                toast("load more finish", 1500);
                rfPic.loadFinish();
            }, 1500);
        });

        /* 点击历史记录条目 */
        hrvPic.setOnHistoryItemClickListener(historyItemBean -> {
            // TODO: 2019/6/14 0014  请求网络 -- 点击历史条目 
            toast("click history :" + historyItemBean.getItemHistoryTvTitleString(), 1500);
        });

        /* 点击历史记录条目删除 */
        hrvPic.setOnHistoryItemDelClickListener(() -> {
            // TODO: 2019/6/14 0014  删除缓存数据
            toast("click history del item", 1500);
        });

        /* 点击清空历史记录 */
        hrvPic.setOnClearAllHistoryListener(() -> {
            // TODO: 2019/6/14 0014  删除全部历史记录缓存数据
            toast("click history del all item", 1500);
        });

        /* 点击换一拨 */
        hrvPic.setOnRecomTurnClickListener(() -> {
            // TODO: 2019/6/14 0014 请求网络 -- 获取换一拨的新数据
            toast("click recom turn", 1500);
        });

        /* 点击推荐item */
        hrvPic.setOnRecomItemClickListener(textView -> {
            // TODO: 2019/6/14 0014 请求网络 -- 跳转推荐条目的详情页
            toast("click recom item: " + textView.getText().toString(), 1500);
        });
    }


    @Override
    public void onNexts(Object o, View view, String s) {

    }

    @Override
    public boolean onBackPresss() {
        if (hrvPic.getVisibility() == View.VISIBLE) {
            // 隐藏软键盘
            Ogg.hideKeyBoard(activity);
            // 隐藏历史记录框
            hrvPic.setVisibility(View.GONE);
            // 先清除焦点 + 不可编辑
            Ogg.setEdittextEditable(etSearchInput, false);
            // 再重新恢复焦点跟随触摸模式
            Ogg.setEdittextEditable(etSearchInput, true);
            return true;
        }
        killAllActivitys();
        kill();
        return true;
    }
}
