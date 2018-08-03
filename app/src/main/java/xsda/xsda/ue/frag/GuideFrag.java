package xsda.xsda.ue.frag;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hiber.hiber.RootFrag;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xsda.xsda.R;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Sgg;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class GuideFrag extends RootFrag {

    @Bind(R.id.vp_guide)
    ViewPager vpGuide;
    @Bind(R.id.tv_guide_click)
    TextView tvGuideClick;
    @Bind(R.id.ll_guide_points)
    PercentLinearLayout llGuidePoints;

    List<ImageView> guideViews = new ArrayList<>();
    List<ImageView> dotViews = new ArrayList<>();
    private int size = 15;// 圆点大小以及间隔参考
    private int[] res_bgs = new int[]{R.drawable.guide1, R.drawable.guide2, R.drawable.guide3};
    private int[] res_dots = new int[]{R.drawable.dot_guide_selected, R.drawable.dot_guide_unselected};

    @Override
    public int onInflateLayout() {
        return R.layout.frag_guide;
    }

    @Override
    public void onNexts(Object yourBean, View view, String whichFragmentStart) {
        prepareResource();// 准备资源
        setGuideAdapter();// 设置适配器
        onClickEvent();
    }

    public void onClickEvent() {
        // 点击「立即体验」
        tvGuideClick.setOnClickListener(v -> {
            Sgg.getInstance(activity).putBoolean(Cons.SP_GUIDE, true);
            toFrag(getClass(), LoginFrag.class, null, false);
        });
    }

    @Override
    public boolean onBackPresss() {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 提取资源
     */
    private void prepareResource() {

        guideViews.clear();
        dotViews.clear();

        // 引导页
        for (int draw : res_bgs) {
            ImageView ivBg = new ImageView(activity);
            ivBg.setImageDrawable(getResources().getDrawable(draw));
            ivBg.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
            ivBg.setBackgroundColor(Color.WHITE);
            guideViews.add(ivBg);
        }

        // 圆点
        for (int i = 0; i < res_bgs.length; i++) {
            ImageView ivDot = new ImageView(activity);
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
    private void setGuideAdapter() {
        GuideAdapter adapter = new GuideAdapter();
        vpGuide.setAdapter(adapter);
        vpGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvGuideClick.setVisibility(position == guideViews.size() - 1 ? View.VISIBLE : View.GONE);
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
}
