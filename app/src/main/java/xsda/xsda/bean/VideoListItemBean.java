package xsda.xsda.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/*
 * Created by qianli.ma on 2019/5/21 0021.
 */
public class VideoListItemBean implements Serializable {

    private String mainPicUrl;// 主图连接
    private Bitmap mainPic;// 主图图元
    private String goodNum;// 点赞数

    public VideoListItemBean() {
    }

    public String getMainPicUrl() {
        return mainPicUrl;
    }

    public void setMainPicUrl(String mainPicUrl) {
        this.mainPicUrl = mainPicUrl;
    }

    public Bitmap getMainPic() {
        return mainPic;
    }

    public void setMainPic(Bitmap mainPic) {
        this.mainPic = mainPic;
    }

    public String getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(String goodNum) {
        this.goodNum = goodNum;
    }
}
