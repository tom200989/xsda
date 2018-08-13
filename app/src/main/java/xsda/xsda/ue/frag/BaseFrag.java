package xsda.xsda.ue.frag;
/*
 * Created by qianli.ma on 2018/8/13 0013.
 */

import android.view.View;

import com.hiber.hiber.RootFrag;

public class BaseFrag extends RootFrag {

    @Override
    public boolean onBackPresss() {
        return false;
    }

    @Override
    public int onInflateLayout() {
        return 0;
    }

    @Override
    public void onNexts(Object o, View view, String s) {

    }
}
