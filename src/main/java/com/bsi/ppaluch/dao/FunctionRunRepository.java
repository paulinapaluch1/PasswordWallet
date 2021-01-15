package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.FunctionRun;
import com.bsi.ppaluch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FunctionRunRepository extends JpaRepository<FunctionRun, Integer> {

    @Query("SELECT p FROM FunctionRun p "  + "WHERE p.user = :user order by p.dateTime desc")
    List<FunctionRun> findFunctionsRunForUser(User user);

}
