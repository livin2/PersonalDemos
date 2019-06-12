package com.dhu777.picm.data;

import android.telecom.Call;

import com.dhu777.picm.data.entity.UserToken;

public interface LoginDataSource{
    boolean isLoggedIn();
    void logout();
    void login(String username, String password,LoginCallback loginCallback);
    void signUp(String username, String password,LoginCallback loginCallback);
    void getToken(LoginCallback callback);

    interface LoginCallback{
        void onSuccess(UserToken Token);
        void onFail(Throwable e);
    }

    interface TokenDataSource{
        void getToken(LoginCallback callback);
        void saveToken(UserToken Token);
        void delToken();
    }

}
