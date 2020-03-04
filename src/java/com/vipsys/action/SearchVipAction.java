package com.vipsys.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
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

@Controller("SearchVipAction")
public class SearchVipAction extends ActionSupport implements ModelDriven<SearchVipDTO> {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "vipGetService")
    private VipGetService vipGetServiceImp;

    @Resource(name = "SearchVipDTO")
    private SearchVipDTO vipInfoDTO;

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
        logger.info("SearchVip getParam key: " + vipInfoDTO.getKey());
        logger.info("SearchVip getParam sex: " + vipInfoDTO.getSex());
        logger.info("SearchVip getParam val: " + vipInfoDTO + " " + vipInfoDTO.getClass());

        try {
            vipList = vipGetServiceImp.getVip(vipInfoDTO.getKey(), vipInfoDTO);
            return SUCCESS;
        } catch (NoSuchElementException e) {
            actionErr.setStatus(404, e.getMessage());
            return ERROR;
        }
    }

    @Override
    public SearchVipDTO getModel() {
        logger.info("getModel() executed");
        return vipInfoDTO;
    }
}
