package com.bsi.ppaluch.rest;

import com.bsi.ppaluch.crypto.Coder;
import com.bsi.ppaluch.dao.IpAddressRepository;
import com.bsi.ppaluch.dao.LoginRepository;
import com.bsi.ppaluch.dao.PasswordRepository;
import com.bsi.ppaluch.dao.UserRepository;
import com.bsi.ppaluch.entity.IpAddress;
import com.bsi.ppaluch.entity.Login;
import com.bsi.ppaluch.entity.Password;
import com.bsi.ppaluch.entity.User;
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
    LoginRepository loginRepository;
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
    public String getUserLastLogins(@RequestParam("id") int id, Model theModel) {
        User user = userRepository.findById(id);
        List<Login> loginList = loginRepository.findByUser(user);
        theModel.addAttribute("id",id);
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

    private Date findLastFailedLogin(List<Login> loginList) {
        return loginList
                .stream()
                .filter(d -> !d.isResult())
                .findFirst().get()
                .getDateTime();
    }

    private Date findLastSuccessfulLogin(List<Login> loginList) {
        return loginList
                .stream()
                .filter(d -> d.isResult())
                .findFirst()
                .get()
                .getDateTime();
    }


    @GetMapping("/logout")
    public String logout(Model theModel) {
        return login(theModel);
    }

    @GetMapping("/users/unlock")
    public String unlockIP(@RequestParam("id") Integer id, @RequestParam("userId") Integer userId, Model theModel) throws Exception {
        IpAddress ip = ipRepository.findById(id).get();
        ip.setIncorrectLoginTrialNumber(0);
        ipRepository.save(ip);

        return getUserLastLogins(userId, theModel);
    }
}
