package com.dhu777.picm.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.data.local.TokenLocalRepo;
import com.dhu777.picm.data.remote.LoginRemoteRepo;
import com.dhu777.picm.util.ReLoginException;

import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class LoginRepositrory implements LoginDataSource {
    private static LoginRepositrory INSTANCE = null;
    private LoginDataSource loginRemoteRepo;
    private TokenDataSource tokenLocalRepo;

    private static String MSG_UNLOGIN ="未登录";
    private static String MSG_OVERDUE ="登录过期";
    private static Long TOKEN_VAILD_PERIOD = 30L*24L*3600L*100L;

    UserToken tokenCache;
    boolean tokenIsDirty = false;

    private LoginRepositrory(@NonNull LoginRemoteRepo loginRemoteRepo,@NonNull TokenLocalRepo tokenLocalRepo) {
        this.loginRemoteRepo = loginRemoteRepo;
        this.tokenLocalRepo = tokenLocalRepo;
    }

    public static LoginRepositrory getInstance(LoginRemoteRepo loginRemoteRepo,
                                               TokenLocalRepo tokenLocalRepo){
        if (INSTANCE == null) {
            INSTANCE = new LoginRepositrory(loginRemoteRepo,tokenLocalRepo);
        }
        return INSTANCE;
    }

    //need a LoginRepo with new RemoteRepo or new LocalRepo
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public boolean isLoggedIn() {
        return tokenCache !=null && !tokenIsDirty;
    }

    @Override
    public void logout() {
        tokenIsDirty = true;
        tokenLocalRepo.delToken();
        loginRemoteRepo.logout();
    }

    @Override
    public void login(@NonNull String username,@NonNull String password,@NonNull final LoginCallback loginCallback) {
        //save token
        loginRemoteRepo.login(username, password, new LoginCallback() {
            @Override
            public void onSuccess(UserToken Token) {
                if(Token ==null){
                    onFail(new Exception("null Token"));
                    return;
                }
                tokenLocalRepo.saveToken(Token);
                tokenCache = Token;
                tokenIsDirty = false;
                loginCallback.onSuccess(Token);
            }

            @Override
            public void onFail(Throwable e) {
                loginCallback.onFail(e);
            }
        });
    }

    @Override
    public void signUp(@NonNull final String username, @NonNull final String password, @NonNull final LoginCallback loginCallback) {
        loginRemoteRepo.signUp(username, password, new LoginCallback() {
            @Override
            public void onSuccess(@Nullable UserToken Token) {
                //Remote Service will not return a Token when signup
                login(username,password,loginCallback);
            }

            @Override
            public void onFail(Throwable e) {
                loginCallback.onFail(e);
            }
        });

    }

    public void getToken(@NonNull final LoginCallback callback){
        checkNotNull(callback);
        if(tokenCache !=null && !tokenIsDirty){
            callback.onSuccess(tokenCache);
            return;
        }

        if (tokenIsDirty){
            notifyDirty(callback,MSG_OVERDUE);
            return;
        }

        tokenLocalRepo.getToken(new LoginCallback() {
            @Override
            public void onSuccess(UserToken Token) {
                if (!checkTokenDue(Token)){
                    tokenIsDirty = true;
                    notifyDirty(callback,MSG_OVERDUE);
                    return;
                }
                refreshCache(Token);
                callback.onSuccess(tokenCache);
            }
            @Override
            public void onFail(Throwable e) {
                tokenIsDirty = true;
                notifyDirty(callback,MSG_UNLOGIN);
            }
        });
    }

    private Boolean checkTokenDue(UserToken utk){
        if(utk.getDueTime() == null)
            return false;
        return utk.getDueTime()-System.currentTimeMillis() < TOKEN_VAILD_PERIOD;
    }

    private void refreshCache(@NonNull UserToken utk){
        if(tokenCache == null){
            tokenCache = new UserToken();
        }
        tokenCache.setId(utk.getId());
        tokenCache.setName(utk.getName());
        tokenCache.setToken(utk.getToken());
        tokenIsDirty = false;
    }

    private void notifyDirty(@NonNull LoginCallback callback,String Msg){
        callback.onFail(new ReLoginException(Msg));
    }
}
