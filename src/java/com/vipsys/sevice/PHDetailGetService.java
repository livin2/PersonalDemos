package com.vipsys.sevice;

import com.vipsys.dto.PHDetail;
import com.vipsys.model.PurchaseHis;
import com.vipsys.model.VipInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("pHDetailGetService")
public class PHDetailGetService {
    @Resource(name = "vipGetService")
    private VipGetService vipGetService;

    @Resource(name = "purchaseGetService")
    private PurchaseGetService purchaseGetService;

    @Resource(name = "springContextUtils")
    private SpringContextUtils wac;


    public PHDetail phDetailFactory(PurchaseHis ph) {
        VipInfo v = vipGetService.getVip(ph.getVipid());
        PHDetail phobj = (PHDetail) wac.getBean(PHDetail.class);
        phobj.setId(ph.getId());
        phobj.setVipid(ph.getVipid());
        phobj.setPointsChange(ph.getPointsChange());
        phobj.setPurDate(ph.getPurDate());
        phobj.setName(v.getName());
        phobj.setPoints(v.getPoints());
        return phobj;
    }

    public List<PHDetail> phDetailFactory(List<PurchaseHis> phs) {
        List list = new ArrayList();
        for (PurchaseHis ph : phs) {
            list.add(phDetailFactory(ph));
        }
        return list;
    }


}
