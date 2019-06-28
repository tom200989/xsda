package xsda.xsda.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import xsda.xsda.R;
import xsda.xsda.bean.MainBean;

/*
 * Created by qianli.ma on 2019/6/20 0020.
 */
public class MainRightAdapter extends RecyclerView.Adapter<MainRightHolder> {

    private Context context;
    private List<MainBean.MainRightBean> mainRightBeans;

    public MainRightAdapter(Context context, List<MainBean.MainRightBean> mainRightBeans) {
        this.context = context;
        this.mainRightBeans = mainRightBeans;
    }

    public void notifys(List<MainBean.MainRightBean> mainRightBeans) {
        this.mainRightBeans = mainRightBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainRightHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MainRightHolder(LayoutInflater.from(context).inflate(R.layout.item_main_right_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainRightHolder mainRightHolder, int i) {
        MainBean.MainRightBean mrb = mainRightBeans.get(i);
        mainRightHolder.ivItemPic.setImageDrawable(mrb.getRightItemPic());
        mainRightHolder.tvItemTitle.setText(mrb.getRightItemTitle());
        mainRightHolder.rlItem.setOnClickListener(v -> rightItemClickNext(mrb));
    }

    @Override
    public int getItemCount() {
        return mainRightBeans != null ? mainRightBeans.size() : 0;
    }

    private OnRightItemClickListener onRightItemClickListener;

    // Inteerface--> 接口OnRightItemClickListener
    public interface OnRightItemClickListener {
        void rightItemClick(MainBean.MainRightBean mrb);
    }

    // 对外方式setOnRightItemClickListener
    public void setOnRightItemClickListener(OnRightItemClickListener onRightItemClickListener) {
        this.onRightItemClickListener = onRightItemClickListener;
    }

    // 封装方法rightItemClickNext
    private void rightItemClickNext(MainBean.MainRightBean mrb) {
        if (onRightItemClickListener != null) {
            onRightItemClickListener.rightItemClick(mrb);
        }
    }
}
