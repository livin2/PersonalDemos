package com.vipsys.action;

import com.vipsys.dto.ExceptionDTOImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.opensymphony.xwork2.Action;

@Aspect
@Component
public class ActionAspect {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    String ERROR = "error";

    @Autowired
    private ExceptionDTOImpl actionErr;

    @Pointcut("execution(* com.vipsys.action.*Action.execute())")
    private void actionExecute() {
    }

    @Pointcut("execution(* com.vipsys.action.*Action.FetchNewId())")
    private void FetchNewId() {
    }

    @Around("actionExecute() && FetchNewId()")
    public String around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            logger.info("cutpoint catch");
            String result = (String) joinPoint.proceed();
            logger.info("joinPoint return");
            return result;
        } catch (Exception e) {
//            logger.info("ActionError added: " + e.getMessage());
            actionErr.setStatus(500, "服务器抛出异常，请检查日志");
            e.printStackTrace();
            return ERROR;
        } finally {
            logger.info("finally");
        }
    }
}
