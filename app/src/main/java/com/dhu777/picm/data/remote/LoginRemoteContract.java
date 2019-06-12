package com.dhu777.picm.data.remote;


import com.dhu777.picm.mock.Injection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


/**
 * specifies model,api interface and value which Retrofit need
 */
public class LoginRemoteContract {
    public final static String URL_BASE = Injection.provideRemoteUnivesalURL();


    public static Api getApiService(Class<Api> apiClass){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.HEADERS)).build();

        Retrofit retrofitInstance = new Retrofit.Builder()
                .baseUrl(LoginRemoteContract.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        return retrofitInstance.create(apiClass);
    }

    /**
     * RESTful Api
     * provided for Retrofit
     */
    public interface Api {
        @POST("signin")
        Call<JwtTokenRes> signIn(@Body PostBodyUser bodyUser);

        @POST("signup")
        Call<ResponseBody> signUp(@Body PostBodyUser bodyUser);
    }

    /**
     * RemoteApi Return
     * POJO class, provided for gson
     * gson deserialize object into json with it
     */
    public static class JwtTokenRes{
        private String jwtToken;
        public String getJwtToken() { return jwtToken; }
        public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }
    }

    /**
     * RemoteApi Args
     * POJO class, provided for gson
     * gson deserialize object into json with it
     */
    public static class PostBodyUser{
        private String username;
        private String password;
        PostBodyUser(String username,String password){
            this.username = username;
            this.password = password;
        }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
//        public void setUsername(String username) { this.username = username; }
//        public void setPassword(String password) { this.password = password; }
    }
}
