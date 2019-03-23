package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiber.tools.Lgg;
import com.hiber.tools.layout.PercentRelativeLayout;

import xsda.xsda.R;
import xsda.xsda.utils.Cons;

/*
 * Created by qianli.ma on 2019/3/22 0022.
 */
public class NoPassPermissWidget extends PercentRelativeLayout {

    public NoPassPermissWidget(Context context) {
        this(context, null, 0);
    }

    public NoPassPermissWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoPassPermissWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View inflate = inflate(context, R.layout.widget_no_pass_permiss, this);
        ImageView ivBg = inflate.findViewById(R.id.iv_bg_no_pass);
        TextView tv_got_it = inflate.findViewById(R.id.tv_no_pass_got_it);
        ivBg.setOnClickListener(v -> Lgg.t(Cons.TAG).ii("no pass permiss bg click "));
        tv_got_it.setOnClickListener(v -> exit());

    }

    /**
     * 退出APP
     */
    private void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
