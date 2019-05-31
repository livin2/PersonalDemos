package com.dhu777.picm.data.entity;

import android.content.Intent;

import androidx.room.Entity;

import com.dhu777.picm.data.remote.PicRemoteContract;
import com.dhu777.picm.mock.Injection;

import java.util.Date;


@Entity
public class PicInfo {
    private String picId;
    private String picName;
    private Date picLastModify;
    private String picType;
    private Long picSize;
    private String userName;
    private String picURL;

    public String getPicId() {
        return picId==null?"null":picId;
    }
    public void setPicId(String picId) { this.picId = picId; }
    public Date getPicLastModify() { return picLastModify==null?new Date():picLastModify; }
    public void setPicLastModify(Date picLastModify) { this.picLastModify = picLastModify; }
    public String getPicName() {
        return picName==null?"null":picName;
    }
    public void setPicName(String picName) {
        this.picName = picName;
    }
    public String getPicType() {
        return picType==null?"null":picType;
    }
    public void setPicType(String picType) {
        this.picType = picType;
    }
    public String getUserName() { return userName==null?"null":userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getPicSize() { return picSize==null?0:picSize; }
    public void setPicSize(Long picSize) { this.picSize = picSize; }


    public void setPicURL(String picURL) { this.picURL = picURL; }
    public String getPicURL() {
        if (Injection.mode == Injection.REAL){
            return Injection.provideRemoteUnivesalURL()+"pic/"+picId;
        }else{
            return this.picURL;
        }
    }

    public String getThumbURL() {
        if (Injection.mode == Injection.REAL){
            return Injection.provideRemoteUnivesalURL()+"thumbnail/"+picId;
        }else{
            return this.picURL;
        }
    }

    private Integer width;
    private Integer height;

    public Integer getWidth() { return width==null?1:width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height==null?1:height; }
    public void setHeight(Integer height) { this.height = height; }
}
