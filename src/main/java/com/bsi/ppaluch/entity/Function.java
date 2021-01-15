package com.bsi.ppaluch.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="functions")
@Data
public class Function {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="function_name")
    private String name;

    private String description;

    @OneToMany(mappedBy = "id")
    List<FunctionRun> functionRunList;
}