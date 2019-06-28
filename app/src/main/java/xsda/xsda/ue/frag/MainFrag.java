package xsda.xsda.ue.frag;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;
import com.hiber.tools.ScreenSize;
import com.p_historyrecommend.p_historyrecommend.core.HistoryRecommond;
import com.p_mascroll.p_mascroll.MAScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.adapter.MainLeftAdapter;
import xsda.xsda.adapter.MainRightAdapter;
import xsda.xsda.bean.MainBean;
import xsda.xsda.test.MainTest;
import xsda.xsda.utils.Ogg;
import xsda.xsda.widget.SearchWidget;
import xsda.xsda.widget.WaitingWidget;

/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

public class MainFrag extends RootFrag {

    @BindView(R.id.rl_main_search_banner)
    RelativeLayout rlSearchBanner;// 搜索布局
    @BindView(R.id.iv_main_search_logo)
    SearchWidget ivSearch;// 搜索按钮
    @BindView(R.id.et_main_search_input)
    EditText etSearch;// 搜索输入框

    @BindView(R.id.rcv_main_left)
    RecyclerView rcvMainLeft;// 左侧菜单区域
    @BindView(R.id.sv_main_right)
    MAScrollView svMainRight;// 右侧内容滚动框架
    @BindView(R.id.ll_main_right)
    LinearLayout llMainRight;// 右侧内容区域

    @BindView(R.id.hrv_main)
    HistoryRecommond hrvMain;// 历史记录
    @BindView(R.id.wv_main)
    WaitingWidget wvMain;// 等待

    private List<MainBean> mainList = new ArrayList<>();// 数据集合
    private List<Integer> layoutTops = new ArrayList<>();// 右侧布局顶部集合
    private List<Integer> layoutBottoms = new ArrayList<>();// 右侧布局底部集合
    private List<MainRightAdapter> mainRightAdapters = new ArrayList<>();// 右侧item适配器
    private List<ImageView> mainRightPics = new ArrayList<>();// 右侧头部图片
    private List<TextView> mainRightMores = new ArrayList<>();// 右侧更多
    private LinearLayoutManager leftManager;// 左侧布局管理器 -- 用于平滑移动到目标position
    private MainLeftAdapter leftAdapter;// 左侧适配器
    private int screenHeight;// 父布局高度
    private int tempPos = 0;// 用于辅助右侧联动记录左侧位置

    @Override
    public int onInflateLayout() {
        return R.layout.frag_mains;
    }

    @Override
    public void onNexts(Object o, View view, String s) {

    }

    @Override
    public void initViewFinish(View inflateView) {
        super.initViewFinish(inflateView);
        initData();// 初始化数据
        createLeftView();// 创建左侧视图
        createRightView();// 创建右侧视图
        Looper.myQueue().addIdleHandler(new IdleHandlerImpl());// 获取右侧布局宽高\设置事件
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // TODO: 这里以后使用网络请求
        screenHeight = ScreenSize.getSize(activity).height;
        mainList.clear();
        mainList = MainTest.getMainbean(activity);// 获取数据
    }


    /**
     * 创建左侧视图
     */
    private void createLeftView() {
        leftManager = new LinearLayoutManager(activity, 1, false);
        rcvMainLeft.setLayoutManager(leftManager);
        leftAdapter = new MainLeftAdapter(activity, mainList);
        rcvMainLeft.setAdapter(leftAdapter);
    }

    /**
     * 创建右侧视图
     */
    private void createRightView() {
        llMainRight.removeAllViews();
        for (MainBean mainBean : mainList) {
            // 查找视图
            View contentPanel = View.inflate(activity, R.layout.item_main_right_list, null);
            ImageView ivPic = contentPanel.findViewById(R.id.iv_content_pic);
            TextView tvTitle = contentPanel.findViewById(R.id.tv_content_title);
            RecyclerView rcvMainRight = contentPanel.findViewById(R.id.rcv_content_item);
            TextView tvMore = contentPanel.findViewById(R.id.tv_content_more);
            mainRightPics.add(ivPic);
            mainRightMores.add(tvMore);
            // 绑定数据
            ivPic.setImageDrawable(mainBean.getRightContentPic());
            tvTitle.setText(mainBean.getRightContentTitle());
            tvMore.setText(mainBean.getRightContentMoreText());
            boolean isMoreBgNull = mainBean.getRightContentMoreBg() == 0;
            tvMore.setBackgroundColor(getResources().getColor(isMoreBgNull ? R.color.colorRoot : mainBean.getRightContentMoreBg()));
            rcvMainRight.setHasFixedSize(true);
            rcvMainRight.setNestedScrollingEnabled(false);
            rcvMainRight.setLayoutManager(new GridLayoutManager(activity, 3, 1, false));
            MainRightAdapter rightAdapter = new MainRightAdapter(activity, mainBean.getMainRightBeans());
            rcvMainRight.setAdapter(rightAdapter);
            mainRightAdapters.add(rightAdapter);
            // 加入视图
            llMainRight.addView(contentPanel);
        }
    }

    /**
     * IdleHandler 实现类 -- 用于在初始化时获取控件宽高
     */
    public class IdleHandlerImpl implements MessageQueue.IdleHandler {
        @Override
        public boolean queueIdle() {
            // 1.收集右侧布局中所有的layout:top:bottom
            layoutTops = getLayoutTops();
            layoutBottoms = getLayoutBottoms();
            // 2.设置RCV的联动
            setEvent();
            return false;// F:只执行1次 T:循环执行
        }
    }

    /**
     * 获取所有的right view layout top
     *
     * @return 所有的right view layout top
     */
    private List<Integer> getLayoutTops() {
        List<Integer> layoutTops = new ArrayList<>();
        for (int i = 0; i < llMainRight.getChildCount(); i++) {
            View childAt = llMainRight.getChildAt(i);
            layoutTops.add(childAt.getTop());
        }
        return layoutTops;
    }

    /**
     * 获取所有的right view layout bottoms
     *
     * @return 所有的right view layout bottoms
     */
    private List<Integer> getLayoutBottoms() {
        List<Integer> layoutBottoms = new ArrayList<>();
        for (int i = 0; i < llMainRight.getChildCount(); i++) {
            View childAt = llMainRight.getChildAt(i);
            layoutBottoms.add(childAt.getBottom());
        }
        return layoutBottoms;
    }

    /**
     * 设置事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setEvent() {

        /* 设定左侧点击联动 */
        leftAdapter.setOnLeftItemClickListener((mainBean, position) -> {
            int targetLayoutTop = layoutTops.get(position);// 取出面板top
            // svMainRight.smoothScrollTo(0, targetLayoutTop);// 平滑到top
            svMainRight.scrollTo(0, targetLayoutTop);
        });

        /* 设定右侧滑动联动监听 */
        svMainRight.setOnScrollListener(new MAScrollView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(MAScrollView maScrollView, int scrollState, int scrollX, int scrollY) {

            }

            @Override
            public void onScroll(MAScrollView maScrollView, boolean isTouchScroll, int scrollX, int scrollY, int left, int top, int oldleft, int oldtop) {
                // 联动定位
                int bestPostion = Ogg.getBestPostion(layoutBottoms, scrollY, screenHeight / 4);
                if (tempPos != bestPostion) {// 检测到新的位标不同之后 -- 才更新左侧列表
                    for (int i = 0; i < mainList.size(); i++) {
                        mainList.get(i).setChoice(i == bestPostion);
                    }
                    leftAdapter.notifys(mainList);
                    leftManager.scrollToPosition(bestPostion);
                    tempPos = bestPostion;
                }
            }
        });

        /* 设定右侧item点击 */
        for (MainRightAdapter rightAdapter : mainRightAdapters) {
            rightAdapter.setOnRightItemClickListener(mrb -> {
                // TODO: 2019/6/26 0026  右侧item点击
                toast("click rightAdapter: " + mrb.getRightItemTitle(), 1500);
            });
        }

        /* 右侧pic点击 */
        for (int i = 0; i < mainRightPics.size(); i++) {
            ImageView rightPic = mainRightPics.get(i);
            int finalI = i;
            rightPic.setOnClickListener(v -> {
                // TODO: 2019/6/27 0027  右侧PIC点击
                toast("click rightPic: " + mainList.get(finalI).getRightContentTitle(), 1500);
            });
        }

        /* 右侧More点击 */
        for (int i = 0; i < mainRightMores.size(); i++) {
            TextView rightMore = mainRightMores.get(i);
            int finalI = i;
            rightMore.setOnClickListener(v -> {
                // TODO: 2019/6/27 0027  右侧More点击
                toast("click rightMore: " + mainList.get(finalI).getRightContentTitle(), 1500);
            });
        }
        
        /* 搜索框点击 */
        // 必须采用实现了［onTouchListener］的控件来重新设定点击行为
        ivSearch.setOnClickItListener(() -> {
            // TODO: 2019/6/27 0027  点击搜索按钮
            Ogg.hideKeyBoard(activity);
            String searchContent = etSearch.getText().toString();
            toast("click search: " + searchContent, 1500);
        });
        
        /* 点击输入框 */
        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
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
                hrvMain.setVisibility(View.VISIBLE);
                hrvMain.setData(hiss, recoms);
            } else {
                Ogg.hideKeyBoard(activity);
                hrvMain.setVisibility(View.GONE);
            }
        });

        /* 点击历史记录条目 */
        hrvMain.setOnHistoryItemClickListener(historyItemBean -> {
            // TODO: 2019/6/14 0014  请求网络 -- 点击历史条目 
            Ogg.hideKeyBoard(activity);
            toast("click history :" + historyItemBean.getItemHistoryTvTitleString(), 1500);
        });

        /* 点击历史记录条目删除 */
        hrvMain.setOnHistoryItemDelClickListener(() -> {
            // TODO: 2019/6/14 0014  删除缓存数据
            Ogg.hideKeyBoard(activity);
            toast("click history del item", 1500);
        });

        /* 点击清空历史记录 */
        hrvMain.setOnClearAllHistoryListener(() -> {
            // TODO: 2019/6/14 0014  删除全部历史记录缓存数据
            Ogg.hideKeyBoard(activity);
            toast("click history del all item", 1500);
        });

        /* 点击换一拨 */
        hrvMain.setOnRecomTurnClickListener(() -> {
            // TODO: 2019/6/14 0014 请求网络 -- 获取换一拨的新数据
            Ogg.hideKeyBoard(activity);
            toast("click recom turn", 1500);
        });

        /* 点击推荐item */
        hrvMain.setOnRecomItemClickListener(textView -> {
            // TODO: 2019/6/14 0014 请求网络 -- 跳转推荐条目的详情页
            Ogg.hideKeyBoard(activity);
            toast("click recom item: " + textView.getText().toString(), 1500);
        });
    }


    @Override
    public boolean onBackPresss() {
        // 历史记录面板
        if (hrvMain.getVisibility() == View.VISIBLE) {
            hrvMain.setVisibility(View.GONE);
            return true;
        }
        // 等待面板
        if (wvMain.getVisibility() == View.VISIBLE) {
            return true;
        }
        killAllActivitys();
        kill();
        return true;
    }
}
