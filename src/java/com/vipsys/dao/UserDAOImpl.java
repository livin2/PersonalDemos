package com.vipsys.dao;

import com.vipsys.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.NoSuchElementException;

@Repository("userDAO")
public class UserDAOImpl implements UserDAO {
    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public User get(int uid) {
        Session session = sessionFactory.getCurrentSession();
        User user = session.get(User.class, uid);
        return user;
    }

    @Override
    @Transactional
    public User get(String uname) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM User WHERE uname = :uname");
        query.setParameter("uname", uname);
        List<User> list = query.getResultList();
        if (list.isEmpty())
            throw new NoSuchElementException();
        return list.get(0);
    }
}
