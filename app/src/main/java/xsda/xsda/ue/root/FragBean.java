package xsda.xsda.ue.root;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public class FragBean {
    public Class currentFragmentClass;// 是哪个fragment发起的跳转
    public Object attach;// 附件(额外自定义数据对象)

    public Class getCurrentFragmentClass() {
        return currentFragmentClass;
    }

    public void setCurrentFragmentClass(Class currentFragmentClass) {
        this.currentFragmentClass = currentFragmentClass;
    }

    public Object getAttach() {
        return attach;
    }

    public void setAttach(Object attach) {
        this.attach = attach;
    }

    @Override
    public String toString() {
        return "FragBean{" + "currentFragmentClass=" + currentFragmentClass + ", attach=" + attach + '}';
    }
}
