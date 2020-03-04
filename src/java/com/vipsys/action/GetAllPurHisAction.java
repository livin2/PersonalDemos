package com.vipsys.action;

import com.opensymphony.xwork2.ActionSupport;
import com.vipsys.dto.ExceptionDTOImpl;
import com.vipsys.dto.PHDetail;
import com.vipsys.model.PurchaseHis;
import com.vipsys.sevice.PHDetailGetService;
import com.vipsys.sevice.PurchaseGetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@Controller("GetAllPurHisAction")
public class GetAllPurHisAction extends ActionSupport {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "purchaseGetService")
    private PurchaseGetService phGetServiceImp;

    @Resource(name = "pHDetailGetService")
    private PHDetailGetService phDetailGetService;

    private List resultList;

    public List getResultList() {
        return resultList;
    }

    @Autowired
    private ExceptionDTOImpl actionErr;
    public ExceptionDTOImpl getActionErr() {
        return actionErr;
    }

    @Override
    public String execute() throws Exception {
        List<PurchaseHis> list = phGetServiceImp.getAll();
        resultList = phDetailGetService.phDetailFactory(list);
        return SUCCESS;
    }
}
