package com.dhu777.picm.home;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.dhu777.picm.MainActivity;
import com.dhu777.picm.R;
import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.login.LoginActivity;
import com.dhu777.picm.mock.Injection;
import com.dhu777.picm.piclist.PicListContract;
import com.dhu777.picm.piclist.PicListFragment;
import com.dhu777.picm.piclist.PicListPresenter;
import com.dhu777.picm.piclist.PicUserActivity;
import com.dhu777.picm.util.ComUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    public static final int REQUEST_PICK_PIC = 1;
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private DrawerLayout mDrawerLayout;
    private PicListPresenter mPicPresenter;
    private HomePresenter mHomePresenter;
    private SwipeRefreshLayout swipeRefresh;
    private View headerView;

    private PicListFragment picLFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestPermission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToggle();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);

        picLFragment = (PicListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if(picLFragment == null){
            picLFragment = PicListFragment.newInstance();
            ComUtil.addFragmentToActivity(getSupportFragmentManager(),picLFragment,R.id.contentFrame);
        }

        swipeRefresh = findViewById(R.id.home_swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefresh.setOnRefreshListener(this);

        mPicPresenter = new PicListPresenter(Injection.providePicInfoRepositrory(),picLFragment);
        mHomePresenter = new HomePresenter(Injection.provideLoginRepositrory(getApplicationContext()),
                mPicPresenter, this);
    }

    public void loginToggle(){
        if (!mHomePresenter.getLoginRepo().isLoggedIn()){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("是否注销当前用户?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mHomePresenter.getLoginRepo().logout();
                            TextView unameV = headerView.findViewById(R.id.nav_header_text);
                            unameV.setText(R.string.nav_header_title);
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        int preMode = Injection.mode;
        Injection.mode = preferences.getBoolean("mock",false)?
                Injection.MOKE:Injection.REAL;

        mPicPresenter.changeRepo(Injection.providePicInfoRepositrory());

        if(preMode!=Injection.mode){
            //providePicInfoRepositrory根据mode标志注入 所以要放在mode标志变更后
            mPicPresenter.refreshList();
        }

        mHomePresenter.getLoginRepo().getToken(new LoginDataSource.LoginCallback() {
            @Override
            public void onSuccess(UserToken Token) {
                try {
                    TextView unameV = headerView.findViewById(R.id.nav_header_text);
                    unameV.setText(Token.getName());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_PICK_PIC && resultCode == Activity.RESULT_OK){
            if (data!=null && data.getData()!=null){
                mHomePresenter.upload(data.getData());
            }else{
                Toast.makeText(getApplicationContext(),
                        R.string.msg_fail_pickpic,Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.home_span_1:return setSpan(item,1);
            case R.id.home_span_2:return setSpan(item,2);
            case R.id.home_span_3:return setSpan(item,3);
            case R.id.home_span_4:return setSpan(item,4);
            default:return super.onOptionsItemSelected(item);
        }
    }

    private boolean setSpan(MenuItem item,int spancount){
        if (item.isChecked())
            return true;
        else{
            picLFragment.setLayoutSpanCount(spancount);
            item.setChecked(true);
            return true;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }else if(id == R.id.nav_send){
            showPickPic();
        }
        else if (id == R.id.nav_test){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_setting){
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_gallery){
            if(mHomePresenter.getLoginRepo().isLoggedIn()){
                Intent intent = new Intent(getApplicationContext(), PicUserActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(),"请登录",Toast.LENGTH_SHORT).show();
                loginToggle();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showPickPic(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_PIC);
    }

    @Override
    public void onRefresh() {
        mPicPresenter.refreshList(new PicListContract.RefreshCallBack() {
            @Override
            public void onFinsh() {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public boolean checkPremission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(){
        if(!checkPremission()){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if(grantResults.length>0  &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //success
                }else{
                    Toast.makeText(getApplicationContext()
                            ,"获取读写权限失败",Toast.LENGTH_SHORT).show();
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
