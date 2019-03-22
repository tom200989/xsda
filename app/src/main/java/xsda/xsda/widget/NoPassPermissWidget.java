package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.hiber.tools.layout.PercentRelativeLayout;

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
    }
}
