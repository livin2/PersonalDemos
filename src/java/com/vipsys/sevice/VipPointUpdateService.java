package com.vipsys.sevice;

import com.vipsys.dao.PurHisDAO;
import com.vipsys.dao.VipInfoDao;
import com.vipsys.model.VipInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("vipPointUpdateService")
@Transactional
public class VipPointUpdateService {
    @Resource(name = "vipInfoDao")
    private VipInfoDao vipInfoDao;

    @Resource(name = "purHisDAO")
    private PurHisDAO purHisDAO;

    Integer update(int vid) {
        Integer newpoints = purHisDAO.getPointsChangeSUM(vid);
        VipInfo v = vipInfoDao.getVip(vid);
        v.setPoints(newpoints);
        vipInfoDao.updateVip(v);
        return newpoints;
    }
}
