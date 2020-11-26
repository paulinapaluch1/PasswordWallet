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

   @Column(name="incorrect_login_trial_number")
   private int incorrectLoginTrialNumber;

   @OneToMany(mappedBy = "id")
   List<Password> passwordList;

   public User(String login, String salt, String passwordHash, boolean isPasswordKeptAsHash) {
      this.login = login;
      this.salt = salt;
      this.password_hash = passwordHash;
      this.isPasswordKeptAsHash = isPasswordKeptAsHash;
   }

   public User() {
   }
}
