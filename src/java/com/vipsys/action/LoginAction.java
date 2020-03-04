package com.vipsys.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.vipsys.dto.ExceptionDTOImpl;
import com.vipsys.dto.GeneralResponseDTO;
import com.vipsys.dto.LoginDTO;
import com.vipsys.sevice.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.NoSuchElementException;

@Controller("loginAction")
public class LoginAction extends ActionSupport implements ModelDriven<LoginDTO> {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "loginDTO")
    private LoginDTO loginDTO;

    @Resource(name = "loginService")
    private LoginService loginService;

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
        try {
            logger.info(loginDTO.getName());
            logger.info(loginDTO.getPassword());
            Boolean res = loginService.checkPwd(loginDTO);
            if (res) {
                response.setStatus(200, "loginSuccess");
                logger.info("loginSuccess");
                return SUCCESS;
            } else {
                actionErr.setStatus(4031, "invaild password");
                return ERROR;
            }
        } catch (NoSuchElementException e) {
            actionErr.setStatus(4041, "invaild username");
            e.printStackTrace();
            logger.info("loginError");
            return ERROR;
        }
    }

    @Override
    public LoginDTO getModel() {
        logger.info("getModel");
        return loginDTO;
    }
}
