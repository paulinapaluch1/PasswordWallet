package com.bsi.ppaluch.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="login")
@Data
public class Login {
    @Id
    @Column(name="id")
    private int id;

    @ManyToOne(cascade = { CascadeType.DETACH })
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dateTime;

    @Column(name="result")
    private boolean result;

    @ManyToOne(cascade = { CascadeType.DETACH })
    @JoinColumn(name = "ip_address_id")
    private IpAddress ipAddress;

    public Login(User user, Date dateTime, boolean result, IpAddress ipAddress) {
        this.user = user;
        this.dateTime = dateTime;
        this.result = result;
        this.ipAddress = ipAddress;
    }

    public Login() {
    }
}
