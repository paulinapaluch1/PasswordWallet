package com.bsi.ppaluch.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="table_name")
@Data
public class TableName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "id")
    List<DataChange> dataChangeList;

}
