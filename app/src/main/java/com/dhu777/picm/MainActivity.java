package com.dhu777.picm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.remote.PicRemoteRepo;
import com.dhu777.picm.util.NetworkContract;
import com.dhu777.picm.util.NetworkUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.main_text);


        PicRemoteRepo.getInstance().fetchPicList(new PicDataSource.FetchPicsCallback() {
            @Override
            public void onDataLoaded(List<PicInfo> picList) {
                PicInfo p = picList.get(0);
                textView.setText(p.getPicId()+" "+p.getPicName()+" ");
            }

            @Override
            public void onDataNotAvailable(Exception e) {

            }
        });

//        NetworkUtil netService = NetworkUtil.getInstance();
//        Call<ResponseBody> call = netService.getAllPicInfo();
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    try {
//                        Log.i("MainText", "Success");
//                        //返回的结果保存在response.body()中
//                        String result = response.body().string();
//                        //onResponse方法是运行在主线程也就是UI线程的，所以我们可以在这里
//                        //直接更新UI
//                        textView.setText(result);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.i("MainText", "failed");
//            }
//        });
    }
}
