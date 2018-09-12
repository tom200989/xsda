package xsda.xsda.helper.wechat;

/*
 * Created by qianli.ma on 2018/9/7 0007.
 */
public class WechatBean {
    private String userIcon;
    private String userName;
    private String userId;
    private String token;
    private String userGender;

    private String sex;// 性别: 1
    private String nickname;// 昵称: name
    private String language;// 语言: zh_CN
    private String headimgurl;// 头像: http://xx/xx/xx
    private String country;// 国家: CN
    private String province;// 省份: Guangdong
    private String city;// 城市: maoming


    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("WechatBean{");
        sb.append("\n").append("\t").append("userIcon ='").append(userIcon).append('\'');
        sb.append("\n").append("\t").append("userName ='").append(userName).append('\'');
        sb.append("\n").append("\t").append("userId ='").append(userId).append('\'');
        sb.append("\n").append("\t").append("token ='").append(token).append('\'');
        sb.append("\n").append("\t").append("userGender ='").append(userGender).append('\'');
        sb.append("\n").append("\t").append("sex ='").append(sex).append('\'');
        sb.append("\n").append("\t").append("nickname ='").append(nickname).append('\'');
        sb.append("\n").append("\t").append("province ='").append(province).append('\'');
        sb.append("\n").append("\t").append("language ='").append(language).append('\'');
        sb.append("\n").append("\t").append("headimgurl ='").append(headimgurl).append('\'');
        sb.append("\n").append("\t").append("city ='").append(city).append('\'');
        sb.append("\n").append("\t").append("country ='").append(country).append('\'');
        sb.append("\n}");
        return sb.toString();
    }
}
