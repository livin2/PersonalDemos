package com.vipsys.dao;

import com.vipsys.model.PurchaseHis;

import java.util.Date;
import java.util.List;

public interface PurHisDAO {
    PurchaseHis getWithId(int id);

    List<PurchaseHis> getAll();
    List<PurchaseHis> getWithVip(int vipid);
    List<PurchaseHis> getWithDate(Date date);
    List<PurchaseHis> getWithPointsChange(int Pchange);

    int getPointsChangeSUM(int vid);
}
