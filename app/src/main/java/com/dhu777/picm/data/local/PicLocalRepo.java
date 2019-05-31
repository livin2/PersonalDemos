package com.dhu777.picm.data.local;

import androidx.annotation.NonNull;

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


}
