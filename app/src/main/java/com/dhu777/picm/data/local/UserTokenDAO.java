package com.dhu777.picm.data.local;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.dhu777.picm.data.entity.UserToken;

import java.util.List;

@Dao
public interface UserTokenDAO {
    @Query("SELECT * FROM UserToken WHERE id = :id LIMIT 1")
    UserToken getById(@NonNull int id);

    @Query("SELECT * FROM UserToken WHERE name LIKE :name LIMIT 1")
    UserToken getByName(@NonNull String name);

    @Delete
    void delete(@NonNull UserToken user);

    @Insert
    void insert(@NonNull UserToken user);

}
