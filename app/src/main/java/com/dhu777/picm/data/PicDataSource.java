package com.dhu777.picm.data;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.entity.PicInfo;

import java.util.List;

public interface PicDataSource {
    interface FetchPicsCallback{
        void onDataLoaded(List<PicInfo> picList);
        void onDataNotAvailable(Exception e);
    }

    interface GetPicCallback{
        void onDataLoaded(PicInfo pic);
        void onDataNotAvailable(Exception e);
    }

    void fetchPicList(@NonNull FetchPicsCallback callback);
    void getPic(@NonNull String picId,@NonNull GetPicCallback callback);
    void refresh();
}
