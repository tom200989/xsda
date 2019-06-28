package xsda.xsda.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import xsda.xsda.R;
import xsda.xsda.bean.MainBean;

import static android.graphics.Typeface.defaultFromStyle;

/*
 * Created by qianli.ma on 2019/6/18 0018.
 */
public class MainLeftAdapter extends RecyclerView.Adapter<MainLeftHolder> {

    private Context context;
    private List<MainBean> mainLists;

    public MainLeftAdapter(Context context, List<MainBean> mainLists) {
        this.context = context;
        this.mainLists = mainLists;
    }

    public void notifys(List<MainBean> mainLists) {
        this.mainLists = mainLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainLeftHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MainLeftHolder(LayoutInflater.from(context).inflate(R.layout.item_main_left_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainLeftHolder mainLeftHolder, int i) {
        MainBean mlb = mainLists.get(i);
        int colorCheck = context.getResources().getColor(R.color.colorMainLeftItemCheck);
        int colorUnCheck = context.getResources().getColor(R.color.colorMainLeftItemUnCheck);
        int colorTextUnCheck = context.getResources().getColor(R.color.colorMainLeftItemTextUnCheck);

        mainLeftHolder.ivItemTag.setBackgroundColor(mlb.isChoice() ? colorCheck : colorUnCheck);
        mainLeftHolder.tvItemContent.setText(mlb.getLeftCateforyName());
        mainLeftHolder.tvItemContent.setTextColor(mlb.isChoice() ? colorCheck : colorTextUnCheck);
        mainLeftHolder.tvItemContent.setTypeface(mlb.isChoice() ? defaultFromStyle(Typeface.BOLD) : defaultFromStyle(Typeface.NORMAL));
        mainLeftHolder.rlItemAll.setOnClickListener(v -> {
            changeState(i);// 切换状态
            notifyDataSetChanged();// 刷新适配器
            leftItemClickNext(mlb, i);// 回调
        });
    }

    /**
     * 切换状态
     *
     * @param position 索标
     */
    private void changeState(int position) {
        for (int i = 0; i < mainLists.size(); i++) {
            mainLists.get(i).setChoice(i == position);
        }
    }

    @Override
    public int getItemCount() {
        return mainLists != null ? mainLists.size() : 0;
    }

    private OnLeftItemClickListener onLeftItemClickListener;

    // Inteerface--> 接口OnLeftItemClickListener
    public interface OnLeftItemClickListener {
        void leftItemClick(MainBean mainBean, int position);
    }

    // 对外方式setOnLeftItemClickListener
    public void setOnLeftItemClickListener(OnLeftItemClickListener onLeftItemClickListener) {
        this.onLeftItemClickListener = onLeftItemClickListener;
    }

    // 封装方法leftItemClickNext
    private void leftItemClickNext(MainBean mainbean, int position) {
        if (onLeftItemClickListener != null) {
            onLeftItemClickListener.leftItemClick(mainbean, position);
        }
    }
}
