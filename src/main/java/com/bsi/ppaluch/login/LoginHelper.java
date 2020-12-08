package com.bsi.ppaluch.login;


public class LoginHelper {

    public int getLoginVerificationTimeInSeconds(int loginTrialAmount) {
        if (loginTrialAmount == 2) {
            return 5;
        } else if (loginTrialAmount == 3) {
            return 10;
        } else if (loginTrialAmount >= 4) {
            return 120;
        } else return 0;
    }


    public boolean isIPBlocked(int trialNumber) {
        return trialNumber >= 4 ? true : false;
    }

    public int getIPVerificationTimeInSeconds(int trialNumber) {
        if (isIPBlocked(trialNumber)) {
            return -1;
        } else if (trialNumber == 2) {
            return 5;
        } else if (trialNumber == 3) {
            return 10;
        } else return 0;
    }


    public int getVerificationTime(int loginTrials, int ipTrials) {
        int loginTrialsTime = getLoginVerificationTimeInSeconds(loginTrials);
        int ipTrialsTime = getIPVerificationTimeInSeconds(ipTrials);
        return loginTrials >= ipTrials ? loginTrialsTime : ipTrialsTime;
    }


}