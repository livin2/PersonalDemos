package com.vipsys.dao;

import com.vipsys.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class UserDAOTest {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
    @Autowired
    UserDAO userDAO;
    @Test
    public void get() {
        User user = userDAO.get(4);
        Assert.assertEquals("114",user.getPassword());
    }

    @Test
    public void getWithName() throws Exception{
        User user = userDAO.get("MrAqua");
        Assert.assertEquals("114",user.getPassword());
    }
}