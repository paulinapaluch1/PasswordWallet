package com.bsi.ppaluch.crypto;

import com.bsi.ppaluch.entity.User;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;



public class CorrectPasswordVerifierAdapter extends CorrectPasswordVerifier{

    public  boolean isTheSamePasswordSavedWithHmac(User oldUser, String oldPassword)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
       return true;
    }

    public  boolean isTheSamePasswordSavedWithSHA(String givenPassword, User oldUser){
        return true;
    }
}
