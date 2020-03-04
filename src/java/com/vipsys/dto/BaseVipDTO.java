package com.vipsys.dto;

import com.vipsys.model.VipInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import java.util.Date;

public class BaseVipDTO implements VipInfoDTO {
    private VipInfo vipValue = new VipInfo();

    public void setId(Integer id) {
        vipValue.setId(id);
    }

    public void setName(String name) {
        vipValue.setName(name);
    }

    public void setAge(Integer age) {
        vipValue.setAge(age);
    }

    public void setSex(Integer sex) {
        vipValue.setSex(sex);
    }

    @JSON(format = "yyyy/MM/dd")
    public void setRegDate(Date regDate) {
        vipValue.setRegDate(regDate);
    }

    public void setPhone(String phone) {
        vipValue.setPhone(phone);
    }

    public void setPoints(Integer points) {
        vipValue.setPoints(points);
    }

    public Integer getId() {
        return vipValue.getId();
    }

    public String getName() {
        return vipValue.getName();
    }

    public Integer getAge() {
        return vipValue.getAge();
    }

    public Integer getSex() {
        return vipValue.getSex();
    }

    public Date getRegDate() {
        return vipValue.getRegDate();
    }

    public String getPhone() {
        return vipValue.getPhone();
    }

    public Integer getPoints() {
        return vipValue.getPoints();
    }

    @Override
    public VipInfo getVipInfo() {
        return vipValue;
    }

    @Override
    public String toString() {
        String val = "id: " + getId() + ",name: " + getName() + ",age: " + getAge()
                + ",sex: " + getSex() + ",phone: " + getPhone() + ",points: " + getPoints()
                + ",RegDate: " + getRegDate();
        return val;
    }
}
