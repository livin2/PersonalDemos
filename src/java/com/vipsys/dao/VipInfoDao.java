package com.vipsys.dao;

import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;
import org.hibernate.criterion.DetachedCriteria;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VipInfoDao {
    VipInfo getVip(int vid);

    List<VipInfo> getAllVip();

    int getMAXID();

    void addNewVip(VipInfo nVip);

    void updateVip(VipInfo nVip);

    void deleteVip(int vipid);

    List<VipInfo> getWithName(String name);

    List<VipInfo> getWithAge(int age);

    List<VipInfo> getWithSex(int sex);

    List<VipInfo> getWithRegDate(Date regDate);

    List<VipInfo> getWithPhone(String phone);

    List<VipInfo> getWithPoints(int points);

    List<VipInfo> dynamicSearch(List<String> wheres, VipInfo vipDTO) throws Exception;
}
