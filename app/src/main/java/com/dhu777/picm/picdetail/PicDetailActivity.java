package com.dhu777.picm.picdetail;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.dhu777.picm.R;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.mock.Injection;
import com.dhu777.picm.util.DataHolder;
import com.github.chrisbanes.photoview.PhotoView;

public class PicDetailActivity extends AppCompatActivity implements PicDetailContract.View{
    DialogFragment dialog;
    PicInfo picInfo;
    PicDetailContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_detail);

        picInfo = DataHolder.getInstance().getImageInfo();
        dialog = new InfoDialogFragment(picInfo);

        PhotoView mPhotoView = (PhotoView) findViewById(R.id.photo_view);
        Glide.with(this).load(picInfo.getPicURL()).into(mPhotoView);
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                registerForContextMenu(v);
                v.showContextMenu();
                unregisterForContextMenu(v);
                return true;
            }
        });
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mPresenter = new PicDetailPresenter(this,
                Injection.provideLoginRepositrory(getApplicationContext()));

        mPresenter.start();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_pic_detail_context, menu);
        try{
            if(mPresenter.checkUser(picInfo.getUserName())){
                MenuItem menuItem =menu.findItem(R.id.picdetail_menu_delete);
                menuItem.setEnabled(true);
                menuItem.setVisible(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.picdetail_menu_info:
                dialog.show(getSupportFragmentManager(), "missiles");
                return true;
            case R.id.picdetail_menu_savepic:
                if(checkPremission())
                    mPresenter.savePic(picInfo);
                else
                    requestPermission(R.id.picdetail_menu_savepic);
                return true;
            case R.id.picdetail_menu_sharepic:
                if(checkPremission())
                    mPresenter.sharePic(picInfo);
                else
                    requestPermission(R.id.picdetail_menu_sharepic);
                return true;
            case R.id.picdetail_menu_editpic:
                if(checkPremission())
                    mPresenter.editPic(picInfo);
                else
                    requestPermission(R.id.picdetail_menu_editpic);
                return true;
            case R.id.picdetail_menu_copyurl:
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("PICM_picUrl", picInfo.getPicURL());
                clipboard.setPrimaryClip(clip);
                showToast("已复制");
                return true;
            case R.id.picdetail_menu_delete:
                mPresenter.deletePic(picInfo);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        if(grantResults.length>0  &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            onPermissionGet(requestCode);
        }else{
            Toast.makeText(getApplicationContext()
                    ,"获取读写权限失败",Toast.LENGTH_SHORT).show();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void onPermissionGet(int requestCode){
        switch (requestCode){
            case R.id.picdetail_menu_savepic:
                mPresenter.savePic(picInfo);
                return;
            case R.id.picdetail_menu_editpic:
                mPresenter.editPic(picInfo);
                return;
            case R.id.picdetail_menu_sharepic:
                mPresenter.sharePic(picInfo);
                return;
        }
    }

    @Override
    public void showShare(Intent shareIntent){
        Log.d("showShare", "showShare: ");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.send_to)));
    }

    @Override
    public void showEdit(Intent editIntent) {
        startActivity(Intent.createChooser(editIntent, getResources().getString(R.string.edit_to)));
    }


    @Override
    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showToast(int Rid){
        Toast.makeText(getApplicationContext(),Rid,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isActive();
    }
    @Override
    public void setPresenter(PicDetailContract.Presenter presenter) {
        //no need
    }

    @Override
    public void finish() {
        super.finish();
    }

    public boolean checkPremission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(int requestCode){
        if(!checkPremission()){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
        }
    }
}
