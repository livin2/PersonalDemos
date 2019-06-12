package com.dhu777.picm.piclist;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.PicInfoRepositrory;
import com.dhu777.picm.data.entity.PicInfo;

import java.util.List;

import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class PicListPresenter implements PicListContract.Presenter{
    private  PicInfoRepositrory mPicInfoRepo;
    private final PicListContract.View mPicListView;
    private PicListContract.RefreshCallBack refreshCallBack = null;

    public PicListPresenter(@NonNull PicInfoRepositrory picInfoRepo,@NonNull PicListContract.View picListView) {
        mPicInfoRepo = checkNotNull(picInfoRepo);
        mPicListView = checkNotNull(picListView);
        picListView.setPresenter(this);
    }

    @Override
    public void start() {
        loadPicList();
    }

    private void loadPicList(){
        mPicInfoRepo.fetchPicList(new PicDataSource.FetchPicsCallback() {
            @Override
            public void onDataLoaded(@NonNull List<PicInfo> picList) {
                if(!mPicListView.isActive()){
                    Log.d("PicListPresenter", "onDataLoaded: View not Active");
                    return;
                }
                processPicList(picList);
            }

            @Override
            public void onDataNotAvailable(@NonNull Exception e) {
                Log.d("PicListPresenter", "onDataNotAvailable:");
                if(!mPicListView.isActive()){
                    return;
                }
                mPicListView.showLoadingPicturesError();
                e.printStackTrace();
            }
        });
    }

    private void processPicList(List<PicInfo> picList){
        if(picList.isEmpty()){
            return;
        }else {
            if(refreshCallBack!=null){
                refreshCallBack.onFinsh();
                refreshCallBack =null;
            }
            mPicListView.showPictures(picList);
        }
    }

    @Override
    public void refreshList(@Nullable PicListContract.RefreshCallBack callBack) {
        refreshCallBack = callBack;
        refreshList();
    }

    @Override
    public void refreshList() {
        mPicInfoRepo.refresh();
        loadPicList();
    }

    @Override
    public void changeRepo(@NonNull PicInfoRepositrory Repo) {
        mPicInfoRepo = Repo;
    }
}
