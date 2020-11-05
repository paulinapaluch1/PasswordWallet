package com.bsi.ppaluch.crypto;

import com.bsi.ppaluch.entity.Password;
import com.bsi.ppaluch.entity.User;

import java.security.*;
import java.util.List;

import static com.bsi.ppaluch.crypto.AESenc.*;
import static com.bsi.ppaluch.crypto.CalculatorHmac.calculateHMAC;
import static com.bsi.ppaluch.crypto.CalculatorSHA.calculateSHA512;

public class Coder {

    public static String PEPPER = "12345anm8e3M-83*2cQ1mlZaU";

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        return bytes.toString();
    }

    public static String generatePasswordHash(String masterPassword, String salt){
        return calculateSHA512(masterPassword+salt+ PEPPER);
    }

    public static String encryptPassword(String masterPasswordHash, String passwordToSave) throws Exception {
        Key key = generateKey(masterPasswordHash);
        return encrypt(passwordToSave,key);
    }

    public static String decryptPassword(String masterPasswordHash, String encryptedPassword) throws Exception {
        Key key = generateKey(masterPasswordHash);
        return decrypt(encryptedPassword,key);
    }

    public static List<Password> encryptAllPasswords(List<Password> passwords, String newHash, User oldUser) throws Exception {
            for (Password p : passwords) {
                String pass = decryptPassword(oldUser.getPassword_hash(), p.getPassword());
                p.setPassword(encryptPassword(newHash, pass));
            }
        return passwords;
    }

    public static boolean isCorrectPassword(User oldUser, String oldPassword)
            throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if(oldUser.isPasswordKeptAsHash()) {
            return isTheSamePasswordSavedWithSHA(oldPassword,oldUser);

        }else{
            return isTheSamePasswordSavedWithHmac(oldUser, oldPassword);
        }
    }

    public static boolean isTheSamePasswordSavedWithHmac(User oldUser, String oldPassword)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        return oldUser.getPassword_hash()
                .equals(calculateHMAC(oldPassword, oldUser.getSalt()));
    }

    public static boolean isTheSamePasswordSavedWithSHA(String givenPassword, User oldUser){
        return calculateSHA512(givenPassword + oldUser.getSalt() + PEPPER)
                .equals(oldUser.getPassword_hash());
    }

}
