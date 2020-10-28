package com.bsi.ppaluch.rest;

import com.bsi.ppaluch.dao.PasswordRepository;
import com.bsi.ppaluch.dao.UserRepository;
import com.bsi.ppaluch.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import static com.bsi.ppaluch.crypto.Coder.isCorrectPassword;

@Controller
@RequestMapping("/api")

public class UserRestController {
    private static final String ALGO = "AES";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRepository passwordRepository;

    @Autowired
    public UserRestController() {}

    @GetMapping("/login")
    public String login(Model theModel) {
        theModel.addAttribute("user", new User());
        return "login";
    }

    @RequestMapping(value="/signin", method=RequestMethod.POST)
    public String validateForm(User user, BindingResult result, Model theModel) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
       User userDb = userRepository.findByLogin(user.getLogin());
        if(isCorrectPassword(userDb, user.getPassword_hash())){
            theModel.addAttribute("passwords", passwordRepository.findByUser(userDb));
            theModel.addAttribute("user", userDb);
            return "/passwords";
        }else{
            theModel.addAttribute("user", userDb);
            theModel.addAttribute("info", "Niepoprawny login lub hasło");
            return "/login";
        }
    }

    @GetMapping("/logout")
    public String logout(Model theModel) {
        return login(theModel);
    }



}
