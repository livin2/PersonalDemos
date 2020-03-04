package com.vipsys.sevice;

import com.vipsys.dto.VipInfoDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class VipUpdateServiceTest {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired
    VipUpdateService vipUpdateService;

    @Resource(name = "generalVipDTO")
    VipInfoDTO vipInfoDTO;

    Date date;


    @Before
    public void setUp() throws Exception {
        String datestr = "2019/04/14";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        date = sdf.parse(datestr);

//        vipInfoDTO.setId(7);
        vipInfoDTO.setName("Dimo");
        vipInfoDTO.setAge(17);
        vipInfoDTO.setSex(0);
        vipInfoDTO.setPhone("13851235035");
        vipInfoDTO.setRegDate(date);
        vipInfoDTO.setPoints(0);
    }

    @Test
    public void addNew() throws Exception{
        int res = vipUpdateService.getANewId();
        logger.info(res);
        vipInfoDTO.setId(res);
        vipUpdateService.addNewVip(vipInfoDTO);
    }

    @Test
    public void updateVip() throws Exception{
        vipInfoDTO.setId(4);
        vipUpdateService.updateVip(vipInfoDTO);
    }

    @Test
    public void deleteVip() throws Exception{
//        vipInfoDTO.setId(7);
        vipUpdateService.deleteVip(7);
    }
}