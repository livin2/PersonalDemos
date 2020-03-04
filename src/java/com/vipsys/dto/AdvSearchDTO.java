package com.vipsys.dto;


import org.apache.struts2.json.annotations.JSON;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("advSearchDTO")
public class AdvSearchDTO extends BaseVipDTO {
    private List<String> wheres = new ArrayList<>();

    public List<String> getWheres() {
        return wheres;
    }

    public void clear() {
        wheres.clear();
    }

    ;

    @Override
    public void setId(Integer id) {
        wheres.add("id");
        super.setId(id);
    }

    @Override
    public void setName(String name) {
        wheres.add("name");
        super.setName(name);
    }

    @Override
    public void setAge(Integer age) {
        wheres.add("age");
        super.setAge(age);
    }

    @Override
    public void setSex(Integer sex) {
        wheres.add("sex");
        super.setSex(sex);
    }


    @Override
    @JSON(format = "yyyy/MM/dd")
    public void setRegDate(Date regDate) {
        wheres.add("regDate");
        super.setRegDate(regDate);
    }

    @Override
    public void setPhone(String phone) {
        wheres.add("phone");
        super.setPhone(phone);
    }

    @Override
    public void setPoints(Integer points) {
        wheres.add("points");
        super.setPoints(points);
    }
}
