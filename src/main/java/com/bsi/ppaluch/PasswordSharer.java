package com.bsi.ppaluch;

import lombok.Data;

@Data
public class PasswordSharer {
    private String email;
    private Integer passwordId;

    public PasswordSharer(Integer passwordId) {
        this.passwordId = passwordId;
    }
}
