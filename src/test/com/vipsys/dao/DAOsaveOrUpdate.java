package com.vipsys.dao;

import com.vipsys.dto.BaseVipDTO;
import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class DAOsaveOrUpdate {

    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Autowired
    VipInfoDao infoDaoImpA;

    @Test
    public void getMAXID() {
        int re = infoDaoImpA.getMAXID();
        logger.info(re);
    }

    @Test
    public void addNewVip() {
        VipInfo v = new VipInfo();
        v.setId(3);
        v.setName("Japc");
        v.setAge(25);
        v.setPoints(9);
        v.setRegDate(new Date());
        v.setPhone("13899992344");
        try {
            infoDaoImpA.addNewVip(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateVip() {
        VipInfo v = new VipInfo();
        v.setId(4);
        v.setName("Kapc");
        v.setAge(27);
        v.setPoints(8);
        v.setRegDate(new Date());
        v.setPhone("13899232344");
        try {
            infoDaoImpA.addNewVip(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteV() {
        infoDaoImpA.deleteVip(4);
        infoDaoImpA.deleteVip(3);

    }

}