package com.bsi.ppaluch.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="function_run")
@Data
public class FunctionRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = {CascadeType.DETACH})
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dateTime;

    @ManyToOne(cascade = {CascadeType.DETACH})
    @JoinColumn(name = "id_function")
    private Function function;
}