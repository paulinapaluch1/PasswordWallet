package com.bsi.ppaluch.dao;


import com.bsi.ppaluch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {

    List<User> findAll();

    User findById(int id);

    User findByLogin(String login);

    List<User> findByEmail(String email);
}
