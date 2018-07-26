package xsda.xsda.ue.root;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by qianli.ma on 2018/7/23 0023.
 */

public abstract class RootFrag extends Fragment implements FragmentBackHandler {

    private View inflateView;
    private int layoutId;
    public FragmentActivity activityAttach;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityAttach = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 4.注册订阅
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void transferFrag(FragBean bean) {
        onCreates(bean);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 1.填入layoutId
        layoutId = onInflateLayout();
        // 2.填充视图
        inflateView = View.inflate(getActivity(), layoutId, null);
        // 3.绑定butterknife
        ButterKnife.bind(this, inflateView);
        onCreatViews(inflateView);
        // 5.点击事件、焦点处理等
        onClickEvent();
        return inflateView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onBackPressed() {
        return onBackPresss();
    }

    /* -------------------------------------------- abstract -------------------------------------------- */

    /**
     * @param bean 1.eventbus接收订阅
     */
    public abstract void onCreates(FragBean bean);

    /**
     * @return 2.填入layoutId
     */
    public abstract int onInflateLayout();

    /**
     * @param inflate 3.填充视图
     */
    public abstract void onCreatViews(View inflate);

    /**
     * 4.点击事件,焦点事件等
     */
    public abstract void onClickEvent();

    /**
     * @return 5.点击返回键
     */
    public abstract boolean onBackPresss();
    
    /* -------------------------------------------- helper -------------------------------------------- */

    /**
     * 跳转到别的fragment
     *
     * @param current  当前
     * @param target   目标
     * @param object   附带
     * @param isReload 是否重载视图
     */
    public void toFrag(Class current, Class target, Object object, boolean isReload) {
        ((RootActivity) getActivity()).toFrag(current, target, object, isReload);
    }

    /**
     * 结束当前Activit
     */
    public void finish() {
        RootHelper.finishOver(getActivity());
    }

    /**
     * 杀死APP
     */
    public void kill() {
        RootHelper.kill();
    }

    /**
     * 吐司提示
     *
     * @param tip      提示
     * @param duration 时长
     */
    public void toast(String tip, int duration) {
        RootHelper.toast(getActivity(), tip, duration);
    }

    /**
     * 跳转(默认方式)
     *
     * @param context   当前环境
     * @param clazz     目标
     * @param isDefault 是否默认方式
     */
    public void toActivity(Context context, Class<?> clazz, boolean isDefault) {
        RootHelper.to(context, clazz, true, true, false, 0);
    }
}
