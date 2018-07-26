package xsda.xsda.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by qianli.ma on 2018/6/21 0021.
 */

public class Tgg {

    /**
     * @param context  环境
     * @param tip      提示
     * @param duration 时长
     */
    public static void show(Context context, String tip, int duration) {
        if (duration == 0) {
            duration = 2000;
        }
        String threadName = Thread.currentThread().getName();
        if (threadName.equalsIgnoreCase("main")) {
            Toast.makeText(context, tip, duration).show();
        } else {
            Activity activity = (Activity) context;
            int finalDuration = duration;
            activity.runOnUiThread(() -> Toast.makeText(activity, tip, finalDuration).show());
        }
    }

    /**
     * @param context  环境
     * @param stringId 提示
     * @param duration 时长
     */
    public static void show(Context context, @StringRes int stringId, int duration) {
        if (duration == 0) {
            duration = 2000;
        }
        String threadName = Thread.currentThread().getName();
        if (threadName.equalsIgnoreCase("main")) {
            Toast.makeText(context, context.getString(stringId), duration).show();
        } else {
            Activity activity = (Activity) context;
            int finalDuration = duration;
            activity.runOnUiThread(() -> Toast.makeText(activity, context.getString(stringId), finalDuration).show());
        }
    }
}
