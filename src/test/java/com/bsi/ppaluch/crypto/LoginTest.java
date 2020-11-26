package com.bsi.ppaluch.crypto;

import com.bsi.ppaluch.login.LoginHelper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class LoginTest {

    @DataProvider(name = "shouldGetCorrectLoginVerificationTimeInSeconds_dataProvider")
    public static Object[][] getDataToGetCorrectLoginVerificationTime() {
        return new Object[][]{{1, 0}, {2, 5}, {3, 10}, {4, 120}, {5, 120}, {10, 120}};
    }

    @Test(dataProvider = "shouldGetCorrectLoginVerificationTimeInSeconds_dataProvider")
    public void shouldGetCorrectLoginVerificationTimeInSeconds(int trialNumber, int expResult) {
        System.out.println("shouldGetCorrectLoginVerificationTimeInSeconds");
        LoginHelper helper = new LoginHelper();
        int result = helper.getLoginVerificationTimeInSeconds(trialNumber);
        assertEquals(result, expResult);
    }
    
    @DataProvider(name = "isUserBlocked_FailedLoginTrialMoreThan4_returnTrue_dataProvider")
    public static Object[][] getDataToFailedLoginTrialMoreThan4() {
        return new Object[][]{{5, true}, {5, true}, {10, true}, {13, true}, {6, true}, {9, true}};
    }

    @Test(dataProvider = "isUserBlocked_FailedLoginTrialMoreThan4_returnTrue_dataProvider")
    public void isIPBlocked_FailedLoginTrialMoreThan4_returnTrue(int trialNumber, boolean expResult){
        System.out.println("isIPBlocked_FailedLoginTrialMoreThan4_returnTrue");
        LoginHelper helper = new LoginHelper();
        boolean result = helper.isIPBlocked(trialNumber);
        assertEquals(result, expResult);
    }

    @DataProvider(name = "isIPBlocked_FailedLoginTrialLessThan4_returnFalse_dataProvider")
    public static Object[][] getDataToFailedLoginTrialLessThan4() {
        return new Object[][]{{1, false}, {0, false}, {2, false}, {3, false}};
    }

    @Test(dataProvider = "isIPBlocked_FailedLoginTrialLessThan4_returnFalse_dataProvider")
    public void isIPBlocked_FailedLoginTrialLessThan4_returnFalse(int trialNumber, boolean expResult){
        System.out.println("isIPBlocked_FailedLoginTrialLessThan4_returnFalse");
        LoginHelper helper = new LoginHelper();
        boolean result = helper.isIPBlocked(trialNumber);
        assertEquals(result, expResult);
    }

    public void isIPBlocked_FailedLoginTrialEquals4_returnTrue(){
        System.out.println("isIPBlocked_FailedLoginTrialLessThan4_returnFalse");
        LoginHelper helper = new LoginHelper();
        boolean result = helper.isIPBlocked(4);
        assertEquals(result, true);
    }

    @DataProvider(name = "shouldGetCorrectIPVerificationTimeInSeconds_dataProvider")
    public static Object[][] getDataToGetCorrectIPVerificationTimeI() {
        return new Object[][]{{1, 0}, {2, 5}, {3, 10}, {4, -1}, {5, -1}, {10, -1}};
    }

    @Test(dataProvider = "shouldGetCorrectIPVerificationTimeInSeconds_dataProvider")
    public void shouldGetCorrectIPVerificationTimeInSeconds(int trialNumber, int expResult) {
        System.out.println("shouldGetCorrectIPVerificationTimeInSeconds");
        LoginHelper helper = new LoginHelper();
        int result = helper.getIPVerificationTimeInSeconds(trialNumber);
        assertEquals(result, expResult);
    }


}

