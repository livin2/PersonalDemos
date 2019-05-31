package com.dhu777.picm.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dhu777.picm.MainActivity;
import com.dhu777.picm.R;
import com.dhu777.picm.mock.Injection;
import com.dhu777.picm.piclist.PicListFragment;
import com.dhu777.picm.piclist.PicListPresenter;
import com.dhu777.picm.util.ComUtil;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_PICK_PIC = 1;

    private DrawerLayout mDrawerLayout;
    private PicListPresenter mPicPresenter;
    private HomePresenter mHomePresenter;

    private PicListFragment picLFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        mPicPresenter = new PicListPresenter(Injection.providePicInfoRepositrory(),picLFragment);
        mHomePresenter = new HomePresenter(Injection.provideLoginRepositrory(getApplicationContext()),
                mPicPresenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        int preMode = Injection.mode;
        Injection.mode = preferences.getBoolean("mock",true)?
                Injection.MOKE:Injection.REAL;

        if(preMode!=Injection.mode){
            //providePicInfoRepositrory根据mode标志注入 所以要放在mode标志变更后
            mPicPresenter.changeRepo(Injection.providePicInfoRepositrory());
            mPicPresenter.refreshList();
        }
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
            case R.id.home_refresh:
                mPicPresenter.refreshList();
                return true;
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
}
