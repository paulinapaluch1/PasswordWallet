package com.bsi.ppaluch.rest;

import com.bsi.ppaluch.ActionRegisterer;
import com.bsi.ppaluch.CurrentLoggedUser;
import com.bsi.ppaluch.CurrentMode;
import com.bsi.ppaluch.Mode;
import com.bsi.ppaluch.crypto.Coder;
import com.bsi.ppaluch.dao.*;
import com.bsi.ppaluch.entity.*;
import com.bsi.ppaluch.login.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.bsi.ppaluch.CurrentLoggedUser.*;
import static com.bsi.ppaluch.CurrentMode.getMode;


@Controller
@RequestMapping("/api")

public class UserRestController {
    private static final String ALGO = "AES";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRepository passwordRepository;
    @Autowired
    private IpAddressRepository ipRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private FunctionRepository functionRepository;
    @Autowired
    private FunctionRunRepository functionRunRepository;
    @Autowired
    private ActionRegisterer actionRegisterer;
    @Autowired
    private DataChangeRepository dataChangeRepository;

    private Coder coder;

    private LoginHelper loginHelper;

    @Autowired
    public UserRestController() {
        coder = new Coder();
        loginHelper = new LoginHelper();
    }

    @GetMapping("/login")
    public String login(Model theModel) {
        theModel.addAttribute("user", new User());
        return "login";
    }

    @RequestMapping(value="/signin", method=RequestMethod.POST)
    public String validateForm(User user, BindingResult result, Model theModel, HttpServletRequest request) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, InterruptedException {
        String ip = getClientIp(request);
        IpAddress  ipAddress = getIPAddress(ip);
        User userDb = userRepository.findByLogin(user.getLogin());

        if(loginHelper.isIPBlocked(ipAddress.getIncorrectLoginTrialNumber())) {
            registerNewLogin(false,userDb,ip);
            increaseLoginTrialForIP(ipAddress);
            theModel.addAttribute("user", userDb);
            theModel.addAttribute("info", "Twój adres IP został zablokowany.");
            return "/login";
        }else{
        TimeUnit.SECONDS.sleep(getVerificationTime(userDb,ipAddress));
        if(coder.isCorrectPassword(userDb, user.getPassword_hash())){
            resetUserIncorrectLoginTrialNumber(userDb);
            resetIPIncorrectTrialNumber(ipAddress);
            theModel.addAttribute("passwords", passwordRepository.findByUser(userDb));
            theModel.addAttribute("user", userDb);
            registerNewLogin(true,userDb,ip);
            CurrentLoggedUser.setUser(userDb);
            CurrentMode.setMode(Mode.READ);
            actionRegisterer.registerAction("show_passwords");
            return "/passwords";
        }else{
            registerNewLogin(false,userDb,ip);
            theModel.addAttribute("user", userDb);
            setIncorrectLoginTrialNumber(userDb, userDb.getIncorrectLoginTrialNumber() + 1);
            if(userDb.getIncorrectLoginTrialNumber() > 1) {
                theModel.addAttribute("info", "Niepoprawny login lub hasło. Czas następnej weryfikacji zostanie zwiększony do "
                        + getVerificationTime(userDb,ipAddress) + " sekund.");
            }else{
                theModel.addAttribute("info", "Niepoprawny login lub hasło.");
            }
            return "/login";
        }

        }

    }




    private int getVerificationTime(User userDb,IpAddress ip) {
        return loginHelper.getVerificationTime(userDb.getIncorrectLoginTrialNumber(), ip.getIncorrectLoginTrialNumber());
    }

    private void registerNewLogin(boolean result, User userDb,String ip){
        Login login = new Login(userDb,new Date(),result, ipRepository.findByIpAddressText(ip));
        loginRepository.save(login);
    }
    private void increaseLoginTrialForIP(IpAddress ip) {
        ip.setIncorrectLoginTrialNumber(ip.getIncorrectLoginTrialNumber()+1);
        ipRepository.save(ip);

    }

    private static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    private IpAddress getIPAddress(String ipAddres) {
        IpAddress ip = ipRepository.findByIpAddressText(ipAddres);
        if(ip != null) {
            return ip;
        }else ip = new IpAddress(ipAddres, 0);
        ipRepository.save(ip);
        return ip;
    }

    private void setIncorrectLoginTrialNumber(User userDb, int i) {
        userDb.setIncorrectLoginTrialNumber(i);
        userRepository.save(userDb);
    }

    private void resetUserIncorrectLoginTrialNumber(User userDb) {
        userDb.setIncorrectLoginTrialNumber(0);
        userRepository.save(userDb);
    }

    private void resetIPIncorrectTrialNumber(IpAddress ipAddress) {
        ipAddress.setIncorrectLoginTrialNumber(0);
        ipRepository.save(ipAddress);
    }

    @GetMapping("/logins")
    public String getUserLastLogins( Model theModel) {
        List<Login> loginList = loginRepository.findByUser(getUser());
        theModel.addAttribute("id",getUser().getId());
        theModel.addAttribute("successfullLoginDate",findLastSuccessfulLogin(loginList));
        theModel.addAttribute("failedLoginDate",findLastFailedLogin(loginList) );
        List<IpAddress> ips = loginList
               .stream()
               .filter(d->d.getIpAddress().getIncorrectLoginTrialNumber() >= 4)
               .map(d -> d.getIpAddress())
               .distinct()
               .collect(Collectors.toList());
        theModel.addAttribute("blockedIPs", ips);
        return "logins";
    }

    @GetMapping("/actions")
    public String getUserLastActions( Model theModel) {
        List<FunctionRun> functionRunList = functionRunRepository.findFunctionsRunForUser(getUser());
        theModel.addAttribute("actions", functionRunList);
        return "actions";
    }

    @GetMapping("/changes")
    public String getUserLastChanges( Model theModel) {
        List<DataChange> changesList = dataChangeRepository.findByUser(getUser());
        theModel.addAttribute("changes", changesList);

        return "changes";
    }

    private Date findLastFailedLogin(List<Login> loginList) {
        Login login = loginList
                .stream()
                .filter(d -> !d.isResult())
                .findFirst().orElse(null);
        return login != null && login.getDateTime() != null ?
                login.getDateTime() : null;

    }

    private Date findLastSuccessfulLogin(List<Login> loginList) {
       Login login =  loginList
                .stream()
                .filter(d -> d.isResult())
                .findFirst().orElse(null);
     return login != null && login.getDateTime() != null ?
             login.getDateTime() : null;

    }


    @GetMapping("/logout")
    public String logout(Model theModel) {
        return login(theModel);
    }

    @GetMapping("/users/unlock")
    public String unlockIP(@RequestParam("id") Integer id, @RequestParam("userId") Integer userId, Model theModel) throws Exception {
        if(Mode.MODIFY.equals(getMode())) {
            IpAddress ip = ipRepository.findById(id).get();
            ip.setIncorrectLoginTrialNumber(0);
            ipRepository.save(ip);
        }
        else{
            theModel.addAttribute("info", "You are in READ mode. Change to MODIFY.");
        }
        return getUserLastLogins(theModel);
    }



}
