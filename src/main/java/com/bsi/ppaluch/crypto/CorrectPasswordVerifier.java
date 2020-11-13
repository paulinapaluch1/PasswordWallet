package com.bsi.ppaluch.crypto;

import com.bsi.ppaluch.entity.User;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import static com.bsi.ppaluch.crypto.CalculatorHmac.calculateHMAC;
import static com.bsi.ppaluch.crypto.CalculatorSHA.calculateSHA512;
import static com.bsi.ppaluch.crypto.Coder.PEPPER;

public class CorrectPasswordVerifier {

    public  boolean isTheSamePasswordSavedWithHmac(User oldUser, String oldPassword)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        return oldUser.getPassword_hash()
                .equals(calculateHMAC(oldPassword, oldUser.getSalt()));
    }

    public  boolean isTheSamePasswordSavedWithSHA(String givenPassword, User oldUser){
        return calculateSHA512(givenPassword + oldUser.getSalt() + PEPPER)
                .equals(oldUser.getPassword_hash());
    }
}
