package com.bsi.ppaluch.login;

import lombok.Data;

@Data
public class PasswordChanger {
    private String oldPassword;
    private String newPassword;
    private boolean keepPaswordAsHash;
}
