package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionTypeRepository extends JpaRepository<ActionType, Integer> {

    ActionType findByTitle(String action);
}
