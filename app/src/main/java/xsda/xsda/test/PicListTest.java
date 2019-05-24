package xsda.xsda.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;
import java.util.List;

import xsda.xsda.R;
import xsda.xsda.bean.PicListItemBean;
import xsda.xsda.utils.DrawTool;

/*
 * Created by qianli.ma on 2019/5/24 0024.
 */
public class PicListTest {

    public static List<PicListItemBean> testData(Activity activity) {
        // TODO: 2019/5/22 0022 ----------------测试设置数据----------------
        List<PicListItemBean> piclists = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            PicListItemBean itemBean = new PicListItemBean();
            itemBean.setGoodNum(String.valueOf(130 + i));
            int mod = (i + 5) % 5;

            // 测试本地部分
            Bitmap bitmap = getThumboDrawable(activity, R.drawable.pic_test_1);
            if (mod == 0) {
                bitmap = getThumboDrawable(activity, R.drawable.pic_test_1);
            } else if (mod == 1) {
                bitmap = getThumboDrawable(activity, R.drawable.pic_test_2);
            } else if (mod == 2) {
                bitmap = getThumboDrawable(activity, R.drawable.pic_test_3);
            } else if (mod == 3) {
                bitmap = getThumboDrawable(activity, R.drawable.pic_test_4);
            } else if (mod == 4) {
                bitmap = getThumboDrawable(activity, R.drawable.pic_test_5);
            }

            // // 测试网络部分
            // String url = "http://pic37.nipic.com/20140113/8800276_184927469000_2.png";
            // if (mod == 0) {
            //     url = "http://pic37.nipic.com/20140113/8800276_184927469000_2.png";
            // } else if (mod == 1) {
            //     url = "http://pic15.nipic.com/20110628/1369025_192645024000_2.jpg";
            // } else if (mod == 2) {
            //     url = "http://pic34.nipic.com/20131103/9422601_173208318000_2.jpg";
            // } else if (mod == 3) {
            //     url = "http://img1.imgtn.bdimg.com/it/u=4050814549,1600009594&fm=26&gp=0.jpg";
            // } else if (mod == 4) {
            //     url = "http://img2.imgtn.bdimg.com/it/u=2569506716,2804469352&fm=26&gp=0.jpg";
            // }

            itemBean.setHead(bitmap);
            itemBean.setMainPic(bitmap);
            // itemBean.setHeadUrl(url);
            // itemBean.setMainPicUrl(url);
            piclists.add(itemBean);
        }
        // TODO: 2019/5/22 0022 ----------------测试设置数据----------------
        return piclists;
    }

    private static Bitmap getThumboDrawable(Activity activity, int drawId) {
        Bitmap bitmap = ((BitmapDrawable) activity.getResources().getDrawable(drawId)).getBitmap();
        return DrawTool.Bitmap2ThumboBitmap(activity, bitmap, 0.5f);
    }
}
