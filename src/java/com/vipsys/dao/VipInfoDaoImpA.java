package com.vipsys.dao;

import com.vipsys.dto.BaseVipDTO;
import com.vipsys.dto.VipInfoDTO;
import com.vipsys.model.VipInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Repository("vipInfoDao")
public class VipInfoDaoImpA implements VipInfoDao {
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public List<VipInfo> getAllVip() {
        Session session = sessionFactory.getCurrentSession();
        List<VipInfo> vips = new ArrayList<VipInfo>();
        Query query = session.createQuery("FROM VipInfo order by id");
        List list = query.list();
        logger.info("Dao return viplist: " + list.size());
        return list;
    }

    @Override
    @Transactional
    public VipInfo getVip(int vid) {
        Session session = sessionFactory.getCurrentSession();
        VipInfo vipInfo = session.get(VipInfo.class, vid);
        logger.info("Dao return vipInfo: " + vipInfo.getName());
        return vipInfo;
    }



    @Override
    @Transactional
    public int getMAXID() {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("SELECT max(v.id) FROM VipInfo v");
        return (Integer) query.getSingleResult();
    }

    @Override
    @Transactional
    public void addNewVip(VipInfo nVip) {
        Session session = sessionFactory.getCurrentSession();
        session.save(nVip);
    }

    @Override
    @Transactional
    public void updateVip(VipInfo nVip) {
        Session session = sessionFactory.getCurrentSession();
        session.update(nVip);
    }

    @Override
    @Transactional
    public void deleteVip(int vipid) {
        Session session = sessionFactory.getCurrentSession();
        VipInfo v = new VipInfo();
        v.setId(vipid);
        session.delete(v);
    }

    @Override
    @Transactional
    public List<VipInfo> getWithName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM VipInfo WHERE name = :name order by id");
        query.setParameter("name", name);
//        List list = query.list();
//        System.out.println(list);
        return query.list();
    }

    @Override
    @Transactional
    public List<VipInfo> getWithAge(int age) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM VipInfo WHERE age = :age order by id");
        query.setParameter("age", age);
        return query.list();
    }

    @Transactional
    @Override
    public List<VipInfo> getWithSex(int sex) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM VipInfo WHERE sex = :sex order by id");
        query.setParameter("sex", sex);
        return query.list();
    }

    @Override
    @Transactional
    public List<VipInfo> getWithRegDate(Date regDate) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM VipInfo WHERE regDate = :regDate order by id");
        query.setParameter("regDate", regDate);
        return query.list();
    }

    @Override
    @Transactional
    public List<VipInfo> getWithPhone(String phone) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM VipInfo WHERE phone = :phone order by id");
        query.setParameter("phone", phone);
        return query.list();
    }

    @Override
    @Transactional
    public List<VipInfo> getWithPoints(int points) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM VipInfo WHERE points = :points order by id");
        query.setParameter("points", points);
        return query.list();
    }

    @Override
    @Transactional
    public List<VipInfo> dynamicSearch(List<String> wheres, VipInfo vipDTO) throws Exception {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<VipInfo> criteria = cb.createQuery(VipInfo.class);
        Root<VipInfo> root = criteria.from(VipInfo.class);
        criteria.select(root);

        Predicate p = null;
        for (String key : wheres) {
            if (p == null)
                p = cb.equal(root.get(key), getFromOb(key, vipDTO));
            else
                p = cb.and(p, cb.equal(root.get(key), getFromOb(key, vipDTO)));
        }
        criteria.where(p).orderBy(cb.asc(root.get("id")));

        return session.createQuery(criteria).getResultList();
    }

    private Object getFromOb(String where, VipInfo vipDTO) throws Exception {
        switch (where) {
            case "id":
                return vipDTO.getId();
            case "name":
                return vipDTO.getName();
            case "age":
                return vipDTO.getAge();
            case "sex":
                return vipDTO.getSex();
            case "regDate":
                return vipDTO.getRegDate();
            case "phone":
                return vipDTO.getPhone();
            case "points":
                return vipDTO.getPoints();
            default:
                throw new NoSuchElementException("column name not found: " + where);
        }
    }
}
