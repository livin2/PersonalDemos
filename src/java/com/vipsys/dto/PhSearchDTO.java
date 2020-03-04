package com.vipsys.dto;

import org.apache.struts2.json.annotations.JSON;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("PhSearchDTO")
public class PhSearchDTO implements PurchaseDTO {
    int id;
    int vipid;
    int pointsChange;
    Date purDate;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getVipid() {
        return vipid;
    }

    @Override
    public void setVipid(int vipid) {
        this.vipid = vipid;
    }

    @Override
    public int getPointsChange() {
        return pointsChange;
    }

    @Override
    public void setPointsChange(int pointsChange) {
        this.pointsChange = pointsChange;
    }

    @Override
    @JSON(format = "yyyy/MM/dd")
    public Date getPurDate() {
        return purDate;
    }

    @Override
    @JSON(format = "yyyy/MM/dd")
    public void setPurDate(Date purDate) {
        this.purDate = purDate;
    }

    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
