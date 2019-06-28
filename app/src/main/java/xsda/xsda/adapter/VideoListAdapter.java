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
import xsda.xsda.bean.VideoListItemBean;

/*
 * Created by qianli.ma on 2019/5/20 0020.
 */
public class VideoListAdapter extends RecyclerView.Adapter<PicListHolder> {

    private Context context;
    private List<VideoListItemBean> picListItemBeans;

    public VideoListAdapter(Context context, List<VideoListItemBean> picListItemBeans) {
        this.context = context;
        this.picListItemBeans = picListItemBeans;
    }

    public void notifys(List<VideoListItemBean> picListItemBeans) {
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
        VideoListItemBean vlib = picListItemBeans.get(i);
        // 设置点击
        holder.rlItemAll.setOnClickListener(v -> videoItemClickNext(vlib));
        // 设置主图
        if (vlib.getMainPic() == null) {
            x.image().bind(holder.ivMainPic, vlib.getMainPicUrl(), getImgOption());
        } else {
            holder.ivMainPic.setImageBitmap(vlib.getMainPic());
        }
        // 设置数量
        holder.tvGoodNum.setText(vlib.getGoodNum());
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

    /* -------------------------------------------- impl -------------------------------------------- */

    private OnVideoItemClickListener onVideoItemClickListener;

    // Inteerface--> 接口OnPicItemClickListener
    public interface OnVideoItemClickListener {
        void videoItemClick(VideoListItemBean picItemBean);
    }

    // 对外方式setOnPicItemClickListener
    public void setOnVideoItemClickListener(OnVideoItemClickListener onVideoItemClickListener) {
        this.onVideoItemClickListener = onVideoItemClickListener;
    }

    // 封装方法picItemClickNext
    private void videoItemClickNext(VideoListItemBean videoItemBean) {
        if (onVideoItemClickListener != null) {
            onVideoItemClickListener.videoItemClick(videoItemBean);
        }
    }
}
