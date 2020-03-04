package com.vipsys.dao;

import com.vipsys.model.PurchaseHis;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("purHisDAO")
public class PurHisDAOImpl implements PurHisDAO {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public PurchaseHis getWithId(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(PurchaseHis.class, id);
    }

    @Override
    @Transactional
    public List<PurchaseHis> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM PurchaseHis order by id");
        List list = query.list();
        logger.info("Dao return all purchaseHis: " + list.size());
        return list;
    }

    @Override
    @Transactional
    public List<PurchaseHis> getWithVip(int vipid) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM PurchaseHis WHERE vipid = :vipid order by id");
        query.setParameter("vipid", vipid);
        List list = query.list();
        return list;
    }

    @Override
    @Transactional
    public List<PurchaseHis> getWithDate(Date date) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM PurchaseHis WHERE purDate = :purDate order by id");
        query.setParameter("purDate", date);
        return query.list();
    }

    @Override
    @Transactional
    public List<PurchaseHis> getWithPointsChange(int Pchange) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM PurchaseHis WHERE pointsChange = :pChange order by id");
        query.setParameter("pChange", Pchange);
        return query.list();
    }

    @Override
    @Transactional
    public int getPointsChangeSUM(int vid) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("select sum(p.pointsChange) FROM PurchaseHis p WHERE vipid = :vipid");
        query.setParameter("vipid", vid);
        Long res = (Long) query.getSingleResult();
        if(res!=null)
            return res.intValue();
        return 0;
    }
}
