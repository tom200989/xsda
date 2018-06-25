package xsda.xsda.helper;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

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
            String updatedFile = avObject.getString(Cons.LeanClound.CLASS_UPDATE_FIELD_NEW_VERSION_FILE);
            Lgg.t(Cons.TAG).ii("updatedAt: " + updatedAt);
            Lgg.t(Cons.TAG).ii("newVersionCode: " + newVersionCode);
            Lgg.t(Cons.TAG).ii("updatedFile: " + updatedFile);
        } else {
            Lgg.t(Cons.TAG).ee(e.getMessage());
        }
    }
}
