package com.dhu777.picm.piclist;

import com.dhu777.picm.BasePresenter;
import com.dhu777.picm.BaseView;

public interface PicListContract {
    interface View extends BaseView<Presenter> {
        void showPictures();
        void setProgressIndicator(boolean active);
        void showLoadingPicturesError();
    }

    interface Presenter extends BasePresenter {
        void openPictureDetails();

    }
}
