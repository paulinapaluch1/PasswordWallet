package com.bsi.ppaluch.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="password")
@Data
public class Password {
    @Id
    @Column(name="id")
    private int id;

    @Column(name="password")
    private String password;

    @ManyToOne(cascade = { CascadeType.DETACH })
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name="web_address")
    private String web_address;

    @Column(name="description")
    private String description;

    @Column(name="login")
    private String login;

    @Transient
    private String master;

    public Password(String password, User user, String web_address, String description, String login) {
        this.password = password;
        this.user = user;
        this.web_address = web_address;
        this.description = description;
        this.login = login;
    }

    public Password() {

    }
}
