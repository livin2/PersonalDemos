package com.vipsys.sevice;

import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;

public interface VipUpdateService {
    int getANewId();

    void addNewVip(VipInfoDTO nVip) throws Exception;

    void updateVip(VipInfoDTO nVip) throws Exception;

    void deleteVip(int vipid) throws Exception;
}
