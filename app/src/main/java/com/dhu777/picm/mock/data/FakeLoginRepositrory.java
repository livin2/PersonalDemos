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
    public Boolean login(@NonNull String username, @NonNull String password) {
        if (username.equals("user") && password.equals("pass"))
            return true;
        return false;
    }
}
