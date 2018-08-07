package xsda.xsda.bean;
/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;

public class UserClientBean {
    private AVUser avUser;
    private AVIMClient avimClient;

    public AVUser getAvUser() {
        return avUser;
    }

    public void setAvUser(AVUser avUser) {
        this.avUser = avUser;
    }

    public AVIMClient getAvimClient() {
        return avimClient;
    }

    public void setAvimClient(AVIMClient avimClient) {
        this.avimClient = avimClient;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserClientBean{");
        sb.append("\n").append("\t").append("avUser =").append(avUser);
        sb.append("\n").append("\t").append("avimClient =").append(avimClient);
        sb.append("\n}");
        return sb.toString();
    }
}
