package com.vipsys.sevice;

import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;

import java.util.List;

public interface VipGetService {
    VipInfo getVip(Integer uid);

    List<VipInfo> getAllVip();

    List<VipInfo> getVip(String where, VipInfoDTO vipDTO) throws Exception;

    List<VipInfo> getVipAdv(List<String> wheres, VipInfoDTO vipDTO) throws Exception;
}
