package com.dhu777.picm.data.remote;

import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.PicList;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public class PicRemoteContract {
    public final static String URL_BASE = "https://easy-mock.com/mock/5cd3f635cd8c445f3fa5bf63/picm/";

    public interface Api {
        @GET("allPicInfo")
        Call<PicList> getAllPicInfo();
    }
}
