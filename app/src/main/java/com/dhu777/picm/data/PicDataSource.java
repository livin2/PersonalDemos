package com.dhu777.picm.data;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.entity.BaseResponse;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.UserToken;

import java.util.List;

public interface PicDataSource {
    //图片列表
    interface FetchPicsCallback{
        void onDataLoaded(@NonNull List<PicInfo> picList);
        void onDataNotAvailable(@NonNull Exception e);
    }

    //原图
    interface GetPicCallback{
        void onDataLoaded(@NonNull PicInfo pic);
        void onDataNotAvailable(@NonNull Exception e);
    }

    void refresh();
    void fetchPicList(@NonNull FetchPicsCallback callback);
    void getPic(@NonNull String picId,@NonNull GetPicCallback callback);
}
