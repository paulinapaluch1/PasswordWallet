package com.bsi.ppaluch.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="password")
@Data
public class Password {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name="the_owner")
    private Boolean theOwner;

    @Column(name="deleted")
    private Boolean deleted;

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

    public Password(String web_address, String description,String login) {
        setWeb_address(web_address);
        setDescription(description);
        setLogin(login);
    }

    public boolean isOwner(User passwordUser){
        return theOwner && passwordUser.getId() == getUser().getId();
    }
}
