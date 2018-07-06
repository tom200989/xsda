package xsda.xsda.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.List;

import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/7/5 0005.
 */

public class GuideWidget extends RelativeLayout {

    private ViewPager vpGuide;
    private TextView tvGuideClick;
    private PercentLinearLayout llGuidePoints;
    List<ImageView> guideViews = new ArrayList<>();
    List<ImageView> dotViews = new ArrayList<>();
    private int size = 15;// 圆点大小以及间隔参考
    private int[] res_bgs = new int[]{R.drawable.guide1, R.drawable.guide2, R.drawable.guide3};
    private int[] res_dots = new int[]{R.drawable.dot_guide_selected, R.drawable.dot_guide_unselected};

    public GuideWidget(Context context) {
        this(context, null, 0);
    }

    public GuideWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuideWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.widget_guide, this);
        vpGuide = (ViewPager) findViewById(R.id.vp_guide);
        tvGuideClick = (TextView) findViewById(R.id.tv_guide_click);
        tvGuideClick.setOnClickListener(v -> clickNowNext());
        llGuidePoints = (PercentLinearLayout) findViewById(R.id.ll_guide_points);
        getRes(context);
        putAdapter();
    }

    /**
     * 提取资源
     *
     * @param context
     */
    private void getRes(Context context) {

        guideViews.clear();
        dotViews.clear();

        // 引导页
        for (int draw : res_bgs) {
            ImageView ivBg = new ImageView(context);
            ivBg.setImageDrawable(getResources().getDrawable(draw));
            ivBg.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
            ivBg.setBackgroundColor(Color.WHITE);
            guideViews.add(ivBg);
        }

        // 圆点
        for (int i = 0; i < res_bgs.length; i++) {
            ImageView ivDot = new ImageView(context);
            ivDot.setImageDrawable(i == 0 ? getResources().getDrawable(res_dots[0]) : getResources().getDrawable(res_dots[1]));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(i == 0 ? 0 : size * 2, 0, 0, 0);
            ivDot.setLayoutParams(lp);
            dotViews.add(ivDot);
            llGuidePoints.addView(ivDot);
        }
    }

    /**
     * 关联适配器
     */
    private void putAdapter() {
        GuideAdapter adapter = new GuideAdapter();
        vpGuide.setAdapter(adapter);
        vpGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvGuideClick.setVisibility(position == guideViews.size() - 1 ? VISIBLE : GONE);
                for (int i = 0; i < dotViews.size(); i++) {
                    ImageView ivDot = dotViews.get(i);
                    ivDot.setImageDrawable(i == position ? getResources().getDrawable(res_dots[0]) : getResources().getDrawable(res_dots[1]));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 适配器
     */
    private class GuideAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return guideViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(guideViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(guideViews.get(position));
            return guideViews.get(position);
        }
    }

    private OnClickNowListener onClickNowListener;

    // 接口OnClickNowListener
    public interface OnClickNowListener {
        void clickNow();
    }

    // 对外方式setOnClickNowListener
    public void setOnClickNowListener(OnClickNowListener onClickNowListener) {
        this.onClickNowListener = onClickNowListener;
    }

    // 封装方法clickNowNext
    private void clickNowNext() {
        if (onClickNowListener != null) {
            onClickNowListener.clickNow();
        }
    }
}
