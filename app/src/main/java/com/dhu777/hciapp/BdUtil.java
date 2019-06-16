package com.dhu777.hciapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.aip.asrwakeup3.core.mini.AutoCheck;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.baidu.speech.asr.SpeechConstant.BOT_ID;

/**
 * 用于baidu语音识别的工具类
 */
@SuppressWarnings("WeakerAccess")
public class BdUtil {
    /** 标识Baidu语音识别结束*/
    public static final String RESULT_TYPE_FINAL = "final_result";

    /**
     * @return 默认会话参数
     */
    public static JSONArray unitParams() {
        JSONArray json = new JSONArray();
        try {
            JSONObject bot = new JSONObject();
            bot.put("bot_id",BOT_ID);
            bot.put("bot_session_id","");
            bot.put("bot_session","");
            json.put(bot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * @param context 应用上下文
     * @return 返回启动语音识别的默认参数列表
     */
    @SuppressLint("HandlerLeak")
    public static Map<String, Object> startParams(Context context) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.PID, 15364);
        params.put(SpeechConstant.DISABLE_PUNCTUATION, true);
        params.put(SpeechConstant.BOT_SESSION_LIST, unitParams());

        (new AutoCheck(context.getApplicationContext(), new Handler() {
            @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
                        Log.w("AutoCheckMessage", message);
                    }
                }
            }
        },false)).checkAsr(params);

       return params;
    }


    /**
     * 申请baidu语音识别需要的权限
     * @param activity 需要权限的Activity
     */
    public static void initPermission(Activity activity) {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限
                Log.d("initPermission", "FAILED");
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), 123);
        }
    }
}
