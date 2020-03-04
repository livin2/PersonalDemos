package com.vipsys.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.vipsys.dto.*;
import com.vipsys.model.VipInfo;
import com.vipsys.sevice.VipUpdateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@Controller("upVipAction")
public class UpVipAction extends ActionSupport implements ModelDriven<VipInfoDTO> {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Resource(name = "vipUpdateService")
    private VipUpdateService vipUpdateService;

    @Resource(name = "generalVipDTO")
    private VipInfoDTO vipInfoDTO;

    @Resource(name = "generalResponseDTO")
    private GeneralResponseDTO response;

    public GeneralResponseDTO getResponse() {
        return response;
    }

    @Autowired
    private ExceptionDTOImpl actionErr;

    public ExceptionDTOImpl getActionErr() {
        return actionErr;
    }

    @Override
    public String execute() throws Exception {
        vipUpdateService.updateVip(vipInfoDTO);
        response.setStatus(200, "success");
        return SUCCESS;
    }

    @Override
    public VipInfoDTO getModel() {
        return vipInfoDTO;
    }
}
