package xsda.xsda.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.p_circleview.p_circleview.core.CornerMaView;

import xsda.xsda.R;

/*
 * Created by qianli.ma on 2019/5/20 0020.
 */
public class VideoListHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rlItemAll;// 总布局
    public CornerMaView ivMainPic;// 主图
    public TextView tvGoodNum;// 点赞数

    public VideoListHolder(@NonNull View itemView) {
        super(itemView);
        rlItemAll = itemView.findViewById(R.id.rl_item_video_list_all);
        ivMainPic = itemView.findViewById(R.id.iv_item_video_list_mainpic);
        tvGoodNum = itemView.findViewById(R.id.tv_item_video_list_good);
    }
}
