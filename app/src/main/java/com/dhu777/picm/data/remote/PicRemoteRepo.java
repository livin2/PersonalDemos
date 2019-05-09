package com.dhu777.picm.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.PicList;
import com.dhu777.picm.util.ComUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class PicRemoteRepo implements PicDataSource {
    private static PicRemoteRepo INSTANCE;
    private static Retrofit retrofitInstance;
    private static PicRemoteContract.Api apiService;

    private PicRemoteRepo(){};
    public static PicRemoteRepo getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PicRemoteRepo();
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(PicRemoteContract.URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            apiService = retrofitInstance.create(PicRemoteContract.Api.class);
        }
        return INSTANCE;
    }


    @Override
    public void fetchPicList(@NonNull final FetchPicsCallback callback) {
        Call<PicList> call = apiService.getAllPicInfo();
        call.enqueue(new Callback<PicList>() {
            @Override
            public void onResponse(Call<PicList> call, Response<PicList> response) {
                Log.d("PicRemoteRepo", "onResponse: Success");
                List<PicInfo> pics = checkNotNull(response.body().getpicList());

                Log.d("PicRemoteRepo", "onResponse: picId:"+pics.get(0).getPicId());
                Log.d("PicRemoteRepo", "onResponse: picName:"+pics.get(0).getPicName());
                Log.d("PicRemoteRepo", "onResponse: picPicCreateDate:"+pics.get(0).getPicCreateDate());
                callback.onDataLoaded(pics);
            }

            @Override
            public void onFailure(Call<PicList> call, Throwable t) {
                Log.d("PicRemoteRepo", "onResponse: failed");
            }
        });
    }

    @Override
    public void getPic(@NonNull String picId, @NonNull GetPicCallback callback) {

    }

    @Override
    public void refresh() {

    }
}
