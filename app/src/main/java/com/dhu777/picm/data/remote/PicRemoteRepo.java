package com.dhu777.picm.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.entity.BaseResponse;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.mock.Injection;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dhu777.picm.data.remote.PicRemoteContract.checkCode;
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
        Log.d("PicRemoteRepo", "fetchPicList");
        Call<List<PicInfo>> call = apiService.getAllPicInfo();
        call.enqueue(new Callback<List<PicInfo>>() {
            @Override
            public void onResponse(Call<List<PicInfo>> call, Response<List<PicInfo>> response) {
                try{
                    if(!checkCode(response.code())){
                        callback.onDataNotAvailable(new Exception("Code:"+response.code()));
                    }
                    Log.d("PicRemoteRepo", "onResponse: Success");
                    List<PicInfo> pics = checkNotNull(response.body());
                    Log.d("PicRemoteRepo", "onResponse: picId 0:"+pics.get(0).getPicId());
                    Log.d("PicRemoteRepo", "onResponse: picName 0:"+pics.get(0).getPicName());
                    Log.d("PicRemoteRepo", "onResponse: picUrl 0:"+pics.get(0).getPicURL());
                    callback.onDataLoaded(pics);
                }catch (NullPointerException e){
                    callback.onDataNotAvailable(new NullPointerException("null response"));
                }
            }

            @Override
            public void onFailure(Call<List<PicInfo>> call, Throwable t) {
                Log.d("PicRemoteRepo", "onFailure: failed"+t.getCause());
                callback.onDataNotAvailable(new Exception(t.getMessage()));
            }
        });
    }

    //pass
    @Override
    public void getPic(@NonNull String picId, @NonNull GetPicCallback callback) {

    }

    @Override
    public void deletePic(@NonNull String picId,@NonNull String jwt, @Nullable final DelPicCallback callback) {
        Call<ResponseBody> call = apiService.deledeSinglePic(picId,jwt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!checkCode(response.code())){
                    callback.onDataNotAvailable(new Exception("Code:"+response.code()));
                }
                if (callback!=null){
                    if(response.body()!=null){
                        BaseResponse<ResponseBody> respmsg = new BaseResponse<ResponseBody>(200,"success");
                        respmsg.setData(response.body());
                        callback.onDataLoaded(respmsg);
                    }else {
                        callback.onDataLoaded(new BaseResponse<Void>(200,"success"));
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onDataNotAvailable(t);
            }
        });
    }

    public void upload(@NonNull byte[] file,@NonNull String sourceid,
                       @NonNull String jwt, final UpPicCallback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file",sourceid, requestBody);
        apiService.uploadSinglePic(part,jwt).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                if(!checkCode(response.code())){
                    callback.onUploadFail(new Exception("Fail Code:"+response.code()));
                }
                Log.d("PicRemoteRepo", "onResponse:"+response);
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
