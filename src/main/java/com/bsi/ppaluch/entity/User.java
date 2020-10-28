package com.bsi.ppaluch.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="user")
@Data
public class User {

   @Id
   @Column(name="id")
   private int id;

   @Column(name="login")
   private String login;

   @Column(name="password_hash")
   private String password_hash;

   @Column(name="salt")
   private String salt;

   @Column(name="is_password_kept_as_hash")
   private boolean isPasswordKeptAsHash;

   @OneToMany(mappedBy = "id")
   List<Password> passwordList;
   }
