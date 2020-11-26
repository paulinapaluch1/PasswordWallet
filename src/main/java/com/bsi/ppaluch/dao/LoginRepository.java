package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.Login;
import com.bsi.ppaluch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoginRepository extends JpaRepository<Login, Integer> {

     List<Login> findAll();

    @Query("SELECT p FROM Login p "  + "WHERE p.id = :id order by p.dateTime desc")
    List<Login> findByUserId(int id);

    @Query("SELECT p FROM Login p "  + "WHERE p.user = :user order by p.dateTime desc")
    List<Login> findByUser(User user);


}
