package com.dhu777.picm.data.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.entity.UserToken;

public class PicLocalRepo implements PicDataSource {

    @Override
    public void refresh() {

    }

    //TODO
    @Override
    public void fetchPicList(@NonNull FetchPicsCallback callback) {
        //pass
    }

    @Override
    public void getPic(@NonNull String picId, @NonNull GetPicCallback callback) {

    }

    @Override
    public void deletePic(@NonNull String picId, @NonNull String jwt, @Nullable DelPicCallback callback) {

    }




}
