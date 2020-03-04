package com.vipsys.dto;

import org.springframework.stereotype.Component;

@Component("generalResponseDTO")
public class GeneralResponseDTO implements ResponseDTO {
    private int code;
    private String msg;

    public void setStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
