package xsda.xsda.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.List;

/*
 * Created by qianli.ma on 2019/6/18 0018.
 */
public class MainBean implements Serializable {
    
    private boolean isChoice;// 是否被选中
    private String leftCateforyName;// 左侧类目名
    private Drawable rightContentPic;// 右侧内容区顶部图片
    private String rightContentTitle;// 右侧内容区标题
    private List<MainRightBean> mainRightBeans;// 右侧列表Item集合
    private String rightContentMoreText;// 右侧［更多］文本
    private int rightContentMoreBg;// 右侧［更多］文本背景

    public MainBean() {
    }

    public boolean isChoice() {
        return isChoice;
    }

    public void setChoice(boolean choice) {
        isChoice = choice;
    }

    public String getLeftCateforyName() {
        return leftCateforyName;
    }

    public void setLeftCateforyName(String leftCateforyName) {
        this.leftCateforyName = leftCateforyName;
    }

    public Drawable getRightContentPic() {
        return rightContentPic;
    }

    public void setRightContentPic(Drawable rightContentPic) {
        this.rightContentPic = rightContentPic;
    }

    public String getRightContentTitle() {
        return rightContentTitle;
    }

    public void setRightContentTitle(String rightContentTitle) {
        this.rightContentTitle = rightContentTitle;
    }

    public List<MainRightBean> getMainRightBeans() {
        return mainRightBeans;
    }

    public void setMainRightBeans(List<MainRightBean> mainRightBeans) {
        this.mainRightBeans = mainRightBeans;
    }

    public String getRightContentMoreText() {
        return rightContentMoreText;
    }

    public void setRightContentMoreText(String rightContentMoreText) {
        this.rightContentMoreText = rightContentMoreText;
    }

    public int getRightContentMoreBg() {
        return rightContentMoreBg;
    }

    public void setRightContentMoreBg(int rightContentMoreBg) {
        this.rightContentMoreBg = rightContentMoreBg;
    }

    /**
     * 右侧item对象
     */
    public class MainRightBean implements Serializable {
        
        private Drawable rightItemPic;// item图片
        private String rightItemTitle;// item标题

        public MainRightBean() {
        }

        public Drawable getRightItemPic() {
            return rightItemPic;
        }

        public void setRightItemPic(Drawable rightItemPic) {
            this.rightItemPic = rightItemPic;
        }

        public String getRightItemTitle() {
            return rightItemTitle;
        }

        public void setRightItemTitle(String rightItemTitle) {
            this.rightItemTitle = rightItemTitle;
        }
    }
}
