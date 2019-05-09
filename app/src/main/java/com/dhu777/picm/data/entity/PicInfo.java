package com.dhu777.picm.data.entity;

import java.util.Date;

public class PicInfo {
    private String picId;
    private Date picCreateDate;
    private String picName;
    private Long picSize;
    private String picType;
    private String picOwner;

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public Date getPicCreateDate() {
        return picCreateDate;
    }

    public void setPicCreateDate(Date picCreateDate) {
        this.picCreateDate = picCreateDate;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public Long getPicSize() {
        return picSize;
    }

    public void setPicSize(Long picSize) {
        this.picSize = picSize;
    }

    public String getPicType() {
        return picType;
    }

    public void setPicType(String picType) {
        this.picType = picType;
    }

    public String getPicOwner() {
        return picOwner;
    }

    public void setPicOwner(String picOwner) {
        this.picOwner = picOwner;
    }
}
