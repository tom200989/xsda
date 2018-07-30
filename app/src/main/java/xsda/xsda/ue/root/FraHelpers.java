package xsda.xsda.ue.root;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qianli.ma on 2017/9/8.
 */
public class FraHelpers {

    /**
     * 一定要传FragmentActivity类型的对象
     */
    private FragmentActivity activity;

    /**
     * 初始化显示的第一个fragment字节码
     */
    private Class initClass;

    /**
     * 容器,如: R.id.framelayoutId
     */
    private int contain;

    /**
     * fragment字节码集合
     */
    private Class[] clazzs;

    /**
     * TAG对应的class
     */
    private Map<String, Class> tagMap;

    /**
     * class对应的tag
     */
    private Map<Class, String> classMap;

    /**
     * tag集合
     */
    private List<String> tags;

    /**
     * fragment调度器
     */
    private FragmentManager fm;

    /**
     * fragment切换辅助类
     *
     * @param activity  必须为FragmentActivity
     * @param clazzs    fragment集合,如[ AFragment.class, BFragment.class... ]
     * @param initClass 初始的fragment class,如:AFragment.class
     * @param contain   fragment容器ID,如:R.id.fragmentlayout
     */
    public FraHelpers(FragmentActivity activity, Class[] clazzs, Class initClass, int contain) {
        this.activity = activity;
        this.fm = activity.getSupportFragmentManager();
        this.clazzs = clazzs;
        this.initClass = initClass;
        this.contain = contain;
        this.tags = getTags();
        init(initClass);// 初始化fragment
    }

    /**
     * 初始化fragment
     *
     * @param clazz 初始化fragment
     */
    private void init(Class clazz) {
        try {
            // 开启事务
            FragmentTransaction ft = fm.beginTransaction();
            // 通过字节码创建碎片
            Fragment fragment = (Fragment) clazz.newInstance();
            ft.replace(contain, fragment, clazz.getSimpleName());
            // 以类名为tag--> 提交事务
            ft.commit();
            /* 这一句一定要有, 即commit()后要求FT立刻执行, 否则是异步执行 */
            fm.executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 移除指定的fragment
     *
     * @param target 移除指定的fragment
     */
    public void remove(Class target) {
        // 1.开启事务
        Fragment fragment;
        FragmentTransaction ft = fm.beginTransaction();
        // 3.以类名为tag, 查找对应的fragment
        String tag = target.getSimpleName();
        fragment = fm.findFragmentByTag(tag);
        if (fragment != null) {
            // 3.2.移除当前
            ft.remove(fragment);
            ft.commit();
            /* 这一句一定要有, 即commit()后要求FT立刻执行, 否则是异步执行 */
            fm.executePendingTransactions();
        }
    }

    /**
     * 切换fragment
     *
     * @param clazz    需要切换的fragment class
     * @param isReload 是否强制重载
     */
    public void transfer(Class clazz, boolean isReload) {

        try {
            // 1.开启事务
            Fragment fragment;

            // 2.隐藏全部的fragment
            for (String tag : tags) {
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment_temp = fm.findFragmentByTag(tag);
                if (fragment_temp != null) {
                    ft.hide(fragment_temp);
                    ft.commit();
                    fm.executePendingTransactions();
                }
            }

            // 3.以类名为tag, 查找对应的fragment
            String tag = clazz.getSimpleName();
            fragment = fm.findFragmentByTag(tag);
            if (fragment == null) {
                FragmentTransaction ft = fm.beginTransaction();
                // 3.1.创建fragment
                fragment = (Fragment) clazz.newInstance();
                // 3.2.添加到容器, 以类名为tag
                ft.add(contain, fragment, tag);
                ft.show(fragment);
                ft.commit();
                fm.executePendingTransactions();
            } else {
                
                /* 如果收到重载指令--> 执行reload(clazz) */
                if (isReload) {
                    reload(clazz);
                } else {// 否则正常显示
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.show(fragment);
                    ft.commit();
                    fm.executePendingTransactions();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重载指定fragment
     *
     * @param clazz 目标
     */
    private void reload(Class clazz) {
        try {
            // 1.先删除
            String tag = clazz.getSimpleName();
            FragmentTransaction ft1 = fm.beginTransaction();
            ft1.remove(fm.findFragmentByTag(tag));
            ft1.commit();
            fm.executePendingTransactions();
            
            // 2.再添加
            FragmentTransaction ft2 = fm.beginTransaction();
            Fragment newFragment = (Fragment) clazz.newInstance();
            ft2.add(contain, newFragment, tag);
            ft2.show(newFragment);
            ft2.commit();
            /* 这一句一定要有, 即commit()后要求FT立刻执行, 否则是异步执行 */
            fm.executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新加载所有的fragment
     *
     * @param initClass 需要显示的首屏fragment
     */
    public void reloadAll(Class initClass) {
        try {
            Map<String, Fragment> fragmentMap = new HashMap<>();
            Fragment showFragment = null;// 需要显示的fragment
            FragmentTransaction ft = fm.beginTransaction();
            // 1. 重新填充fragment到contain
            for (String tag : tagMap.keySet()) {
                Fragment fragment_temp = fm.findFragmentByTag(tag);
                if (fragment_temp != null) {
                    // 删除原先的fragment
                    ft.remove(fragment_temp);
                    // 再通过字节码文件进行新创
                    fragment_temp = (Fragment) tagMap.get(tag).newInstance();
                    if (showFragment == null) {// 此处防止空指针
                        showFragment = fragment_temp;
                    }
                    // 重新添加到容器中
                    ft.add(contain, fragment_temp, tag);
                    // 装载进临时集合(在下一步显示指定界面做准备)
                    fragmentMap.put(tag, fragment_temp);
                }
            }
            // 2.重置所有的fragment为hide
            for (String tag : fragmentMap.keySet()) {
                ft.hide(fragmentMap.get(tag));
            }
            // 3.通过initClass字节码名称找出对应的fragment
            for (String tag : fragmentMap.keySet()) {
                if (tag.equalsIgnoreCase(initClass.getSimpleName())) {
                    showFragment = fragmentMap.get(tag);
                }
            }
            // 4.提交
            ft.show(showFragment);
            ft.commit();
            /* 这一句一定要有, 即commit()后要求FT立刻执行, 否则是异步执行 */
            fm.executePendingTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把所有fragment字节码文件提取出tag集合
     *
     * @return tag集合 { Afragment.class --> Afragment }
     */
    private List<String> getTags() {
        List<String> tags = new ArrayList<>();
        tagMap = new HashMap<>();
        classMap = new HashMap<>();
        for (Class clazz : clazzs) {
            tags.add(clazz.getSimpleName());
            tagMap.put(clazz.getSimpleName(), clazz);
            classMap.put(clazz, clazz.getSimpleName());
        }
        return tags;
    }

}
