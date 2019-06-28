package xsda.xsda.ue.frag;

import android.os.Handler;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.hiber.hiber.RootFrag;
import com.p_recycler.p_recycler.core.RcvMAWidget;
import com.p_recycler.p_recycler.core.RcvRefreshWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import xsda.xsda.R;
import xsda.xsda.adapter.VideoListAdapter;
import xsda.xsda.bean.VideoListItemBean;
import xsda.xsda.test.VideoListTest;
import xsda.xsda.widget.WaitingWidget;

/*
 * Created by qianli.ma on 2019/5/7 0007.
 */
public class VideoFrag extends RootFrag {

    @BindView(R.id.rf_video)
    RcvRefreshWidget rfVideo;
    @BindView(R.id.wv_video)
    WaitingWidget wvVideo;

    private RcvMAWidget rcvVideo;// 视频列表
    private VideoListAdapter videoListAdapter;
    private List<VideoListItemBean> videoListItemBeans = new ArrayList<>();

    @Override
    public int onInflateLayout() {
        return R.layout.frag_video;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initAdapter();// 初始化适配器
        initData();// 初始化数据
        initEvent();// 初始化事件
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        rcvVideo = rfVideo.getRcv();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
        // 如果采用瀑布流, 建议加上这个, 用于防止item闪烁
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rcvVideo.setLayoutManager(layoutManager);
        videoListAdapter = new VideoListAdapter(activity, videoListItemBeans);
        videoListAdapter.setOnVideoItemClickListener(videoItemBean -> {
            // TODO: 2019/6/14 0014  跳转到详情页
            toast("click : " + videoItemBean.getGoodNum(), 2000);
        });
        rcvVideo.setAdapter(videoListAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        // TODO: 2019/5/24 0024  初始化获取图片
        // TOAT: 建议获取网络图片的方式是: ［统一下载图片后再显示］而不要用xutils的在线显示模式
        // 显示等待
        wvVideo.setDescritionText(getString(R.string.video_loading_text));
        // TOGO: 测试数据
        new Thread(() -> {
            videoListItemBeans = VideoListTest.testData(activity);
            activity.runOnUiThread(() -> {
                videoListAdapter.notifys(videoListItemBeans);
                wvVideo.setGone();
            });
        }).start();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {

        /* 下拉刷新 */
        rfVideo.setOnBeginRefreshListener(recyclerView -> {
            // TODO: 2019/6/14 0014  请求网络 -- 重新加载
            new Handler().postDelayed(() -> {
                toast("refresh finish", 1500);
                rfVideo.refreshFinish();
            }, 1500);
        });

        /* 上拉加载 */
        rfVideo.setOnBeginLoadMoreListener(recyclerView -> {
            // TODO: 2019/6/14 0014 请求网络 -- 加载更多 
            new Handler().postDelayed(() -> {
                toast("load more finish", 1500);
                rfVideo.loadFinish();
            }, 1500);
        });
    }

    @Override
    public void onNexts(Object o, View view, String s) {

    }

    @Override
    public boolean onBackPresss() {

        if (wvVideo.getVisibility() == View.VISIBLE) {
            return true;
        }
        killAllActivitys();
        kill();
        return true;
    }

}
