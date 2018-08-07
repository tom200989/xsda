package xsda.xsda.bean;
/*
 * Created by qianli.ma on 2018/8/7 0007.
 */

public class LoginBean {
    private String phoneNum;
    private String password;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LoginBean{");
        sb.append("\n").append("\t").append("phoneNum ='").append(phoneNum).append('\'');
        sb.append("\n").append("\t").append("password ='").append(password).append('\'');
        sb.append("\n}");
        return sb.toString();
    }
}
