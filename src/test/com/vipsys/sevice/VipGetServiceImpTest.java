package com.vipsys.sevice;

import com.vipsys.dao.VipInfoDao;
import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class VipGetServiceImpTest {
    @Autowired
    VipGetService vipGetService;

    @Resource(name = "generalVipDTO")
    VipInfoDTO vipInfoDTO;

    Date date;

    @Before
    public void setUp() throws Exception {
        String datestr = "2019/04/12";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        date = sdf.parse(datestr);


        vipInfoDTO.setId(4);
        vipInfoDTO.setName("Diwmo");
        vipInfoDTO.setAge(14);
        vipInfoDTO.setSex(1);
        vipInfoDTO.setPhone("13851235035");
        vipInfoDTO.setRegDate(date);
        vipInfoDTO.setPoints(3);

    }

    @Test
    public void getVipId() throws Exception {
        List<VipInfo> list = vipGetService.getVip("id", vipInfoDTO);
//        VipInfo v = vipGetService.getVip(1);
        for (VipInfo v : list) {
            System.out.println("getVipId: " + v.getName());
            Assert.assertEquals("Diwmo", v.getName());
        }
    }

    @Test
    public void getVipName() throws Exception {
        List<VipInfo> list = vipGetService.getVip("name", vipInfoDTO);
        for (VipInfo v : list) {
            System.out.println("getVipName: " + v.getName());
            Assert.assertEquals("Diwmo", v.getName());
        }
    }

    @Test
    public void getVipAge() throws Exception {
        List<VipInfo> list = vipGetService.getVip("age", vipInfoDTO);
        for (VipInfo v : list) {
            System.out.println("getVipAge: " + v.getName());
            Assert.assertEquals("Diwmo", v.getName());
        }
    }

    @Test
    public void getVipSex() throws Exception {
        List<VipInfo> list = vipGetService.getVip("sex", vipInfoDTO);
        Assert.assertEquals(4, list.size());
//        for (VipInfo v : list) {
//            System.out.println("getVipSex: " + v.getName());
//            Assert.assertEquals("Mickey", v.getName());
//        }
    }

    @Test
    public void getVipRegDate() throws Exception {

        List<VipInfo> list = vipGetService.getVip("regDate", vipInfoDTO);
        Assert.assertEquals(1, list.size());
//        for (VipInfo v : list) {
//            System.out.println("getWithRegDate: " + v.getName());
//            Assert.assertEquals("Mickey", v.getName());
//        }
    }

    @Test
    public void getVipPhone() throws Exception {
        List<VipInfo> list = vipGetService.getVip("phone", vipInfoDTO);
        for (VipInfo v : list) {
            System.out.println("getVipPhone: " + v.getName());
            Assert.assertEquals("Diwmo", v.getName());
        }
    }

    @Test
    public void getVipPoints() throws Exception {
        List<VipInfo> list = vipGetService.getVip("points", vipInfoDTO);
        for (VipInfo v : list) {
            System.out.println("getVipPoints: " + v.getName());
            Assert.assertEquals("Diwmo", v.getName());
        }
    }
}