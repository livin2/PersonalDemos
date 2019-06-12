package com.dhu777.picm.home;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dhu777.picm.R;
import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.LoginRepositrory;
import com.dhu777.picm.data.entity.BaseResponse;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.data.remote.PicRemoteRepo;

import static com.dhu777.picm.util.ComUtil.getContext;

import com.dhu777.picm.data.remote.PicUpload;
import com.dhu777.picm.piclist.PicListPresenter;
import com.google.common.io.ByteStreams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class HomePresenter implements PicUpload.UpPicCallback{
    private LoginDataSource mLoginRepo;
    private static final String TAG = "HomePresenter";
    private PicListPresenter picPresenter;

    public HomePresenter(LoginDataSource mLoginRepo) {
        this.mLoginRepo = mLoginRepo;
    }

    public HomePresenter(LoginDataSource mLoginRepo, PicListPresenter picPresenter) {
        this.mLoginRepo = mLoginRepo;
        this.picPresenter = picPresenter;
    }

    public LoginDataSource  getLoginRepo(){
        return mLoginRepo;
    }

    public void upload(@NonNull final Uri picUri){
        final String name =  picUri.getLastPathSegment();
        String mimeType = getContext().getContentResolver().getType(picUri);
        Log.d("HomePresenter", "upload(): name:"+name);
        Log.d("HomePresenter", "upload(): mimeType:"+mimeType);
        mLoginRepo.getToken(new LoginDataSource.LoginCallback() {
            @Override
            public void onSuccess(UserToken Token) {
                String JWT = Token.getToken();
                byte[] picBits =  openPic(picUri);
                if(picBits != null)
                    PicRemoteRepo.getInstance()
                            .upload(picBits,name,JWT,HomePresenter.this);
            }

            @Override
            public void onFail(Throwable e) {
                Toast.makeText(getContext(),
                        e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private byte[] openPic(Uri picUri){
        try {
            InputStream inputStream = getContext()
                    .getContentResolver().openInputStream(picUri);
            byte[] targetArray = new byte[0];
            targetArray = ByteStreams.toByteArray(inputStream);
            return targetArray;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPicUploaded(@NonNull BaseResponse response) {
        if (response.getMsg()!=null)
            Log.d(TAG, "onPicUploaded: "+response.getMsg());
        Toast.makeText(getContext(),
                R.string.msg_suc_upload,Toast.LENGTH_SHORT).show();
        picPresenter.refreshList();
    }

    @Override
    public void onUploadFail(@NonNull Throwable t) {
        Toast.makeText(getContext(),
                t.getMessage(),Toast.LENGTH_SHORT).show();
        picPresenter.refreshList();
    }
}
