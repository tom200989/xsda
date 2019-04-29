package xsda.xsda.bean;

import java.io.Serializable;

/*
 * Created by qianli.ma on 2019/4/25 0025.
 */
public class KickBean implements Serializable {

    private int type;// 被踢类型

    public KickBean() {
    }

    public KickBean(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
