package com.dhu777.picm.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ComUtil extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        if (context == null)
            Log.d(TAG, "getContext: null");
        return context;
    }

    public static <T> T checkNotNull(T reference) throws NullPointerException{
        if (reference == null) {
            Log.e("checkNotNull", "arg must not be null");
            throw new NullPointerException("arg must not be null");
        }
        return reference;
    }

    public static void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }
}
