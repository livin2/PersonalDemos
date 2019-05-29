package com.dhu777.picm.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.entity.BaseResponse;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.UserToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import com.dhu777.picm.data.remote.PicRemoteContract.PicList;

import static com.dhu777.picm.data.remote.PicRemoteContract.getApiService;
import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class PicRemoteRepo implements PicDataSource,PicUpload{
    private static PicRemoteRepo INSTANCE;

    private static PicRemoteContract.Api apiService;

    private PicRemoteRepo(){};
    public static PicRemoteRepo getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PicRemoteRepo();
            apiService = getApiService(PicRemoteContract.Api.class);
        }
        return INSTANCE;
    }


    @Override
    public void fetchUserList(@NonNull UserToken userToken,
                              @NonNull final FetchPicsCallback callback) {
        Call<List<PicInfo>> call = apiService.getUserPicList(checkNotNull(userToken.getName()),
                checkNotNull(userToken.getToken()));
        call.enqueue(new Callback<List<PicInfo>>() {
            @Override
            public void onResponse(Call<List<PicInfo>> call, Response<List<PicInfo>> response) {
                List<PicInfo> pics = checkNotNull(response.body());
                callback.onDataLoaded(pics);
            }

            @Override
            public void onFailure(Call<List<PicInfo>> call, Throwable t) {
                Log.d("PicRemoteRepo", "onFailure:fetchUserList failed");
                t.printStackTrace(); //TODO
            }
        });
    }

    @Override
    public void fetchPicList(@NonNull final FetchPicsCallback callback) {
        Call<List<PicInfo>> call = apiService.getAllPicInfo();
        call.enqueue(new Callback<List<PicInfo>>() {
            @Override
            public void onResponse(Call<List<PicInfo>> call, Response<List<PicInfo>> response) {
                Log.d("PicRemoteRepo", "onResponse: Success");
                List<PicInfo> pics = checkNotNull(response.body());

                Log.d("PicRemoteRepo", "onResponse: picId:"+pics.get(0).getPicId());
                Log.d("PicRemoteRepo", "onResponse: picName:"+pics.get(0).getPicName());
                Log.d("PicRemoteRepo", "onResponse: picUrl:"+pics.get(0).getPicURL());
                callback.onDataLoaded(pics);
            }

            @Override
            public void onFailure(Call<List<PicInfo>> call, Throwable t) {
                Log.d("PicRemoteRepo", "onFailure: failed");
                t.printStackTrace(); //TODO
            }
        });
    }

    //TODO
    @Override
    public void getPic(@NonNull String picId, @NonNull GetPicCallback callback) {

    }

    public void upload(@NonNull byte[] file,@NonNull String sourceid,
                       @NonNull String jwt, final UpPicCallback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file",sourceid, requestBody);


        apiService.uploadSinglePic(part,jwt).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                Log.d("PicRemoteRepo", "onResponse:");
                BaseResponse body = response.body();
                if (callback!=null && body!=null)
                    callback.onPicUploaded(body);
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                Log.d("PicRemoteRepo", "onFailure:"+t.getMessage());
                if (callback!=null)
                    callback.onUploadFail(t);
            }
        });
    }
}
