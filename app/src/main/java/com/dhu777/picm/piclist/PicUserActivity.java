package com.dhu777.picm.piclist;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dhu777.picm.R;
import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.LoginRepositrory;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.mock.Injection;
import com.dhu777.picm.piclist.PicListFragment;
import com.dhu777.picm.piclist.PicListPresenter;

import static com.dhu777.picm.util.ComUtil.addFragmentToActivity;

public class PicUserActivity extends AppCompatActivity implements LoginDataSource.LoginCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_list);

        Injection.provideLoginRepositrory(getApplicationContext())
                .getToken(this);
    }

    @Override
    public void onSuccess(UserToken Token) {
        if (Token==null){
            onFail(new Exception("null token"));
            return;
        }
        PicListFragment picListFragment = (PicListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.frag_activity_pictest);
        if(picListFragment == null){
            picListFragment = PicListFragment.newInstance();
            addFragmentToActivity(getSupportFragmentManager()
                    ,picListFragment,R.id.frag_activity_pictest);
        }

        new PicListPresenter(
                Injection.provideUserPicRepositrory(Token),picListFragment);
    }

    @Override
    public void onFail(Throwable e) {
        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
    }
}
