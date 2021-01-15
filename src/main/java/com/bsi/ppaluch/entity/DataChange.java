package com.bsi.ppaluch.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="data_change")
@Data
public class DataChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = {CascadeType.DETACH})
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dateTime;

    @Column(name="id_modified_record")
    private int idModifiedRecord;

    private String previousRecordValue;

    private String presentRecordValue;

    @ManyToOne(cascade = {CascadeType.DETACH})
    @JoinColumn(name = "action_type_id")
    private ActionType actionType;

    @ManyToOne(cascade = {CascadeType.DETACH})
    @JoinColumn(name = "table_name_id")
    private TableName tableName;

}
