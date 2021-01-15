package com.bsi.ppaluch.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="action_type")
@Data
public class ActionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    private String title;

    private String description;

    @OneToMany(mappedBy = "id")
    List<DataChange> dataChangeList;

}
