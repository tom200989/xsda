package xsda.xsda.bean;

/**
 * Created by qianli.ma on 2018/6/26 0026.
 */

public class UpdateBean {

    private String updatedAt;// 最近一次更新日期
    private String newVersionCode;// 新版本号
    private String newVersionFix;// 新版本描述
    private String newVersionFileUrl;// 新版本链接
    private long newVersionSize;// 新版本大小

    public UpdateBean() {

    }

    public long getNewVersionSize() {
        return newVersionSize;
    }

    public void setNewVersionSize(long newVersionSize) {
        this.newVersionSize = newVersionSize;
    }

    public String getNewVersionFix() {
        return newVersionFix;
    }

    public void setNewVersionFix(String newVersionFix) {
        this.newVersionFix = newVersionFix;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getNewVersionCode() {
        return newVersionCode;
    }

    public void setNewVersionCode(String newVersionCode) {
        this.newVersionCode = newVersionCode;
    }

    public String getNewVersionFileUrl() {
        return newVersionFileUrl;
    }

    public void setNewVersionFileUrl(String newVersionFileUrl) {
        this.newVersionFileUrl = newVersionFileUrl;
    }

    @Override
    public String toString() {
        return "UpdateBean{" + "updatedAt='" + updatedAt + '\'' + ", newVersionCode='" + newVersionCode + '\'' + ", newVersionFix='" + newVersionFix + '\'' + ", newVersionFileUrl='" + newVersionFileUrl + '\'' + ", newVersionSize=" + newVersionSize + '}';
    }
}
