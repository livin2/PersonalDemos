package com.dhu777.picm.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.data.entity.UserToken;

import static com.dhu777.picm.util.ComUtil.checkNotNull;

@Database(entities = {UserToken.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public static AppDataBase INSTANCE;
    public static AppDataBase getInstance(@NonNull final Context context){
        if (INSTANCE == null) {
            synchronized ( AppDataBase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(checkNotNull(context).getApplicationContext(),
                            AppDataBase.class, "picm_db").build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract UserTokenDAO userTokenDAO();
}
