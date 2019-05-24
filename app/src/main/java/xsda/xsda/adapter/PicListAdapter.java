package xsda.xsda.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import xsda.xsda.R;
import xsda.xsda.bean.PicListItemBean;

/*
 * Created by qianli.ma on 2019/5/20 0020.
 */
public class PicListAdapter extends RecyclerView.Adapter<PicListHolder> {

    private Context context;
    private List<PicListItemBean> picListItemBeans;

    public PicListAdapter(Context context, List<PicListItemBean> picListItemBeans) {
        this.context = context;
        this.picListItemBeans = picListItemBeans;
    }

    public void notifys(List<PicListItemBean> picListItemBeans) {
        this.picListItemBeans = picListItemBeans;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PicListHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new PicListHolder(LayoutInflater.from(context).inflate(R.layout.item_pic_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PicListHolder holder, int i) {
        PicListItemBean plib = picListItemBeans.get(i);
        // 设置主图
        if (plib.getMainPic() == null) {
            x.image().bind(holder.ivMainPic, plib.getMainPicUrl(), getImgOption());
        } else {
            holder.ivMainPic.setImageBitmap(plib.getMainPic());
        }
        // 设置头像
        if (plib.getHead() == null) {
            x.image().bind(holder.ivHead, plib.getHeadUrl(), getImgOption());
        } else {
            holder.ivHead.setImageBitmap(plib.getHead());
        }
        // 设置数量
        holder.tvGoodNum.setText(plib.getGoodNum());
    }

    @Override
    public int getItemCount() {
        return picListItemBeans != null ? picListItemBeans.size() : 0;
    }

    /**
     * 获取图片属性
     *
     * @return 图片属性
     */
    private ImageOptions getImgOption() {
        ImageOptions.Builder ib = new ImageOptions.Builder();
        ib.setImageScaleType(ImageView.ScaleType.FIT_XY);
        ib.setFailureDrawableId(R.drawable.loadfailed_bg_pure);
        ib.setLoadingDrawableId(R.drawable.loading_bg_pure);
        ib.setUseMemCache(true);
        ib.setIgnoreGif(false);
        return ib.build();
    }
}
