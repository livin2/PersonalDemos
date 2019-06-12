package com.dhu777.picm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.LoginRepositrory;
import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.data.local.TokenLocalRepo;
import com.dhu777.picm.data.remote.LoginRemoteRepo;
import com.dhu777.picm.data.remote.PicRemoteRepo;
import com.dhu777.picm.login.LoginActivity;
import com.dhu777.picm.piclist.PicTestActivity;
import com.dhu777.picm.piclist.PicUserActivity;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    TextView textView;
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        textView = findViewById(R.id.main_text);
        Button loginButton = findViewById(R.id.login);
        Button tokenButton = findViewById(R.id.show_token);
        Button testPicListButton = findViewById(R.id.test_piclist_action);
        Button upload = findViewById(R.id.upload);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        tokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testTokenLocalRepo();
            }
        });

        testPicListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PicUserActivity.class);
                startActivity(intent);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);

            }
        });

    }

//    Cursor returnCursor =
//                        getContentResolver().query(uri, null, null, null, null);
//                int nIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                returnCursor.moveToFirst();
//                String name = returnCursor.getString(nIndex);
//                Log.d("MainActivity", "onActivityResult: name:"+name);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if (data!=null){
                final Uri uri =data.getData();
                Log.d("MainActivity", "onActivityResult: "+uri);
                Log.d("MainActivity", "onActivityResult: getPath:"+uri.getLastPathSegment());

                String mimeType = getContentResolver().getType(uri);
                Log.d("MainActivity", "onActivityResult: mimeType:"+mimeType);

                File img = new File(uri.getPath());
                final String name = img.getName();
                Log.d("MainActivity", "onActivityResult: imgName:"+img.getName());
                Log.d("MainActivity", "onActivityResult: imgPath:"+img.getPath());
                TokenLocalRepo.getInstance(MainActivity.this).getToken(new LoginDataSource.LoginCallback() {
                    @Override
                    public void onSuccess(UserToken Token) {
                        String JWT = Token.getToken();
                        InputStream inputStream = null;
                        try {
                            inputStream = getContentResolver().openInputStream(uri);
                            byte[] targetArray = new byte[0];
                            targetArray = ByteStreams.toByteArray(inputStream);
                            PicRemoteRepo.getInstance().upload(targetArray,name,JWT,null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(Throwable e) {

                    }
                });
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void testTokenLocalRepo(){
        TokenLocalRepo tokenLocalRepo = TokenLocalRepo.getInstance(MainActivity.this);
//        tokenLocalRepo.saveToken("tester","000-aaa-bbb");
        tokenLocalRepo.getToken(new LoginDataSource.LoginCallback() {
            @Override
            public void onSuccess(UserToken Token) {
                String display = Token.getId()+" "+
                        Token.getName()+"\n"+Token.getToken();
                textView.setText(display);
            }

            @Override
            public void onFail(Throwable e) {
                textView.setText(e.getMessage());
            }
        });
    }

    private void testLoginRemote() {
        LoginRemoteRepo.getInstance().login("test", "test"
                , new LoginDataSource.LoginCallback() {
                    @Override
                    public void onSuccess(@NonNull UserToken Token) {
                        String display = Token.getId()+" "+
                                Token.getName()+"\n"+Token.getToken();
                        textView.setText(display);
                    }

                    @Override
                    public void onFail(Throwable e) {

                    }
                });

    }

    private void testPicRemote() {
        PicRemoteRepo.getInstance().fetchPicList(new PicDataSource.FetchPicsCallback() {
            @Override
            public void onDataLoaded(@NonNull List<PicInfo> picList) {
                PicInfo p = picList.get(0);
                textView.setText(p.getPicId() + " " + p.getPicName() + " ");
            }

            @Override
            public void onDataNotAvailable(Exception e) {

            }
        });
    }
}
