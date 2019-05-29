package com.dhu777.picm.picdetail;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.dhu777.picm.R;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.util.DataHolder;
import com.github.chrisbanes.photoview.PhotoView;

public class PicDetailActivity extends AppCompatActivity {
    DialogFragment dialog;
    PicInfo picInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_detail);

        picInfo = DataHolder.getInstance().getImageInfo();
        dialog = new InfoDialogFragment(picInfo);
        //todo load downloaded img from device
        String pic = picInfo.getPicURL();

        PhotoView mPhotoView = (PhotoView) findViewById(R.id.photo_view);
        Glide.with(this).load(pic).into(mPhotoView);
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
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_pic_detail_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.picdetail_menu_info:
                dialog.show(getSupportFragmentManager(), "missiles");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
