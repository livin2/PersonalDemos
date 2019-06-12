package com.dhu777.picm.data.remote;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.entity.BaseResponse;

public interface PicUpload {
    interface UpPicCallback{
        void onPicUploaded(@NonNull BaseResponse response);
        void onUploadFail(@NonNull Throwable t);
    }

    void upload(byte[] file,String sourceid,String jwt,UpPicCallback callback);
}
