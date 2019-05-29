package com.dhu777.picm.util;

import android.util.Log;

import com.dhu777.picm.data.entity.PicInfo;

public class DataHolder {
    private static final DataHolder instance = new DataHolder();
    private DataHolder(){}
    public static DataHolder getInstance() {
        return instance;
    }

    private PicInfo imageInfo;

    public PicInfo getImageInfo() {
        if (imageInfo==null){
            Log.d("DataHolder", "getImageInfo: null");
            return new PicInfo();
        }
        return imageInfo;
    }

    public void setImageInfo(PicInfo imageInfo) {
        this.imageInfo = imageInfo;
    }
}
