package com.dhu777.picm.piclist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.dhu777.picm.R;
import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.LoginRepositrory;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.mock.Injection;
import com.dhu777.picm.piclist.PicListFragment;
import com.dhu777.picm.piclist.PicListPresenter;

import static com.dhu777.picm.util.ComUtil.addFragmentToActivity;

public class PicUserActivity extends AppCompatActivity{
    private PicListContract.Presenter mPicPresenter;
    private PicListFragment picListFragment;
    private UserToken utk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Injection.provideLoginRepositrory(getApplicationContext())
                .getToken(initCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        int preMode = Injection.mode;
        Injection.mode = preferences.getBoolean("mock",false)?
                Injection.MOKE:Injection.REAL;

        if(preMode!=Injection.mode && mPicPresenter!=null){
            //providePicInfoRepositrory根据mode标志注入 所以要放在mode标志变更后
            Injection.provideLoginRepositrory(getApplicationContext())
                    .getToken(refreshCallback);
        }
    }


    private LoginDataSource.LoginCallback initCallback = new LoginDataSource.LoginCallback() {
        @Override
        public void onSuccess(UserToken Token) {
            if (Token==null){
                onFail(new Exception("null token"));
                return;
            }
             picListFragment = (PicListFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.frag_activity_pictest);
            if(picListFragment == null){
                picListFragment = PicListFragment.newInstance();
                addFragmentToActivity(getSupportFragmentManager()
                        ,picListFragment,R.id.frag_activity_pictest);
            }
            utk=Token;
            mPicPresenter = new PicListPresenter(
                    Injection.provideUserPicRepositrory(Token),picListFragment);
        }

        @Override
        public void onFail(Throwable e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    };

    private LoginDataSource.LoginCallback refreshCallback = new LoginDataSource.LoginCallback() {
        @Override
        public void onSuccess(UserToken Token) {
            if (Token==null){
                onFail(new Exception("null token"));
                return;
            }
            mPicPresenter.changeRepo(Injection.provideUserPicRepositrory(utk));
            mPicPresenter.refreshList();
        }

        @Override
        public void onFail(Throwable e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    };
}
