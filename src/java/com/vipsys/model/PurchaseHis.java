package com.vipsys.model;

import org.apache.struts2.json.annotations.JSON;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class PurchaseHis {
    private int id;
    private int vipid;
    private int pointsChange;
    private Date purDate;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vipid")
    public int getVipid() {
        return vipid;
    }

    public void setVipid(int vipid) {
        this.vipid = vipid;
    }

    @Basic
    @Column(name = "PointsChange")
    public int getPointsChange() {
        return pointsChange;
    }

    public void setPointsChange(int pointsChange) {
        this.pointsChange = pointsChange;
    }

    @Basic
    @Column(name = "PurDate")
    @JSON(format = "yyyy/MM/dd")
    public Date getPurDate() {
        return purDate;
    }

    @JSON(format = "yyyy/MM/dd")
    public void setPurDate(Date purDate) {
        this.purDate = purDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PurchaseHis that = (PurchaseHis) o;

        if (id != that.id) return false;
        if (vipid != that.vipid) return false;
        if (pointsChange != that.pointsChange) return false;
        if (purDate != null ? !purDate.equals(that.purDate) : that.purDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + vipid;
        result = 31 * result + pointsChange;
        result = 31 * result + (purDate != null ? purDate.hashCode() : 0);
        return result;
    }
}
