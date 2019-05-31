package com.dhu777.picm.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.entity.BaseResponse;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.mock.Injection;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dhu777.picm.data.remote.PicRemoteContract.getApiService;
import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class PicRemoteRepo implements PicDataSource,PicUpload{
    private static PicRemoteRepo INSTANCE;
    private static PicRemoteContract.Api apiService;
    private static PicRemoteContract.Api realApi;
    private static PicRemoteContract.Api mockApi;

    protected PicRemoteRepo(){};
    public static PicRemoteRepo getInstance(){
        if(INSTANCE == null){
            INSTANCE = new PicRemoteRepo();
        }
        if(Injection.mode==Injection.MOKE){
            if(mockApi == null){
                mockApi = getApiService(PicRemoteContract.Api.class,Injection.MockURL);
            }
            apiService = mockApi;
        }
        if(Injection.mode==Injection.REAL){
            if(realApi == null){
                realApi = getApiService(PicRemoteContract.Api.class,Injection.RealURL);
            }
            apiService = realApi;
        }
        return INSTANCE;
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
                Log.d("PicRemoteRepo", "onFailure: failed"+t.getCause());
                Exception e = new Exception(t.getMessage());
                t.printStackTrace(); //TODO
                e.printStackTrace();
                callback.onDataNotAvailable(e);

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
                t.printStackTrace();
                if (callback!=null)
                    callback.onUploadFail(t);
            }
        });
    }

    //call when exchange mock mode
    @Override
    public void refresh() {
    }
}
