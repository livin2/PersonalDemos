package com.vipsys.dto;


import org.springframework.stereotype.Component;

import java.util.Date;

@Component("SearchVipDTO")
public class SearchVipDTO extends BaseVipDTO {
    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
