package com.dhu777.picm.picdetail;

import android.content.Context;
import android.content.Intent;

import com.dhu777.picm.BasePresenter;
import com.dhu777.picm.BaseView;
import com.dhu777.picm.data.entity.PicInfo;

public interface PicDetailContract {
    interface View extends BaseView<Presenter> {
        void showToast(String msg);
        void showToast(int Rid);
        void showShare(Intent shareIntent);
        void showEdit(Intent editIntent);
        Context getApplicationContext();
        void finish();
    }

    interface Presenter extends BasePresenter {
        void savePic(PicInfo picInfo);
        void sharePic(PicInfo picInfo);
        void editPic(PicInfo picInfo);
        void deletePic(PicInfo picInfo);
        boolean isLoginIn();
        boolean checkUser(String username);
    }
}
