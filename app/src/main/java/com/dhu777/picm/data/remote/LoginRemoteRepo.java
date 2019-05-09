package com.dhu777.picm.data.remote;

import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.Result;

public class LoginRemoteRepo implements LoginDataSource<Result> {
    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public Result login(String username, String password) {
        return null;
    }
}
