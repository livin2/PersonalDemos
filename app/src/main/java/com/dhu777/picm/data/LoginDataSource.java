package com.dhu777.picm.data;

public interface LoginDataSource <T>{
    boolean isLoggedIn();
    void logout();
    T login(String username, String password);

    interface TokenDataSource{
        String getToken();
        void saveToken(String name,String token);
        void delToken();
    }
}
