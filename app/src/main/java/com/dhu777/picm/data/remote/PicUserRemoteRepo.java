package com.dhu777.picm.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.LoginRepositrory;
import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.mock.Injection;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dhu777.picm.data.remote.PicRemoteContract.getApiService;
import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class PicUserRemoteRepo implements PicDataSource{
    private static PicUserRemoteRepo INSTANCE;
    private static PicRemoteContract.Api apiService;
    private static PicRemoteContract.Api realApi;
    private static PicRemoteContract.Api mockApi;

    private UserToken userToken;
    protected PicUserRemoteRepo(){};
    public static PicUserRemoteRepo getInstance(@NonNull UserToken userToken){
        if(INSTANCE == null){
            INSTANCE = new PicUserRemoteRepo();
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
        INSTANCE.userToken = userToken;
        return INSTANCE;
    }

    @Override
    public void refresh() {
        //pass
    }

    @Override
    public void fetchPicList(@NonNull final FetchPicsCallback callback) {
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
    public void getPic(@NonNull String picId, @NonNull GetPicCallback callback) {
        //todo
    }
}
