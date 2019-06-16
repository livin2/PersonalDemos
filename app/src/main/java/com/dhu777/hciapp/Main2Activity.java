package com.dhu777.hciapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import static android.view.View.INVISIBLE;
import static com.dhu777.hciapp.BdUtil.RESULT_TYPE_FINAL;
import static com.dhu777.hciapp.BdUtil.initPermission;
import static com.dhu777.hciapp.BdUtil.startParams;

@SuppressWarnings("WeakerAccess")
public class Main2Activity extends AppCompatActivity implements EventListener,SubsamplingScaleImageView.OnStateChangedListener{
    private Players players;
    private ProgressBar mProgressBar;
    private SubsamplingScaleImageView imageView;
    private AudioManager audioManager;
    private Locator mLocator;
    private FloatingActionButton fab;
    protected MyRecognizer myRecognizer;

    private static final String TAG = "Main2Activity";

    private final Float defaultScale = 1.3f;
    private final PointF defaultCenter = new PointF(5400,700);

    private static final double moveStepH = 500;
    private static final double moveStepV = 300;
    private String moveFlagUP ="上";
    private String moveFlagDOWN ="下";
    private String moveFlagRIGHT ="右";
    private String moveFlagLEFT ="左";
    private String volFlagUP ="大";
    private String volFlagDOWN ="小";
    private String jumpFlagGate ="城门";
    private String jumpFlagBridge ="桥";

    private Integer volMax;
    private Integer volMin;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission(this);
        mProgressBar = findViewById(R.id.fabProgress);

        imageView = findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.longpic));
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        imageView.setMaxScale(2.5f);
        imageView.setScaleAndCenter(defaultScale,defaultCenter);
        imageView.setOnStateChangedListener(this);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Objects.requireNonNull(imageView.animateScaleAndCenter(defaultScale, defaultCenter)).start();
                return true;
            }
        });


        fab = findViewById(R.id.voiceButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProgressBar.getVisibility()== INVISIBLE){
                    players.pause();
                    start();
                }else{
                    stop();
                    players.play();
                }
            }
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        volMax = Float.valueOf(volMax.floatValue()*0.75f).intValue();
        volMin = Float.valueOf(volMax.floatValue()*0.55f).intValue();

        int defVol = Double.valueOf((volMax-volMin)*(0.3/1.5) +volMin).intValue();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, defVol
                , AudioManager.FLAG_PLAY_SOUND);

        players = new Players(getApplicationContext());
        mLocator = new Locator(players);
        mLocator.locateVoice(defaultCenter);

        myRecognizer = new MyRecognizer(this, this);
    }

    /**
     * 读取设置的关键词
     */
    public void setKeyWord(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        moveFlagUP = sharedPreferences.getString("move_up",moveFlagUP);
        moveFlagDOWN = sharedPreferences.getString("move_down",moveFlagDOWN);
        moveFlagLEFT = sharedPreferences.getString("move_left",moveFlagLEFT);
        moveFlagRIGHT = sharedPreferences.getString("move_right",moveFlagRIGHT);
        volFlagUP = sharedPreferences.getString("vol_up",volFlagUP);
        volFlagDOWN = sharedPreferences.getString("vol_down",volFlagDOWN);
        jumpFlagGate = sharedPreferences.getString("jump_gate",jumpFlagGate);
        jumpFlagBridge = sharedPreferences.getString("jump_bridge",jumpFlagBridge);
    }

    /**
     * 在缩放比例变化时执行
     * @param newScale 新的缩放比例
     * @param origin 操作事件来源
     */
    @Override
    public void onScaleChanged(float newScale, int origin) {
        Float newVolF = (volMax-volMin)*((newScale-1f)/1.5f) +volMin;
        int newVol = newVolF>=volMax?volMax:newVolF.intValue();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol
                , AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * @param newCenter 新的视点中心
     * @param origin
     */
    @Override
    public void onCenterChanged(PointF newCenter, int origin) {
        mLocator.locateVoice(newCenter);
    }

    public void onFinalResult(String finResStr){
        mProgressBar.setVisibility(View.INVISIBLE);
        fab.setImageResource(R.drawable.ic_voice);

        ImageViewState state = Objects.requireNonNull(imageView.getState());
        PointF pf = state.getCenter();
        float scale = state.getScale();
        String MSG = "无法识别语音:"+finResStr;
        if(finResStr.contains(moveFlagUP)){
            MSG="向上移动";
            pf.y-=moveStepV;
        }else if(finResStr.contains(moveFlagDOWN)){
            MSG="向下移动";
            pf.y+=moveStepV;
        }else if(finResStr.contains(moveFlagLEFT)){
            MSG="向左移动";
            pf.x-=moveStepH;
        }else if(finResStr.contains(moveFlagRIGHT)){
            MSG="向右移动";
            pf.x+=moveStepH;
        }else if(finResStr.contains(jumpFlagGate)){
            MSG="移动至城门";
            scale = defaultScale;
            pf=Locator.PCityGate;
        }else if(finResStr.contains(jumpFlagBridge)){
            MSG="移动至桥";
            scale = defaultScale;
            pf=Locator.PBridge;
        } else if(finResStr.contains(volFlagUP)){
            int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            vol+=1;
            if(vol>=volMax)
                MSG="最大音量";
            else{
                MSG="增大音量";
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol
                        , AudioManager.FLAG_PLAY_SOUND);
            }
        }else if(finResStr.contains(volFlagDOWN)){

            int vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            vol-=1;
            if(vol<=volMin)
                MSG="最小音量";
            else{
                MSG="降低音量";
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol
                        , AudioManager.FLAG_PLAY_SOUND);
            }
        }
        Objects.requireNonNull(imageView.animateScaleAndCenter(scale,pf)).start();
        Toast.makeText(getApplicationContext(),MSG,Toast.LENGTH_SHORT).show();
        players.play();
    }

    //语音识别回调
    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
            MyRecogResult result = MyRecogResult.parseJson(params);
            if(result.getResultType().equals(RESULT_TYPE_FINAL)){
                onFinalResult(result.getBestResult());
            }
        }

//        if(name.equals(SpeechConstant.CALLBACK_EVENT_ASR_ERROR)||name.equals(SpeechConstant.CALLBACK_EVENT_ASR_EXIT)){
//            stop();
//            Toast.makeText(getApplicationContext(),"识别失败",Toast.LENGTH_SHORT).show();
//        }
        Log.i("onEvent", logTxt);
    }

    @SuppressLint("HandlerLeak")
    private void start() {
        mProgressBar.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_stop);

        Map<String, Object> params = startParams(getApplicationContext());
        String json = new JSONObject(params).toString();
        Log.i(TAG, "输入参数：" + json);
        myRecognizer.start(params);
    }

    private void stop() {
        mProgressBar.setVisibility(View.INVISIBLE);
        fab.setImageResource(R.drawable.ic_voice);
        myRecognizer.stop();
        Log.i(TAG,"停止识别：ASR_STOP");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setKeyWord();
        players.play();
    }

    @Override
    protected void onStop() {
        players.pause();
        stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        myRecognizer.release();
        players.release();
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(Main2Activity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
