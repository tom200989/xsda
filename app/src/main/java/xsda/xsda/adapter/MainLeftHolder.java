package xsda.xsda.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xsda.xsda.R;

/*
 * Created by qianli.ma on 2019/6/18 0018.
 */
public class MainLeftHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlItemAll;
    public ImageView ivItemTag;
    public TextView tvItemContent;

    public MainLeftHolder(@NonNull View itemView) {
        super(itemView);
        rlItemAll = itemView.findViewById(R.id.rl_main_item_left_all);
        ivItemTag = itemView.findViewById(R.id.iv_main_item_left_tag);
        tvItemContent = itemView.findViewById(R.id.tv_main_item_left_content);
    }
}
