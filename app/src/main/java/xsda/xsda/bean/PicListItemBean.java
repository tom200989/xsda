package xsda.xsda.bean;

import android.graphics.Bitmap;

/*
 * Created by qianli.ma on 2019/5/21 0021.
 */
public class PicListItemBean {
    
    private String mainPicUrl;// 主图连接
    private Bitmap mainPic;// 主图图元
    private String headUrl;// 头像链接
    private Bitmap head;// 头像图元
    private String goodNum;// 点赞数

    public PicListItemBean() {
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

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public Bitmap getHead() {
        return head;
    }

    public void setHead(Bitmap head) {
        this.head = head;
    }

    public String getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(String goodNum) {
        this.goodNum = goodNum;
    }
}
