package xsda.xsda.helper;

import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by qianli.ma on 2017/6/22.
 */

public abstract class TimerHelper {

    private TimerTask timerTask;
    private Context context;
    private Timer timer;

    public TimerHelper(Context context) {
        this.context = context;
    }

    /**
     * 启动
     *
     * @param period 毫秒
     */
    public void start(int period) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                doSomething();
            }
        };
        timer.schedule(timerTask, 0, period);
    }

    /**
     * 延迟启动
     * @param delay 单位毫秒
     */
    public void startDelay(int delay) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                doSomething();
            }
        };
        timer.schedule(timerTask, delay);
    }

    /**
     * @param delay  单位毫秒
     * @param period 单位毫秒
     */
    public void start(int delay, int period) {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                doSomething();
            }
        };
        timer.schedule(timerTask, delay, period);
    }

    /**
     * 停止
     */
    public void stop() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public abstract void doSomething();

}
