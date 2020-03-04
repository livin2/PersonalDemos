package com.vipsys.dto;

//response.data为一json对象{errCode:?,errMsg:?}
public interface ExceptionDTO {
    int getErrCode();

    String getErrMsg();
}
