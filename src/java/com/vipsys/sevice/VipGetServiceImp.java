package com.vipsys.sevice;

import com.vipsys.dao.VipInfoDao;
import com.vipsys.dto.AdvSearchDTO;
import com.vipsys.dto.SearchVipDTO;
import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service("vipGetService")
public class VipGetServiceImp implements VipGetService {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "vipInfoDao")
    private VipInfoDao vipInfoDao;

    @Transactional
    @Override
    public VipInfo getVip(Integer uid) {
        VipInfo vipInfo = vipInfoDao.getVip(uid);
        logger.info("service return vipInfo: " + vipInfo.getName());
        return vipInfo;
    }

    @Transactional
    @Override
    public List<VipInfo> getAllVip() {
        List<VipInfo> vipInfoList = vipInfoDao.getAllVip();
        logger.info("service return viplist: " + vipInfoList.size());
        return vipInfoList;
    }

    @Transactional
    @Override
    public List<VipInfo> getVip(String where, VipInfoDTO val) throws Exception {
//        String where = val.getKey();
        List<VipInfo> list;
        logger.info("vipGetService receive where: " + where);
        if (where.equals("id")) {
            VipInfo v = vipInfoDao.getVip(val.getId());
            if (v == null)
                throw new NoSuchElementException();
            list = new ArrayList();
            list.add(v);
            return list;
        }
        Object value;
        switch (where) {
            case "name":
                list = vipInfoDao.getWithName(val.getName());
                value = val.getName();
                break;
            case "age":
                list = vipInfoDao.getWithAge(val.getAge());
                value = val.getAge();
                break;
            case "sex":
                list = vipInfoDao.getWithSex(val.getSex());
                value = val.getSex();
                break;
            case "regDate":
                list = vipInfoDao.getWithRegDate(val.getRegDate());
                value = val.getRegDate();
                break;
            case "phone":
                list = vipInfoDao.getWithPhone(val.getPhone());
                value = val.getPhone();
                break;
            case "points":
                list = vipInfoDao.getWithPoints(val.getPoints());
                value = val.getPoints();
                break;
            default:
                throw new Exception("column name not found: " + where);

        }
        if (list.isEmpty())
            throw new NoSuchElementException("value not found: can't find " + value + " in " + where);
        return list;

    }

    @Transactional
    @Override
    public List<VipInfo> getVipAdv(List<String> wheres, VipInfoDTO vipDTO) throws Exception {
        List<VipInfo> list = vipInfoDao.dynamicSearch(wheres, vipDTO.getVipInfo());
        if (list.isEmpty())
            throw new NoSuchElementException("value not found");
        return list;
    }


}
