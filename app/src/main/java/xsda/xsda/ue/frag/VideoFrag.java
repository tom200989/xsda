package xsda.xsda.ue.frag;

import android.view.View;

import com.hiber.hiber.RootFrag;

import xsda.xsda.R;

/*
 * Created by qianli.ma on 2019/5/7 0007.
 */
public class VideoFrag extends RootFrag {

    @Override
    public int onInflateLayout() {
        return R.layout.frag_video;
    }

    @Override
    public void initViewFinish(View inflateView) {

    }

    @Override
    public void onNexts(Object o, View view, String s) {

    }

    @Override
    public boolean onBackPresss() {
        return false;
    }

}
