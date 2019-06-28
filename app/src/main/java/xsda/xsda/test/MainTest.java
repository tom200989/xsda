package xsda.xsda.test;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xsda.xsda.R;
import xsda.xsda.bean.MainBean;

/*
 * Created by qianli.ma on 2019/6/18 0018.
 */
public class MainTest {

    /**
     * 获取左侧菜单对象集合
     *
     * @return 左侧菜单集合
     */
    public static List<MainBean> getMainbean(Context context) {
        List<MainBean> mbs = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            MainBean mlb = new MainBean();
            mlb.setChoice(i == 0);
            String cateName = "测试数据" + i;
            mlb.setLeftCateforyName(cateName);
            mlb.setRightContentPic(context.getResources().getDrawable(i % 2 == 0 ? R.drawable.main_test_1 : R.drawable.main_test_2));
            mlb.setRightContentTitle(cateName);

            Random random = new Random();
            int r = random.nextInt(9);
            r = r <= 0 ? 2 : r;
            List<MainBean.MainRightBean> mrbs = new ArrayList<>();
            for (int j = 0; j < r; j++) {
                MainBean.MainRightBean mrb = new MainBean().new MainRightBean();
                mrb.setRightItemPic(context.getResources().getDrawable(R.drawable.pic_test_1));
                mrb.setRightItemTitle("Panel" + j);
                mrbs.add(mrb);
            }

            mlb.setMainRightBeans(mrbs);
            mlb.setRightContentMoreText(cateName);
            mbs.add(mlb);
        }
        return mbs;
    }
}
