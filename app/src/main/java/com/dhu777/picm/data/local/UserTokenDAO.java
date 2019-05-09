package com.dhu777.picm.data.local;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.dhu777.picm.data.entity.UserToken;

import java.util.List;

public interface UserTokenDAO {
    @Query("SELECT * FROM UserToken WHERE id = :id LIMIT 1")
    UserToken getById(int id);

    @Query("SELECT * FROM UserToken WHERE name LIKE :name LIMIT 1")
    UserToken getByName(String name);

    @Delete
    void delete(UserToken user);

    @Insert
    void insert(UserToken user);

}
