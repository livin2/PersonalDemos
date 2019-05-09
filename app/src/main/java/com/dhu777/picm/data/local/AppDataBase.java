package com.dhu777.picm.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.UserToken;

@Database(entities = {UserToken.class, PicInfo.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public static AppDataBase INSTANCE;
    public static AppDataBase getInstance(Context applicationContext){
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(applicationContext,
                    AppDataBase.class, "picM").build();
        }
        return INSTANCE;
    }
    public abstract UserTokenDAO userTokenDAO();
}
