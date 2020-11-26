package com.bsi.ppaluch.crypto;

import com.bsi.ppaluch.entity.Password;
import com.bsi.ppaluch.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.*;
import java.util.List;

import static com.bsi.ppaluch.crypto.AESenc.*;
import static com.bsi.ppaluch.crypto.CalculatorSHA.calculateSHA512;

@Component
public class Coder {

    private CorrectPasswordVerifier verifier;

    public static String PEPPER = "12345anm8e3M-83*2cQ1mlZaU";

    public Coder(CorrectPasswordVerifier verifier) {
        this.verifier = verifier;
    }

    public Coder() {
        this.verifier = new CorrectPasswordVerifier();
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return bytes.toString();
    }

    public  String generatePasswordHash(String masterPassword, String salt){
        return calculateSHA512(masterPassword+salt+ PEPPER);
    }

    public  String encryptPassword(String masterPasswordHash, String passwordToSave) throws Exception {
        Key key = generateKey(masterPasswordHash);
        return encrypt(passwordToSave,key);
    }

    public  String decryptPassword(String masterPasswordHash, String encryptedPassword) throws Exception {
        Key key = generateKey(masterPasswordHash);
        return decrypt(encryptedPassword,key);
    }

    public  List<Password> encryptAllPasswords(List<Password> passwords, String newHash, User oldUser) throws Exception {
            for (Password p : passwords) {
                String pass = decryptPassword(oldUser.getPassword_hash(), p.getPassword());
                p.setPassword(encryptPassword(newHash, pass));
            }
        return passwords;
    }

    public  boolean isCorrectPassword(User oldUser, String oldPassword)
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if(oldUser.isPasswordKeptAsHash()) {
            return verifier.isTheSamePasswordSavedWithSHA(oldPassword,oldUser);

        }else{
            return verifier.isTheSamePasswordSavedWithHmac(oldUser, oldPassword);
        }
    }

    public CorrectPasswordVerifier getVerifier() {
        return verifier;
    }
}
