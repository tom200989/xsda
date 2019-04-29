package xsda.xsda.wxapi;

import java.io.Serializable;
import java.util.List;

/*
 * Created by qianli.ma on 2018/10/9 0009.
 */
public class WechatInfo implements Serializable {
    /**
     * openid : oM2MN1F2Tx5K6FTeBfXKNxHspKzQ
     * nickname : Doris
     * sex : 2
     * language : zh_CN
     * city :
     * province : Dublin
     * country : IE
     * headimgurl : http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83erV
     * privilege : []
     * unionid : o_MkywZOq7WqHdsvsCIQnW3JOL5E
     */

    private String openid;
    private String nickname;// 昵称
    private int sex;// 性别--> 1:man 2:women
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;// 头像链接
    private String unionid;
    private List<?> privilege;
    private String attach;// 自定义信息(非微信返回) 

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<?> privilege) {
        this.privilege = privilege;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("WechatInfo{");
        sb.append("\n").append("\t").append("openid ='").append(openid).append('\'');
        sb.append("\n").append("\t").append("nickname ='").append(nickname).append('\'');
        sb.append("\n").append("\t").append("sex =").append(sex);
        sb.append("\n").append("\t").append("language ='").append(language).append('\'');
        sb.append("\n").append("\t").append("city ='").append(city).append('\'');
        sb.append("\n").append("\t").append("province ='").append(province).append('\'');
        sb.append("\n").append("\t").append("country ='").append(country).append('\'');
        sb.append("\n").append("\t").append("headimgurl ='").append(headimgurl).append('\'');
        sb.append("\n").append("\t").append("unionid ='").append(unionid).append('\'');
        sb.append("\n").append("\t").append("privilege =").append(privilege);
        sb.append("\n").append("\t").append("attach ='").append(attach).append('\'');
        sb.append("\n}");
        return sb.toString();
    }
}
