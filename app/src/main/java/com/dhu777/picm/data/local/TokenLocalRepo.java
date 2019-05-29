package com.dhu777.picm.data.local;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.entity.UserToken;

import static com.dhu777.picm.util.ComUtil.checkNotNull;

public class TokenLocalRepo implements LoginDataSource.TokenDataSource {
    public static TokenLocalRepo INSTANCE;
    private static UserTokenDAO userTokenDAO;

    //there should be only one row in the UserToken Table
    private static final int uniId = 0;

    public static TokenLocalRepo getInstance(@NonNull Context applicationContext) {
        if (INSTANCE == null) {
            userTokenDAO = AppDataBase.getInstance(checkNotNull(applicationContext)).userTokenDAO();
            INSTANCE = new TokenLocalRepo();
        }
        return INSTANCE;
    }

    private TokenLocalRepo() {
    }


    @Override
    public void getToken(@NonNull LoginDataSource.LoginCallback callback) {
        new getAsyn(checkNotNull(callback)).execute();
    }

    @Override
    public void saveToken(@NonNull UserToken utk) {
        //TODO:encrypt token here
        new saveAsyn().execute(utk);
    }

    @Override
    public void delToken() {
        new delAsyn().execute();
    }

    private static class delAsyn extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                UserToken utk = new UserToken();
                utk.setId(uniId);
                userTokenDAO.delete(utk);
//                userTokenDAO.delete(userTokenDAO.getById(uniId));
            } catch (Exception e) {
                Log.e("TokenLocalRepo", "delAsyn:" + e.getMessage());
            }
            return null;
        }
    }

    private static class saveAsyn extends AsyncTask<UserToken, Void, Void> {
        @Override
        protected Void doInBackground(UserToken... args) {
            try {
                UserToken utk = new UserToken();
                utk.setId(uniId);
                userTokenDAO.delete(utk);
            } catch (Exception e) {
                Log.e("TokenLocalRepo", "saveAsyn: " + e.getMessage());
            }
            userTokenDAO.insert(args[0]);
            return null;
        }
    }

    private static class getAsyn extends AsyncTask<Void, Void, Integer> {
        private LoginDataSource.LoginCallback callback;
        private static final int TYPE_SUCCESS = 0;
        private static final int TYPE_FAiLED = 1;
        private UserToken utk = null;
        private Exception exp = null;

        public getAsyn(@NonNull LoginDataSource.LoginCallback callback) {
            this.callback = checkNotNull(callback);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            //TODO:decrypt token here:
            //get-decrypt-set-return
            try {
                utk = userTokenDAO.getById(uniId);
            } catch (Exception e) {
                exp = e;
                return TYPE_FAiLED;
            }
            return TYPE_SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer res) {
            switch (res) {
                case TYPE_SUCCESS:
                    callback.onSuccess(checkNotNull(utk));
                    return;
                case TYPE_FAiLED:
                    callback.onFail(checkNotNull(exp));
                    return;
            }

        }
    }
}
