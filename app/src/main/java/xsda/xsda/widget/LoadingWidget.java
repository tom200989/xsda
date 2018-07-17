package xsda.xsda.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import xsda.xsda.R;

/**
 * Created by qianli.ma on 2018/6/22 0022.
 */

public class LoadingWidget extends RelativeLayout{

    private RelativeLayout rlAll;

    public LoadingWidget(Context context) {
        this(context, null,0);
    }

    public LoadingWidget(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.widget_loading,this);
        rlAll = findViewById(R.id.rl_loading_all);
        rlAll.setOnClickListener(v -> {});
    }
}
