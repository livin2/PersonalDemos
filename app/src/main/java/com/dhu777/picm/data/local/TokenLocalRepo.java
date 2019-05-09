package com.dhu777.picm.data.local;

import android.content.Context;


import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.entity.UserToken;

public class TokenLocalRepo implements LoginDataSource.TokenDataSource {
    //there should be only one row in the UserToken Table
    private static final int uniId = 0;
    private final UserTokenDAO userTokenDAO;
    public static TokenLocalRepo INSTANCE;
    public static TokenLocalRepo getInstance(Context applicationContext){
        if (INSTANCE == null) {
            UserTokenDAO dao = AppDataBase.getInstance(applicationContext).userTokenDAO();
            INSTANCE = new TokenLocalRepo(dao);
        }
        return INSTANCE;
    }
    private TokenLocalRepo(UserTokenDAO userTokenDAO) {
        this.userTokenDAO = userTokenDAO;
    }


    @Override
    public String getToken() {
        UserToken res = userTokenDAO.getById(uniId);
        //TODO:decrypt token here
        return res.getToken();
    }

    @Override
    public void saveToken(String name,String token) {
        UserToken utk = new UserToken();
        utk.setId(uniId);
        utk.setName(name);
        //TODO:encrypt token here
        utk.setToken(token);

        delToken();
        userTokenDAO.insert(utk);
    }

    @Override
    public void delToken() {
        userTokenDAO.delete(userTokenDAO.getById(uniId));
    }
}
