package com.dhu777.picm.mock.data;


import androidx.annotation.NonNull;

import com.dhu777.picm.data.LoginDataSource;

public class FakeLoginRepositrory implements LoginDataSource {

    private static FakeLoginRepositrory INSTANCE;
    private FakeLoginRepositrory(){}

    public static FakeLoginRepositrory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeLoginRepositrory();
        }
        return INSTANCE;
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void logout() {

    }

    @Override
    public void login(String username, String password, LoginCallback loginCallback) {

    }

    @Override
    public void signUp(String username, String password, LoginCallback loginCallback) {

    }

    @Override
    public void getToken(LoginCallback callback) {

    }

    public Boolean login(@NonNull String username, @NonNull String password) {
        if (username.equals("user") && password.equals("pass"))
            return true;
        return false;
    }
}
