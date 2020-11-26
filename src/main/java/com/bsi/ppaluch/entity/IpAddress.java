package com.bsi.ppaluch.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="ip_address")
@Data
public class IpAddress {

    @Id
    @Column(name="id")
    private int id;

    @Column(name="ip_address")
    private String ipAddressText;

    @Column(name="incorrect_login_trial_number")
    private int incorrectLoginTrialNumber;

    @OneToMany(mappedBy = "id")
    List<Login> loginList;

    public IpAddress(String ipAddressText, int incorrectLoginTrialNumber) {
        this.ipAddressText = ipAddressText;
        this.incorrectLoginTrialNumber = incorrectLoginTrialNumber;
    }

    public IpAddress() {
    }
}
