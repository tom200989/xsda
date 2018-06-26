package xsda.xsda.helper;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

import xsda.xsda.bean.UpdateBean;
import xsda.xsda.utils.Cons;
import xsda.xsda.utils.Lgg;
import xsda.xsda.utils.Ogg;

/**
 * Created by qianli.ma on 2018/6/25 0025.
 */

public class GetUpdateHelper extends GetCallback<AVObject> {

    /**
     * 获取新的版本
     */
    public void getNewVersion() {
        AVQuery<AVObject> avQuery = new AVQuery<>(Cons.LeanClound.CLASS_UPDATE_NAME);
        avQuery.getInBackground(Cons.LeanClound.CLASS_UPDATE_OBJECTID, this);
    }

    @Override
    public void done(AVObject avObject, AVException e) {
        if (e == null) {
            
            String updatedAt = Ogg.turnDateToString(avObject.getUpdatedAt());
            String newVersionCode = avObject.getString(Cons.LeanClound.CLASS_UPDATE_FIELD_NEW_VERSION_CODE);
            String newVersionFix = avObject.getString(Cons.LeanClound.CLASS_UPDATE_FIELD_NEW_VERSION_FIX);
            String newVersionFileUrl = avObject.getAVFile(Cons.LeanClound.CLASS_UPDATE_FIELD_NEW_VERSION_FILE).getUrl();
            String newVersionSize = avObject.getString(Cons.LeanClound.CLASS_UPDATE_FIELD_NEW_VERSION_SIZE);
            
            UpdateBean updateBean = new UpdateBean();
            updateBean.setUpdatedAt(updatedAt);
            updateBean.setNewVersionCode(newVersionCode);
            updateBean.setNewVersionFix(newVersionFix);
            updateBean.setNewVersionFileUrl(newVersionFileUrl);
            updateBean.setNewVersionSize(Long.valueOf(newVersionSize));
            
            getUpdateNext(updateBean);
            Lgg.t(Cons.TAG).ii(JSONObject.toJSONString(updateBean));
        } else {
            Lgg.t(Cons.TAG).ee(e.getMessage());
            exceptionNext(e);
        }
    }

    private OnGetUpdateListener onGetUpdateListener;

    // 接口OnGetUpdateListener
    public interface OnGetUpdateListener {
        void getUpdate(UpdateBean updateBean);
    }

    // 对外方式setOnGetUpdateListener
    public void setOnGetUpdateListener(OnGetUpdateListener onGetUpdateListener) {
        this.onGetUpdateListener = onGetUpdateListener;
    }

    // 封装方法getUpdateNext
    private void getUpdateNext(UpdateBean updateBean) {
        if (onGetUpdateListener != null) {
            onGetUpdateListener.getUpdate(updateBean);
        }
    }

    private OnExceptionListener onExceptionListener;

    // 接口OnExceptionListener
    public interface OnExceptionListener {
        void exception(Exception e);
    }

    // 对外方式setOnExceptionListener
    public void setOnExceptionListener(OnExceptionListener onExceptionListener) {
        this.onExceptionListener = onExceptionListener;
    }

    // 封装方法exceptionNext
    private void exceptionNext(Exception e) {
        if (onExceptionListener != null) {
            onExceptionListener.exception(e);
        }
    }
}
