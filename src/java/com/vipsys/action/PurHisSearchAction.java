package com.vipsys.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.vipsys.dto.ExceptionDTOImpl;
import com.vipsys.dto.PhSearchDTO;
import com.vipsys.dto.PurchaseDTO;
import com.vipsys.model.PurchaseHis;
import com.vipsys.sevice.PurchaseGetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;

@Controller("PurHisSearhcAction")
public class PurHisSearchAction extends ActionSupport implements ModelDriven<PurchaseDTO> {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "purchaseGetService")
    private PurchaseGetService phGetServiceImp;

    @Resource(name = "PhSearchDTO")
    private PhSearchDTO phSearchDTO;

    private List<PurchaseHis> resultList;

    public List<PurchaseHis> getResultList() {
        return resultList;
    }

    @Autowired
    private ExceptionDTOImpl actionErr;

    public ExceptionDTOImpl getActionErr() {
        return actionErr;
    }

    @Override
    public String execute() throws Exception {
        logger.info("SearchPurHis getParam key: " + phSearchDTO.getKey());
        try {
            resultList = phGetServiceImp.get(phSearchDTO.getKey(), phSearchDTO);
            return SUCCESS;
        } catch (NoSuchElementException e) {
            actionErr.setStatus(404, e.getMessage());
            return ERROR;
        }
    }

    @Override
    public PurchaseDTO getModel() {
        return phSearchDTO;
    }
}
