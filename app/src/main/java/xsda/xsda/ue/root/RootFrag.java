package xsda.xsda.ue.root;

import android.app.Activity;
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
    public FragmentActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 1.填入layoutId
        layoutId = onInflateLayout();
        // 2.填充视图
        inflateView = View.inflate(activity, layoutId, null);
        // 3.绑定butterknife
        ButterKnife.bind(this, inflateView);
        // 4.初始化
        firstInitNext();
        return inflateView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            // 4.注册订阅
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getData(FragBean bean) {
        Object attach = bean.getAttach();
        String whichFragmentStart = bean.getCurrentFragmentClass().getSimpleName();
        String targetFragment = bean.getTargetFragmentClass().getSimpleName();
        // 确保现在运行的是目标fragment
        if (getClass().getSimpleName().equalsIgnoreCase(targetFragment)) {
            onNexts(attach, inflateView, whichFragmentStart);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public boolean onBackPressed() {
        return onBackPresss();
    }

    /* -------------------------------------------- abstract -------------------------------------------- */

    /**
     * 1.你的业务逻辑
     *
     * @param yourBean           你的自定义附带对象(请执行强转)
     * @param view               填充视图
     * @param whichFragmentStart 由哪个fragment发起的跳转
     */
    public abstract void onNexts(Object yourBean, View view, String whichFragmentStart);

    /**
     * @return 2.填入layoutId
     */
    public abstract int onInflateLayout();

    /**
     * @return 3.点击返回键
     */
    public abstract boolean onBackPresss();
    
    /* -------------------------------------------- helper -------------------------------------------- */

    /**
     * 跳转到别的fragment
     *
     * @param current        当前
     * @param target         目标
     * @param object         附带
     * @param isTargetReload 是否重载视图
     */
    public void toFrag(Class current, Class target, Object object, boolean isTargetReload) {
        ((RootActivity) getActivity()).toFrag(current, target, object, isTargetReload);
    }

    /**
     * @param target 移除某个fragment
     */
    public void removeFrag(Class target) {
        ((RootActivity) getActivity()).removeFrag(target);
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
    public void toActivity(Activity context, Class<?> clazz, boolean isDefault) {
        RootHelper.toActivity(context, clazz, true, true, false, 0);
    }

    /**
     * @return 获取attach Activity
     */
    public FragmentActivity getActivitys() {
        return activity;
    }
    
    /* -------------------------------------------- impl -------------------------------------------- */

    private OnFirstInitListener onFirstInitListener;

    // 接口OnFirstInitListener
    public interface OnFirstInitListener {
        void firstInit();
    }

    // 对外方式setOnFirstInitListener
    public void setOnFirstInitListener(OnFirstInitListener onFirstInitListener) {
        this.onFirstInitListener = onFirstInitListener;
    }

    // 封装方法firstInitNext
    private void firstInitNext() {
        if (onFirstInitListener != null) {
            onFirstInitListener.firstInit();
        }
    }
}
