package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FunctionRepository extends JpaRepository<Function, Integer> {

    @Query("SELECT p FROM Function p "  + "WHERE p.name = :name")
    Function findByName(String name);

    @Query("SELECT p FROM Function p "  + "WHERE p.id = :id")
    Function findByFunctionId(int id);
}
