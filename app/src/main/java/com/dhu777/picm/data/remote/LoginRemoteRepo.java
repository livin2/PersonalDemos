package com.dhu777.picm.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.data.remote.LoginRemoteContract.JwtTokenRes;
import com.dhu777.picm.data.remote.LoginRemoteContract.PostBodyUser;


import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.dhu777.picm.data.remote.LoginRemoteContract.getApiService;
import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class LoginRemoteRepo implements LoginDataSource {
    private static LoginRemoteRepo INSTANCE = null;
    private static LoginRemoteContract.Api apiService;

    private LoginRemoteRepo() {}
    public static LoginRemoteRepo getInstance(){
        if(INSTANCE == null){
            INSTANCE = new LoginRemoteRepo();

            apiService = getApiService(LoginRemoteContract.Api.class);
        }
        return INSTANCE;
    }

    @Override
    public void logout() {
        //TODO
    }

    @Override
    public void login(@NonNull final String username, @NonNull final String password,@NonNull final LoginCallback loginCallback) {
        PostBodyUser post = new PostBodyUser(username,password);
        Call<JwtTokenRes> call =apiService.signIn(post);

        //async
        call.enqueue(new Callback<JwtTokenRes>() {
            @Override
            public void onResponse(@NonNull Call<JwtTokenRes> call,@NonNull Response<JwtTokenRes> response) {
                try{
                    String token = checkNotNull(response.body().getJwtToken());
                    Log.d("LoginRemoteRepo", "login.onResponse: fetch Token Success");
//                Log.d("LoginRemoteRepo", "login.onResponse: "+response);
                    UserToken utk = new UserToken();
                    utk.setName(username);
                    utk.setToken(token);
                    utk.setDueTime(System.currentTimeMillis());
                    loginCallback.onSuccess(utk);
                }catch (Exception e){
                    e.printStackTrace();
                    loginCallback.onFail(new Throwable("Failed"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JwtTokenRes> call,@NonNull Throwable t) {
                Log.d("LoginRemoteRepo", "login.onFailure: "+t.getMessage());
                loginCallback.onFail(t);
            }
        });

    }

    @Override
    public void signUp(@NonNull final String username,@NonNull final String password,@NonNull final LoginCallback loginCallback) {
        PostBodyUser post = new PostBodyUser(username,password);
        Call<ResponseBody> call =apiService.signUp(post);

        //async
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                Log.d("LoginRemoteRepo", "signUp.onResponse: Success");
                //no Token return
                loginCallback.onSuccess(null);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                Log.d("LoginRemoteRepo", "login.onFailure: "+t.getMessage());
                loginCallback.onFail(t);
            }
        });
    }


    /**
     * should not be use,login status handle by{@link com.dhu777.picm.data.LoginRepositrory}
     */
    @Deprecated
    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Deprecated
    @Override
    public void getToken(LoginCallback callback) {
    }
}
