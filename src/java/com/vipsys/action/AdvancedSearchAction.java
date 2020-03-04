package com.vipsys.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.vipsys.dto.AdvSearchDTO;
import com.vipsys.dto.ExceptionDTOImpl;
import com.vipsys.dto.SearchVipDTO;
import com.vipsys.model.VipInfo;
import com.vipsys.sevice.VipGetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;

@Controller("AdvancedSearchAction")
public class AdvancedSearchAction extends ActionSupport implements ModelDriven<AdvSearchDTO> {

    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "vipGetService")
    private VipGetService vipGetServiceImp;

    @Resource(name = "advSearchDTO")
    private AdvSearchDTO vipInfoDTO;

    @Autowired
    private ExceptionDTOImpl actionErr;

    public ExceptionDTOImpl getActionErr() {
        return actionErr;
    }

    private List<VipInfo> vipList;

    public List<VipInfo> getVipList() {
        return vipList;
    }

    @Override
    public String execute() throws Exception {
        try {
            logger.info("AdvancedSearchAction: DTO: " + vipInfoDTO);
            vipList = vipGetServiceImp.getVipAdv(vipInfoDTO.getWheres(), vipInfoDTO);
            return SUCCESS;
        } catch (NoSuchElementException e) {
            actionErr.setStatus(404, e.getMessage());
            return ERROR;
        }
    }

    @Override
    public AdvSearchDTO getModel() {
        logger.info("getModel() executed");
        vipInfoDTO.clear();
        return vipInfoDTO;
    }
}
