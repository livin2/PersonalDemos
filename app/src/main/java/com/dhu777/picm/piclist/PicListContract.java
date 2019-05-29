package com.dhu777.picm.piclist;

import com.dhu777.picm.BasePresenter;
import com.dhu777.picm.BaseView;

public interface PicListContract {
    interface View extends BaseView<Presenter> {
        void showPictures();
        void setProgressIndicator(boolean active);
        void showLoadingPicturesError();
        void setLayoutSpanCount(int count);
        void refreshLayout();
    }

    interface Presenter extends BasePresenter {
        void openPictureDetails();
        void refreshPicList();
    }
}
