package com.dhu777.picm.mock;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.LoginRepositrory;
import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.PicInfoRepositrory;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.data.local.TokenLocalRepo;
import com.dhu777.picm.data.remote.LoginRemoteRepo;
import com.dhu777.picm.data.remote.PicRemoteRepo;
import com.dhu777.picm.data.remote.PicUserRemoteRepo;
import com.dhu777.picm.mock.data.FakeLoginRepositrory;

/**
 * 根据mode值 切换MOKE的后端/真实的后端
 */
public class Injection {
    public static final int REAL = 0;
    public static final int MOKE = 1;

    public static int mode = REAL;

    private static final String MockUser = "usermock";
    private static final String MockToken = "0000";
    public static final String MockURL =
            "https://easy-mock.com/mock/5cd3f635cd8c445f3fa5bf63/picm/";

    public static final String RealURL =
            "http://115.159.75.152:8080/";

    public static String provideRemoteUnivesalURL(){
        switch (mode){
            case REAL:return RealURL;
            case MOKE:return MockURL;
            default:return MockURL;
        }
    }

    public static LoginDataSource provideLoginRepositrory(Context context){
        switch (mode){
            case REAL:
                return  LoginRepositrory.getInstance(LoginRemoteRepo.getInstance()
                        , TokenLocalRepo.getInstance(context));
            case MOKE:
                return LoginRepositrory.getInstance(LoginRemoteRepo.getInstance()
                        , TokenLocalRepo.getInstance(context));
            default:
                return FakeLoginRepositrory.getInstance();
        }
    }

    public static PicInfoRepositrory providePicInfoRepositrory(){
//        PicInfoRepositrory.destory();
        Log.d("Injection", "providePicInfoRepositrory");
        return PicInfoRepositrory.getInstance(PicRemoteRepo.getInstance(),null
                ,PicRemoteRepo.getInstance());
    }

    public static PicInfoRepositrory provideUserPicRepositrory(@NonNull UserToken userToken){
//        PicInfoRepositrory.destory();
        UserToken utoken = userToken;
        if(mode==MOKE){
            utoken=new UserToken();
            utoken.setName(MockUser);
            utoken.setToken(MockToken);
        }
        Log.d("Injection", "provideUserPicRepositrory");
        return PicInfoRepositrory.getInstance(PicUserRemoteRepo.getInstance(utoken),null
                ,PicRemoteRepo.getInstance());
    }
}
