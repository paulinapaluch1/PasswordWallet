package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class UserDaoHibernateImpl  {

    private EntityManager entityManager;

    @Autowired
    public UserDaoHibernateImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public List<User> findAll() {
        Session currentSession= entityManager.unwrap(Session.class);
        Query<User> theQuery = currentSession.createQuery("from User", User.class);
        List<User> users = theQuery.getResultList();

        return users;
    }


    public void save(User user) {

        entityManager.merge(user);
        user.setId(user.getId());
    }

    public User findById(int id) {
        User user = entityManager.find(User.class,id);
        return user;
    }

    public void deleteById(int id) {
        Query query = (Query) entityManager
                .createQuery("delete from User where id=:id");
        query.setParameter("id",id);
        query.executeUpdate();
    }


}
