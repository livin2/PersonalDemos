package com.vipsys.sevice;

import com.vipsys.dto.PhSearchDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class PurchaseGetServiceTest {
    @Autowired
    PurchaseGetService pHGetService;

    @Autowired
    PhSearchDTO phSearchDTO;

    @Before
    public void setUp() throws Exception {
        phSearchDTO.setKey("pointsChange");
        phSearchDTO.setPointsChange(2);
    }

    @Test
    public void get() throws Exception{
        List list = pHGetService.get(phSearchDTO.getKey(),phSearchDTO);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void getAll() {
       List list = pHGetService.getAll();
        Assert.assertEquals(2, list.size());
    }
}