package com.vipsys.dao;

import com.vipsys.model.VipInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class VipInfoDaoImpATest {

    @Autowired
    VipInfoDao infoDaoImpA;

    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Test
    public void getAllVip() {
        List<VipInfo> list = infoDaoImpA.getAllVip();
        for (VipInfo v : list) {
            System.out.println("getAllvip: " + v.getId() + " " + v.getRegDate());
        }
    }

    @Test
    public void getVip() {
        try {
            VipInfo v = infoDaoImpA.getVip(3);
            System.out.println("getvip: " + v.getName());
            Assert.assertEquals("Mickey", v.getName());
        } catch (Exception e) {
            logger.info("catching Exception");
            e.printStackTrace();
        }
    }

    @Test
    public void getWithName() {
        List<VipInfo> list = infoDaoImpA.getWithName("Mickey");
        for (VipInfo v : list) {
            System.out.println("getWithName: " + v.getName());
            Assert.assertEquals("Mickey", v.getName());
        }
    }

    @Test
    public void getWithAge() {
        List<VipInfo> list = infoDaoImpA.getWithAge(22);
        for (VipInfo v : list) {
            System.out.println("getWithAge: " + v.getName());
            Assert.assertEquals("Mickey", v.getName());
        }
    }

    @Test
    public void getWithSex() {
        List<VipInfo> list = infoDaoImpA.getWithSex(1);
        for (VipInfo v : list) {
            System.out.println("getWithSex: " + v.getName());
//            Assert.assertEquals("Mickey", v.getName());
        }
    }

    @Test
    public void getWithRegDate() {
        String datestr = "2019/04/08";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//        ParsePosition pos = new ParsePosition(0);
        try {
            Date date = sdf.parse(datestr);
            List<VipInfo> list = infoDaoImpA.getWithRegDate(date);
            for (VipInfo v : list) {
                System.out.println("getWithRegDate: " + v.getName());
                Assert.assertEquals("Mickey", v.getName());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Test
    public void getWithPhone() {
        List<VipInfo> list = infoDaoImpA.getWithPhone("13233237777");
        for (VipInfo v : list) {
            System.out.println("getWithPhone: " + v.getName());
            Assert.assertEquals("Mickey", v.getName());
        }
    }

    @Transactional
    @Test
    public void getWithPoints() {
        List<VipInfo> list = infoDaoImpA.getWithPoints(0);
        for (VipInfo v : list) {
            System.out.println("getWithPoints: " + v.getName());
//            Assert.assertEquals("Mickey", v.getName());
        }
    }
}