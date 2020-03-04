package com.vipsys.dao;

import com.vipsys.model.PurchaseHis;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class PurHisDAOTest {

    @Autowired
    PurHisDAO purHisDAO;

    @Test
    public void getWithId() {
        PurchaseHis p = purHisDAO.getWithId(1);
        Assert.assertEquals(4,p.getVipid());
    }

    @Test
    public void getAll() {
        List list = purHisDAO.getAll();
        Assert.assertEquals(2,list.size());
    }

    @Test
    public void getWithVip() {
        List list = purHisDAO.getWithVip(4);
        Assert.assertEquals(2,list.size());
    }

    @Test
    public void getWithDate() {
        String datestr = "2019/04/14";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//        ParsePosition pos = new ParsePosition(0);
        try {
            Date date = sdf.parse(datestr);
            List list = purHisDAO.getWithDate(date);
            Assert.assertEquals(1,list.size());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getWithPointsChange() {
        List list = purHisDAO.getWithPointsChange(2);
        Assert.assertEquals(1,list.size());
    }

    @Test
    public void getPointsChangeSUM() {
        int res = purHisDAO.getPointsChangeSUM(4);
        Assert.assertEquals(3,res);
    }
}