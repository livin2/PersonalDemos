package com.dhu777.picm.home;

import android.app.Activity;
import android.content.Intent;
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
        mHomePresenter = new HomePresenter(Injection.provideLoginRepositrory(getApplicationContext()));
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
        if (id == R.id.action_settings) {
            picLFragment.refreshLayout();
            return true;
        }else if(id == R.id.action_spanc3){
            picLFragment.setLayoutSpanCount(3);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
