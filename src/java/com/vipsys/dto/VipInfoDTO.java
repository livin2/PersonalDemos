package com.vipsys.dto;

import com.vipsys.model.VipInfo;
import org.apache.struts2.json.annotations.JSON;

import java.util.Date;

public interface VipInfoDTO {
    public void setId(Integer id);

    public void setName(String name);

    public void setAge(Integer age);

    public void setSex(Integer sex);

    @JSON(format = "yyyy/MM/dd")
    public void setRegDate(Date regDate);

    public void setPhone(String phone);

    public void setPoints(Integer points);

    public Integer getId();

    public String getName();

    public Integer getAge();

    public Integer getSex();

    public Date getRegDate();

    public String getPhone();

    public Integer getPoints();

    public VipInfo getVipInfo();
}
