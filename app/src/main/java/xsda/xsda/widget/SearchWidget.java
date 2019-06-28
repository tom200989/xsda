package xsda.xsda.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/*
 * Created by qianli.ma on 2019/6/27 0027.
 */
@SuppressLint("AppCompatCustomView")
public class SearchWidget extends ImageView implements View.OnTouchListener {

    private long downTime;

    public SearchWidget(Context context) {
        this(context, null, 0);
    }

    public SearchWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                long upTime = System.currentTimeMillis();
                long delTime = Math.abs(upTime - downTime);
                if (delTime < 500) {// 小于500ms -- 点击行为
                    Log.i("ma_downtouch", "down touch");
                    clickItNext();
                }
                break;
        }
        return true;
    }

    private OnClickItListener onClickItListener;

    // Inteerface--> 接口OnClickItListener
    public interface OnClickItListener {
        void clickIt();
    }

    // 对外方式setOnClickItListener
    public void setOnClickItListener(OnClickItListener onClickItListener) {
        this.onClickItListener = onClickItListener;
    }

    // 封装方法clickItNext
    private void clickItNext() {
        if (onClickItListener != null) {
            onClickItListener.clickIt();
        }
    }
}
