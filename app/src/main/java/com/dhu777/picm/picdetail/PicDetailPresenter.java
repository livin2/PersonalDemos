package com.dhu777.picm.picdetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dhu777.picm.R;
import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.PicDataSource;
import com.dhu777.picm.data.PicInfoRepositrory;
import com.dhu777.picm.data.entity.BaseResponse;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.mock.Injection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PicDetailPresenter implements PicDetailContract.Presenter,PicDataSource.DelPicCallback{
    private PicDetailContract.View mView;
    private byte[] picBytes;
    private LoginDataSource mLoginRepo;
    private PicInfoRepositrory mPicRepo;

    private UserToken utk = null;


    public PicDetailPresenter(PicDetailContract.View mView, LoginDataSource mLoginRepo) {
        this.mView = mView;
        this.mLoginRepo = mLoginRepo;
        mPicRepo = PicInfoRepositrory.getInstance();
    }


    @Override
    public void start() {
        mLoginRepo.getToken(new LoginDataSource.LoginCallback() {
            @Override
            public void onSuccess(UserToken Token) {
                utk = Token;
            }
            @Override
            public void onFail(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isLoginIn(){
        return mLoginRepo.isLoggedIn();
    }
    @Override
    public boolean checkUser(String username){
        if(mLoginRepo.isLoggedIn()&&utk!=null){
            if (utk.getName().equals(username)){
                return true;
            }
        }
        return false;
    }

    private boolean checkExternalStorage(){
        if (!Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState())){
            Log.e("savePic", "ExternalStorage: "+Environment.getExternalStorageState());
            mView.showToast(R.string.no_mount);
            return false;
        }
        return true;
    }

    public void deletePic(final PicInfo picInfo){
        mLoginRepo.getToken(new LoginDataSource.LoginCallback() {
            @Override
            public void onSuccess(UserToken Token) {
                if(mPicRepo!=null){
                    mPicRepo.deletePic(picInfo.getPicId(), Token.getToken()
                            , PicDetailPresenter.this);
                }else{
                    mView.showToast("删除失败");
                }
            }
            @Override
            public void onFail(Throwable e) {
                mView.showToast("删除失败,登录过期");
                e.printStackTrace();
            }
        });

    }

    @Override
    public void savePic(PicInfo picInfo) {
        if (!checkExternalStorage())
            return;

        File dir = getPublicAlbumStorageDir(mView.getApplicationContext());
        if(dir==null)
            return;
        final File file = new File(dir, picInfo.getPicName()+"."+picInfo.getPicType());
        if(file.exists()){
            mView.showToast(R.string.file_exist);
            return;
        }

        if(picBytes!=null){
            saveTofile(file,picBytes);
            return;
        }

        Glide.with(mView.getApplicationContext())
                .as(byte[].class).load(picInfo.getPicURL()).into(new CustomTarget<byte[]>(){
            @Override
            public void onResourceReady(@NonNull byte[] bytes, @Nullable Transition<? super byte[]> transition) {
                picBytes = bytes;
                saveTofile(file,bytes);
                mView.showToast(file.getPath());
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });
    }

    private void mShare(Uri fileUri){
        final Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM,fileUri);
        mView.showShare(shareIntent);
    }

    private void mEdit(Uri fileUri){
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(fileUri, "image/*");
        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mView.showEdit(editIntent);
    }

    @Override
    public void sharePic(PicInfo picInfo) {
        if (!checkExternalStorage())
            return;

        File dir = getPublicAlbumStorageDir(mView.getApplicationContext());
        if(dir==null)
            return;
        final File file = new File(dir, picInfo.getPicName()+"."+picInfo.getPicType());
        if(file.exists()){
            mShare(FileToUrl(file));
            return;
        }
        if(picBytes!=null){
            mShare(saveTofile(file,picBytes));
            return;
        }

        Glide.with(mView.getApplicationContext())
                .as(byte[].class).load(picInfo.getPicURL()).into(new CustomTarget<byte[]>(){
            @Override
            public void onResourceReady(@NonNull byte[] bytes, @Nullable Transition<? super byte[]> transition) {
                picBytes = bytes;
                mShare(saveTofile(file,picBytes));
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });
    }

    @Override
    public void editPic(PicInfo picInfo) {
        if (!checkExternalStorage())
            return;

        File dir = getPublicAlbumStorageDir(mView.getApplicationContext());
        if(dir==null)
            return;
        final File file = new File(dir, picInfo.getPicName()+"."+picInfo.getPicType());
        if(file.exists()){
            mEdit(FileToUrl(file));
            return;
        }
        if(picBytes!=null){
            mEdit(saveTofile(file,picBytes));
            return;
        }

        Glide.with(mView.getApplicationContext())
                .as(byte[].class).load(picInfo.getPicURL()).into(new CustomTarget<byte[]>(){
            @Override
            public void onResourceReady(@NonNull byte[] bytes, @Nullable Transition<? super byte[]> transition) {
                picBytes = bytes;
                mEdit(saveTofile(file,picBytes));
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });
    }

    private Uri FileToUrl(File file){
        return FileProvider.getUriForFile(mView.getApplicationContext(),
                mView.getApplicationContext().getPackageName()
                        + ".fileprovider"
                , file);
    }

    private Uri saveTofile(File file,byte[] bytes){
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.close();
            return FileToUrl(file);
        } catch (IOException e) {
            mView.showToast(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private File getPublicAlbumStorageDir(Context context) {
        File dir =
            new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                       context.getApplicationContext().getString(R.string.app_name));
        if (!dir.exists()) {
            if(!dir.mkdirs()){
               mView.showToast(R.string.create_dir_fail);
                mView.showToast(dir.getPath());
               return null;
            }
        }
        return dir;
    }

    //delete Callback
    @Override
    public void onDataLoaded(@Nullable BaseResponse msg) {
        mView.showToast("删除成功");
        mPicRepo.refresh();
        mView.finish();
    }

    @Override
    public void onDataNotAvailable(@NonNull Throwable t) {
//        mView.showToast("删除失败");
        t.printStackTrace();
    }
}
