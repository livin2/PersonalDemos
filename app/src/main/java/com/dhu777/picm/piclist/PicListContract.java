package com.dhu777.picm.piclist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhu777.picm.BasePresenter;
import com.dhu777.picm.BaseView;
import com.dhu777.picm.data.PicInfoRepositrory;
import com.dhu777.picm.data.entity.PicInfo;

import java.util.List;

public interface PicListContract {
    interface View extends BaseView<Presenter> {
        void showPictures(List<PicInfo> pics);
        void showLoadingPicturesError();
        void setLayoutSpanCount(int count);
    }

    interface Presenter extends BasePresenter {
        void refreshList(@Nullable RefreshCallBack callBack);
        void refreshList();
        void changeRepo(@NonNull PicInfoRepositrory Repo);
    }

    interface RefreshCallBack{
        void onFinsh();
    }
}
