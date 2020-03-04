package com.vipsys.dto;

import org.apache.struts2.json.annotations.JSON;

import javax.persistence.Basic;
import javax.persistence.Column;
import java.util.Date;

public interface PurchaseDTO {
    public int getId();
    public void setId(int id);

    public int getVipid();
    public void setVipid(int vipid);

    @JSON(format = "yyyy/MM/dd")
    public Date getPurDate();

    @JSON(format = "yyyy/MM/dd")
    public void setPurDate(Date purDate);

    public int getPointsChange();

    public void setPointsChange(int pointsChange);


}
