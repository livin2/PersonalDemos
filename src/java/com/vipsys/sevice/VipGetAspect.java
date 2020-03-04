package com.vipsys.sevice;

import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Aspect
@Component
public class VipGetAspect {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "vipPointUpdateService")
    VipPointUpdateService vipPointUpdateService;

    @Before("bean(vipGetService)&&execution(* getVip())&&args(id)")
    public void beforeGet(int id) {
        vipPointUpdateService.update(id);
    }


    @AfterReturning(
            pointcut = "bean(vipGetService)&&execution(java.util.List get*(..))",
            returning = "result")
    public void AfterAll(JoinPoint jp, Object result) {
        logger.info(jp.getSignature());
        for (VipInfo v : (List<VipInfo>) result) {
            Integer newPoints = vipPointUpdateService.update(v.getId());
            v.setPoints(newPoints);
        }
    }
}
