package com.dhu777.picm.data.remote;

import com.dhu777.picm.data.LoginDataSource;

public class TokenRemoteRepo implements LoginDataSource.TokenDataSource {
    //TODO
    @Override
    public String getToken() {
        return null;
    }

    @Override
    public void saveToken(String name, String token) {

    }

    @Override
    public void delToken() {

    }
}
