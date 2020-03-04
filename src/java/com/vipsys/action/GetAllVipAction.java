package com.vipsys.action;

import com.opensymphony.xwork2.ActionSupport;
import com.vipsys.model.VipInfo;
import com.vipsys.sevice.VipGetService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

import static com.opensymphony.xwork2.Action.SUCCESS;

@Controller("getAllVipAction")
public class GetAllVipAction extends ActionSupport {

    @Resource(name = "vipGetService")
    private VipGetService vipGetServiceImp;

    private List<VipInfo> allVip;

    public List<VipInfo> getAllVip() {
        return allVip;
    }

    @Override
    public String execute() throws Exception {
        allVip = vipGetServiceImp.getAllVip();
        this.addActionMessage("getAllVipList():list size:" + allVip.size());
//            this.addActionError("test Error");
        return SUCCESS;
    }


}
