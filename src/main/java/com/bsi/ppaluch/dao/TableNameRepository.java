package com.bsi.ppaluch.dao;

import com.bsi.ppaluch.entity.DataChange;
import com.bsi.ppaluch.entity.TableName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableNameRepository extends JpaRepository<TableName, Integer> {

}
