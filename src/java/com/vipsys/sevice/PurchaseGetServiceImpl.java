package com.vipsys.sevice;

import com.vipsys.dao.PurHisDAO;
import com.vipsys.dto.PurchaseDTO;
import com.vipsys.model.PurchaseHis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service("purchaseGetService")
public class PurchaseGetServiceImpl implements PurchaseGetService {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "purHisDAO")
    private PurHisDAO phDAO;

    @Override
    @Transactional
    public List<PurchaseHis> get(String where, PurchaseDTO phDTO) throws Exception {
        List<PurchaseHis> list;
        logger.info("purchaseGetService receive where: " + where);
        if (where.equals("id")) {
            PurchaseHis v = phDAO.getWithId(phDTO.getId());
            list = new ArrayList();
            list.add(v);
            return list;
        }

        Object value;
        switch (where) {
            case "vipid":
                list = phDAO.getWithVip(phDTO.getVipid());
                value = phDTO.getVipid();
                break;
            case "purDate":
                list = phDAO.getWithDate(phDTO.getPurDate());
                value = phDTO.getPurDate();
                break;
            case "pointsChange":
                list = phDAO.getWithPointsChange(phDTO.getPointsChange());
                value = phDTO.getPointsChange();
                break;
            default:
                throw new Exception("column name not found: " + where);
        }
        if (list.isEmpty())
            throw new NoSuchElementException("value not found: can't find " + value + " in " + where);
        return list;
    }

    @Override
    @Transactional
    public List<PurchaseHis> getAll() {
        return phDAO.getAll();
    }
}
