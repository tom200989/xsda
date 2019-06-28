package xsda.xsda.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xsda.xsda.R;

/*
 * Created by qianli.ma on 2019/6/20 0020.
 */
public class MainRightHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlItem;// 总布局
    public ImageView ivItemPic;// 货品图
    public TextView tvItemTitle;// 货品标题

    public MainRightHolder(@NonNull View itemView) {
        super(itemView);
        rlItem = itemView.findViewById(R.id.rl_right_item);
        ivItemPic = itemView.findViewById(R.id.iv_right_item_pic);
        tvItemTitle = itemView.findViewById(R.id.tv_right_item_title);
    }
}
