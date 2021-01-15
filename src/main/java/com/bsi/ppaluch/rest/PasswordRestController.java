package com.bsi.ppaluch.rest;

import com.bsi.ppaluch.ActionRegisterer;
import com.bsi.ppaluch.Mode;
import com.bsi.ppaluch.PasswordSharer;
import com.bsi.ppaluch.login.PasswordChanger;
import com.bsi.ppaluch.crypto.Coder;
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

import static com.bsi.ppaluch.CurrentLoggedUser.*;
import static com.bsi.ppaluch.CurrentMode.getMode;
import static com.bsi.ppaluch.CurrentMode.setMode;
import static com.bsi.ppaluch.crypto.AESenc.*;
import static com.bsi.ppaluch.crypto.CalculatorHmac.calculateHMAC;
import static com.bsi.ppaluch.crypto.Coder.*;

@Controller
@RequestMapping("/api")
public class PasswordRestController {
    private PasswordRepository passwordRepository;
    @Autowired
    private UserRepository userRepository;
    Coder coder;
    @Autowired
    ActionRegisterer actionRegisterer;

    public PasswordRestController(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
        coder = new Coder();
    }

    @GetMapping("/passwords")
    public String getAllPasswords(Model theModel)   {
        theModel.addAttribute("passwords", passwordRepository.findByUser(getUser()));
        theModel.addAttribute("userId",Integer.valueOf(getUser().getId()));
        return "passwords";
    }

    @RequestMapping(value = "/passwords/change-master", method = RequestMethod.GET)
    public String changeMasterPassword(Model theModel)   {
        if(Mode.MODIFY.equals(getMode())) {
            PasswordChanger changer = new PasswordChanger();
            theModel.addAttribute("changer", changer);
            return "changeMaster";

        }else{
            theModel.addAttribute("info", "You are in READ mode. Change to MODIFY.");
            return getAllPasswords(theModel);
        }
    }


    @RequestMapping(value = "/passwords/showFormForAdd", method = RequestMethod.GET)
    public String showFormForAddPassword(Model theModel)   {
        if(Mode.MODIFY.equals(getMode())) {
            theModel.addAttribute("pass", new Password());
            theModel.addAttribute("web_address", "");
            theModel.addAttribute("action", "add");
            theModel.addAttribute("passwords", passwordRepository.findByUser(getUser()));
            return "addPassword";
        }else{
            theModel.addAttribute("info", "You are in READ mode. Change to MODIFY.");
            return getAllPasswords(theModel);
        }
    }

    @RequestMapping(value = "/passwords/editPassword", method = RequestMethod.GET)
    public String showFormForEditPassword(@RequestParam("id") Integer id, Model theModel)   {
        if(Mode.MODIFY.equals(getMode())) {
            theModel.addAttribute("pass", passwordRepository.findByPasswordId(id));
            theModel.addAttribute("web_address", "");
            theModel.addAttribute("action", "add");
            theModel.addAttribute("passwords", passwordRepository.findByUser(getUser()));
            return "editPassword";
        }else{
            theModel.addAttribute("info", "You are in READ mode. Change to MODIFY.");
            return getAllPasswords(theModel);
        }
    }

    @RequestMapping(value = "/passwords/saveEdited", method = RequestMethod.POST)
    public String saveEditedPassword(@ModelAttribute("pass") Password pass,
                               Model theModel) throws Exception {
        User user = getUser();
        Key key = generateKey(user.getPassword_hash());
        if(pass!=null) {
            pass.setPassword(encrypt(pass.getPassword(), key));
        }
        pass.setUser(user);
        pass.setTheOwner(true);
        pass.setDeleted(false);
        actionRegisterer.registerPasswordDataChange(passwordRepository.findByPasswordId(pass.getId()), pass,"save_edited",pass.getId());
        passwordRepository.save(pass);
        actionRegisterer.registerAction("save_edited_password");

        return getAllPasswords(theModel);
    }

    @RequestMapping(value = "/passwords/save", method = RequestMethod.POST)
    public String savePassword(@ModelAttribute("pass") Password pass,
                              Model theModel) throws Exception {
           User user = getUser();
           Key key = generateKey(user.getPassword_hash());
           pass.setPassword(encrypt(pass.getPassword(), key));
           pass.setUser(user);
           pass.setTheOwner(true);
           pass.setDeleted(false);
           passwordRepository.save(pass);
        actionRegisterer.registerPasswordDataChange(null, pass,"add",pass.getId());

        actionRegisterer.registerAction("add_password");


        return getAllPasswords(theModel);
    }

    @GetMapping("/passwords/encodePassword")
    public String encodePassword(@RequestParam("id") Integer id, Model theModel) throws Exception {
        if(Mode.MODIFY.equals(getMode())) {
            Password pass = passwordRepository.findByPasswordId(id);
            String password = decrypt(pass.getPassword(), generateKey(getUser().getPassword_hash()));
            theModel.addAttribute(pass);
            theModel.addAttribute("passwordText", password);
            theModel.addAttribute("passwords", passwordRepository.findByUser(getUser()));
            PasswordSharer sharer = new PasswordSharer(id);
            theModel.addAttribute("sharer", sharer);
            actionRegisterer.registerAction("encode_password");


        }else{
            theModel.addAttribute("info", "You are in READ mode. Change to MODIFY.");
        }
        return getAllPasswords(theModel);
    }

    @GetMapping("/passwords/recover")
    public String recoverPassword(@RequestParam("id") Integer id, Model theModel) throws Exception {
           actionRegisterer.recoverDataChange(id);
        return getAllPasswords(theModel);
    }

    @RequestMapping(value = "/password/change", method = RequestMethod.POST)
    public String changeMaster(@ModelAttribute("changer") PasswordChanger changer, Model theModel) throws Exception {
        User oldUser = getUser();
        if (coder.isCorrectPassword(oldUser, changer.getOldPassword())) {
            if(changer.isKeepPaswordAsHash()) {
                changeSHAPasswordMaster(changer, oldUser);
            }
           else {
                changeHmacPasswordMaster(changer, oldUser);
           }
            actionRegisterer.registerAction("change_master");

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
                = coder.encryptAllPasswords(passwordRepository.findByUser(oldUser), newHmac, oldUser);
        oldUser.setPasswordKeptAsHash(false);
        oldUser.setSalt(newSalt);
        oldUser.setPassword_hash(newHmac);
        userRepository.save(oldUser);
        setUser(oldUser);
        passwordRepository.saveAll(currentPasswordList);
    }

    private void changeSHAPasswordMaster(PasswordChanger changer, User oldUser) throws Exception {
        String newSalt = generateSalt();
        String newHash = coder.generatePasswordHash(changer.getNewPassword(), newSalt);
        List<Password> currentPasswordList
                = coder.encryptAllPasswords(passwordRepository.findByUser(oldUser), newHash, oldUser);
        oldUser.setPasswordKeptAsHash(true);
        oldUser.setSalt(newSalt);
        oldUser.setPassword_hash(newHash);
        userRepository.save(oldUser);
        setUser(oldUser);
        passwordRepository.saveAll(currentPasswordList);
    }


    @GetMapping("/passwords/deletePassword")
    public String deletePassword(@RequestParam("id") Integer id, Model theModel) throws Exception {
        if(Mode.MODIFY.equals(getMode())) {
            Password password = passwordRepository.findByPasswordId(id);
            if (password.getTheOwner()) {
                password.setDeleted(true);
                passwordRepository.save(password);
                actionRegisterer.registerAction("delete_password");
                actionRegisterer.registerPasswordDataChange(password, null,"delete",password.getId());

            } else {
                theModel.addAttribute("reason", "You are not the owner");

            }
        }else{
                theModel.addAttribute("info", "You are in READ mode. Change to MODIFY.");

            }
        theModel.addAttribute("passwords", passwordRepository.findByUser(getUser()));
        return "passwords";
    }

    @RequestMapping(value = "/passwords/share", method = RequestMethod.POST)
    public String sharePassword(@ModelAttribute("sharer") PasswordSharer sharer, Model theModel) throws Exception {
        List<User> list = userRepository.findByEmail(sharer.getEmail());
        Password password = passwordRepository.findByPasswordId(sharer.getPasswordId());
        if(getUser().getEmail().equals(sharer.getEmail())){
            theModel.addAttribute("reason", "You typed your own email adress");
            theModel.addAttribute("shared", false);
            return getAllPasswords(theModel);
        }
        if (password.getTheOwner()) {
            if(!list.isEmpty()) {
                sharePasswordWithUsers(list, password);
                theModel.addAttribute("shared", true);
            }else{
                theModel.addAttribute("reason", "There is no user with this email in database");
                theModel.addAttribute("shared", false);
            }
        } else {
            theModel.addAttribute("reason", "You are not the owner");
            theModel.addAttribute("shared", false);

        }
            return getAllPasswords(theModel);
    }

    private void sharePasswordWithUsers(List<User> list, Password password) throws Exception {
        String passwordText = decrypt(password.getPassword(), generateKey(getUser().getPassword_hash()));
        Password newPassword = new Password(password.getWeb_address(), password.getDescription(), password.getLogin());
        for (User user : list) {
            passwordRepository.save(prepareSharedPasswordToSave(passwordText, newPassword, user));
        }
    }

    private Password prepareSharedPasswordToSave(String passwordText, Password newPassword, User user) throws Exception {
        Key key = generateKey(user.getPassword_hash());
        newPassword.setPassword(encrypt(passwordText, key));
        newPassword.setUser(user);
        newPassword.setTheOwner(false);
        return newPassword;
    }

    @GetMapping("/readMode")
    public String changeModeToRead(Model theModel) {
        setMode(Mode.READ);
        theModel.addAttribute("info", "You are in READ MODE");
        actionRegisterer.registerAction("change_mode_read");
        return getAllPasswords(theModel);
    }

    @GetMapping("/modifyMode")
    public String changeModeToModify(Model theModel) {
        setMode(Mode.MODIFY);
        theModel.addAttribute("info", "You are in MODIFY MODE");
        actionRegisterer.registerAction("change_mode_modify");
        return getAllPasswords(theModel);
    }
}
