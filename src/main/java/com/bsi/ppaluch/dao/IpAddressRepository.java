package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.IpAddress;
import com.bsi.ppaluch.entity.Login;
import com.bsi.ppaluch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IpAddressRepository extends JpaRepository<IpAddress, Integer> {

     List<IpAddress> findAll();

    @Query("SELECT p FROM IpAddress p "  + "WHERE p.id = :id")
    IpAddress findById(int id);

    @Query("SELECT p FROM IpAddress p "  + "WHERE p.ipAddressText = :ip")
    IpAddress findByIpAddressText(String ip);
}
