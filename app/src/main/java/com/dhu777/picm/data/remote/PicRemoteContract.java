package com.dhu777.picm.data.remote;

import com.dhu777.picm.data.entity.BaseResponse;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.mock.Injection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class PicRemoteContract {
    public static Api getApiService(Class<Api> apiClass,String URL_BASE){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.HEADERS)).build();

        Retrofit retrofitInstance = new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        return retrofitInstance.create(apiClass);
    }

    public interface Api {
        @Headers("Cache-Control:no-cache")
        @GET("allPicInfo")
        Call<List<PicInfo>> getAllPicInfo();

        @GET("picInfo/{username}")
        Call<List<PicInfo>> getUserPicList(@Path("username") String username,@Header("jwtToken")String jwt);

        @Multipart
        @POST("upload")
        Call<BaseResponse<String>> uploadSinglePic(@Part MultipartBody.Part multipart, @Header("jwtToken")String jwt);
    }

    /**
     * RemoteApi return
     * POJO class provide for gson
     * gson serialize data into object with it
     */
    /*public static class PicList {
        private List<PicInfo> picList;
        public List<PicInfo> getpicList() {
            return picList;
        }
        public void setpicList(List<PicInfo> pics) {
            this.picList = pics;
        }
    }*/
}
