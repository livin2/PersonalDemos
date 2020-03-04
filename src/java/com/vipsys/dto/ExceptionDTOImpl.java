package com.vipsys.dto;

import org.springframework.stereotype.Component;

@Component("generalErrorDTO")
public class ExceptionDTOImpl implements ExceptionDTO {
    private int errCode;
    private String errMsg;

    public void setStatus(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMsg() {
        return errMsg;
    }
}
