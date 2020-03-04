package com.vipsys.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.vipsys.dto.ExceptionDTOImpl;
import com.vipsys.dto.GeneralResponseDTO;
import com.vipsys.dto.VipInfoDTO;
import com.vipsys.sevice.InvaildIdException;
import com.vipsys.sevice.VipUpdateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller("addVipAction")
public class AddVipAction extends ActionSupport implements ModelDriven<VipInfoDTO> {
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

    private int newId;

    public int getNewId() {
        return newId;
    }

    public String FetchNewId() {
        newId = vipUpdateService.getANewId();
        logger.info("newid: " + newId);
        return "gotNewId";
    }

    @Override
    public String execute() throws Exception {
        try {
            vipUpdateService.addNewVip(vipInfoDTO);
            response.setStatus(200, "success");
            return SUCCESS;
        } catch (InvaildIdException e) {
            String msg = "Msg: " + e.getMessage()
                    + ",expectId: " + e.getExpectID()
                    + ",gotId: " + e.getGotID();
            actionErr.setStatus(400, msg);
            logger.info("ActionError added: " + msg);
            e.printStackTrace();
            return ERROR;
        }
    }

    @Override
    public VipInfoDTO getModel() {
        return vipInfoDTO;
    }
}
