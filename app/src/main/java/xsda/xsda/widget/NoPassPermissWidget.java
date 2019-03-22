package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.hiber.tools.layout.PercentRelativeLayout;

import xsda.xsda.R;

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
        View viewById = inflate.findViewById(R.id.tv_no_pass_cancel);
        View viewById = inflate.findViewById(R.id.tv_no_pass_ok);
    }
}
