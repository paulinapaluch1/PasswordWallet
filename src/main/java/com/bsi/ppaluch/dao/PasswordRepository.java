package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.Password;
import com.bsi.ppaluch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Integer> {

     List<Password> findAll();

    @Query("SELECT p FROM Password p "  + "WHERE p.id = :id")
    Password findByPasswordId(int id);

    @Query("SELECT p FROM Password p "  + "WHERE p.user = :user and p.deleted =0")
    List<Password> findByUser(User user);


    List<Password> findByUserAndDeleted(User user, boolean deleted);
}
