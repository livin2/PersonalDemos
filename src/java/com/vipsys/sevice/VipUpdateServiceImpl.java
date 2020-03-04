package com.vipsys.sevice;

import com.vipsys.dao.VipInfoDao;
import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("vipUpdateService")
public class VipUpdateServiceImpl implements VipUpdateService {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "vipInfoDao")
    private VipInfoDao vipInfoDao;

    private static int nullID = -1;
    private int newid = nullID;

    //Service由Spring装配 默认单例存在于对象池中 运行时保持状态
    //故而为当前服务维护一个newid的可以用于验证提交给addNewVip()对象的合法性：
    //每次给addNewVip提交数据对象之前应getANewId并赋值给数据对象
    @Override
    public int getANewId() {
        if (newid == nullID) {
            newid = vipInfoDao.getMAXID() + 1;
        }
        return newid;
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void addNewVip(VipInfoDTO nVip) throws Exception {
        if (nVip.getId() != newid)
            throw new InvaildIdException("id not match newid", nVip.getId(), newid);
        vipInfoDao.addNewVip(nVip.getVipInfo());
        newid = nullID;
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void updateVip(VipInfoDTO nVip) throws Exception {
        //id
        vipInfoDao.updateVip(nVip.getVipInfo());
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public void deleteVip(int vipid) throws Exception {
        //id
        vipInfoDao.deleteVip(vipid);
    }
}
