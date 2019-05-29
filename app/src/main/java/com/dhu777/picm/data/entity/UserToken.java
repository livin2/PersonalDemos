package com.dhu777.picm.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserToken {
    @PrimaryKey
    private int id;
    private String name;
    private String Token;
    private Long dueTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public Long getDueTime() {
        return dueTime;
    }

    public void setDueTime(Long dueTime) {
        this.dueTime = dueTime;
    }
}
