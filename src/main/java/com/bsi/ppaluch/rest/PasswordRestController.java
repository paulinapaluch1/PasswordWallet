package com.bsi.ppaluch.rest;

import com.bsi.ppaluch.PasswordChanger;
import com.bsi.ppaluch.dao.PasswordRepository;
import com.bsi.ppaluch.dao.UserRepository;
import com.bsi.ppaluch.entity.Password;
import com.bsi.ppaluch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.List;

import static com.bsi.ppaluch.crypto.AESenc.*;
import static com.bsi.ppaluch.crypto.CalculatorHmac.calculateHMAC;
import static com.bsi.ppaluch.crypto.Coder.*;

@Controller
@RequestMapping("/api")

public class PasswordRestController {
    private PasswordRepository passwordRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public PasswordRestController(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    @GetMapping("/passwords")
    public String getAllPasswords(Model theModel)   {
        theModel.addAttribute("passwords", passwordRepository.findAll());
        return "passwords";
    }

    @RequestMapping(value = "/passwords/change-master", method = RequestMethod.GET)
    public String changeMasterPassword(Model theModel)   {
        PasswordChanger changer =  new PasswordChanger();
        theModel.addAttribute("changer", changer);
        return "changeMaster";
    }


    @RequestMapping(value = "/passwords/showFormForAdd", method = RequestMethod.GET)
    public String showFormForAddPassword(Model theModel)   {
        theModel.addAttribute("pass", new Password());
        theModel.addAttribute("web_address", "");
        theModel.addAttribute("action", "add");
        theModel.addAttribute("passwords", passwordRepository.findAll());
        return "addPassword";
    }


    @RequestMapping(value = "/passwords/save", method = RequestMethod.POST)
    public String savePassword(@ModelAttribute("pass") Password pass, @RequestParam("action") String action,
                              Model theModel) throws Exception {
            User user = userRepository.findById(8);

           Key key = generateKey(user.getPassword_hash());
           pass.setPassword(encrypt(pass.getPassword(), key));
           pass.setUser(user);
           passwordRepository.save(pass);
           return getAllPasswords(theModel);
    }

    @GetMapping("/passwords/encodePassword")
    public String encodePassword(@RequestParam("id") Integer id, Model theModel) throws Exception {
        Password pass = passwordRepository.findByPasswordId(id);
        User user = userRepository.findById(8);
        String password = decrypt(pass.getPassword(),generateKey(user.getPassword_hash()));
        theModel.addAttribute(pass);
        theModel.addAttribute("passwordText", password);
        theModel.addAttribute("passwords", passwordRepository.findAll());

        return "/passwords";
    }

    @RequestMapping(value = "/password/change", method = RequestMethod.POST)
    public String changeMaster(@ModelAttribute("changer") PasswordChanger changer, Model theModel) throws Exception {
        User oldUser = userRepository.findById(8);
        if (isCorrectPassword(oldUser, changer.getOldPassword())) {
            if(changer.isKeepPaswordAsHash()) {
                changeSHAPasswordMaster(changer, oldUser);
            }
           else {
                changeHmacPasswordMaster(changer, oldUser);
            }
        } else {
                theModel.addAttribute("info", "Niepoprawne obecne haslo");
                return changeMasterPassword(theModel);

        }
        return getAllPasswords(theModel);

    }

    private void changeHmacPasswordMaster(PasswordChanger changer, User oldUser) throws Exception {
        String newSalt = generateSalt();
        String newHmac = calculateHMAC(changer.getNewPassword(), newSalt);
        List<Password> currentPasswordList
                = encryptAllPasswords(passwordRepository.findByUser(oldUser), newHmac, oldUser);
        oldUser.setPasswordKeptAsHash(false);
        oldUser.setSalt(newSalt);
        oldUser.setPassword_hash(newHmac);
        userRepository.save(oldUser);
        passwordRepository.saveAll(currentPasswordList);
    }

    private void changeSHAPasswordMaster(PasswordChanger changer, User oldUser) throws Exception {
        String newSalt = generateSalt();
        String newHash = generatePasswordHash(changer.getNewPassword(), newSalt);
        List<Password> currentPasswordList
                = encryptAllPasswords(passwordRepository.findByUser(oldUser), newHash, oldUser);
        oldUser.setPasswordKeptAsHash(true);
        oldUser.setSalt(newSalt);
        oldUser.setPassword_hash(newHash);
        userRepository.save(oldUser);
        passwordRepository.saveAll(currentPasswordList);
    }


    @GetMapping("/passwords/deletePassword")
    public String deletePassword(@RequestParam("id") Integer id, Model theModel) throws Exception {
        passwordRepository.deleteById(id);
        theModel.addAttribute("passwords", passwordRepository.findAll());
        return "passwords";
    }




}
