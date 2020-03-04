package com.vipsys.model;

import org.apache.struts2.json.annotations.JSON;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class VipInfo {
    private int id;
    private String name;
    private int age;
    private int sex;
    private Date regDate;
    private String phone;
    private int points;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "Age")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Basic
    @Column(name = "Sex")
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Basic
    @Column(name = "RegDate")
    @JSON(format = "yyyy/MM/dd")
    public Date getRegDate() {
        return regDate;
    }

    @JSON(format = "yyyy/MM/dd")
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    @Basic
    @Column(name = "Phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "Points")
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VipInfo vipInfo = (VipInfo) o;

        if (id != vipInfo.id) return false;
        if (age != vipInfo.age) return false;
        if (sex != vipInfo.sex) return false;
        if (points != vipInfo.points) return false;
        if (name != null ? !name.equals(vipInfo.name) : vipInfo.name != null) return false;
        if (regDate != null ? !regDate.equals(vipInfo.regDate) : vipInfo.regDate != null) return false;
        if (phone != null ? !phone.equals(vipInfo.phone) : vipInfo.phone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + sex;
        result = 31 * result + (regDate != null ? regDate.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + points;
        return result;
    }
}
