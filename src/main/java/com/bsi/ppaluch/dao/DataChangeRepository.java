package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.DataChange;
import com.bsi.ppaluch.entity.Function;
import com.bsi.ppaluch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataChangeRepository extends JpaRepository<DataChange, Integer> {

    @Query("SELECT p FROM DataChange p "  + "WHERE p.user = :user order by p.dateTime desc")
    List<DataChange> findByUser(User user);
}
