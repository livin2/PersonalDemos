package com.dhu777.picm.piclist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.dhu777.picm.R;
import com.dhu777.picm.mock.Injection;

import static com.dhu777.picm.util.ComUtil.addFragmentToActivity;

public class PicTestActivity extends AppCompatActivity {

    private PicListPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_list);

        PicListFragment picListFragment = (PicListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.frag_activity_pictest);
        if(picListFragment == null){
            picListFragment = PicListFragment.newInstance();
            addFragmentToActivity(getSupportFragmentManager()
                    ,picListFragment,R.id.frag_activity_pictest);
        }

        mPresenter = new PicListPresenter(
                Injection.providePicInfoRepositrory(),picListFragment);
    }
}
