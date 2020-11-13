/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bsi.ppaluch.crypto;

import com.bsi.ppaluch.entity.User;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.testng.annotations.*;

import static com.bsi.ppaluch.crypto.Coder.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 *
 * @author pauli
 */
public class CoderNGTest {
    public static String SALT = "[B@8c75639";

    @Mock
    private CorrectPasswordVerifier mockPasswordVerifier;

    @TestSubject
    private Coder coder = new Coder(mockPasswordVerifier);


    public CoderNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
        EasyMockSupport.injectMocks(this);

    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    @Test
    public void shouldGenerateSalt() {
        System.out.println("shouldGenerateSalt");
        String result = generateSalt();
        assertNotNull(result);
    }

    @DataProvider(name="shouldGeneratePasswordHashDataProvider")
    public static Object[][] getDataToGeneratePasswordHash(){
        return new Object[][] {{"123","caf5570997751f5d9002750ab6ca7169309b56d3f319ef9f1134c2dc83289b253fc143b55fd0f14e6b0ccd9dc2c94fbae59a75dad1d796151c0c0e1c433d5eac"}};
    }

    @Test(dataProvider = "shouldGeneratePasswordHashDataProvider")
    public void shouldGeneratePasswordHash(String masterPassword, String expResult) {
        System.out.println("generatePasswordHash");
        Coder coder = new Coder(new CorrectPasswordVerifierAdapter());
        String result = coder.generatePasswordHash(masterPassword, SALT);
        assertEquals(result, expResult);
    }

    @Test
    public void shouldEncryptPassword() throws Exception {
        System.out.println("shouldEncryptPassword");
        String masterPasswordHash = "caf5570997751f5d9002750ab6ca7169309b56d3f319ef9f1134c2dc83289b253fc143b55fd0f14e6b0ccd9dc2c94fbae59a75dad1d796151c0c0e1c433d5eac";
        String passwordToSave = "123";
        String expResult = "oEcapAJ0PTcPPPHFCvfi6g==";
        Coder coder = new Coder(new CorrectPasswordVerifierAdapter());
        String result = coder.encryptPassword(masterPasswordHash, passwordToSave);
        assertEquals(result, expResult);
    }

    @DataProvider(name="shouldDecryptPasswordDataProvider")
    public static Object[][] numbers1(){
        return new Object[][] {{"caf5570997751f5d9002750ab6ca7169309b56d3f319ef9f1134c2dc83289b253fc143b55fd0f14e6b0ccd9dc2c94fbae59a75dad1d796151c0c0e1c433d5eac","oEcapAJ0PTcPPPHFCvfi6g==","123"}};
    }

    @Test(dataProvider = "shouldDecryptPasswordDataProvider")
    public void shouldDecryptPassword(String masterPasswordHash, String encryptedPassword, String expResult) throws Exception {
        System.out.println("shouldDecryptPassword");
        Coder coder = new Coder(new CorrectPasswordVerifierAdapter());
        String result = coder.decryptPassword(masterPasswordHash, encryptedPassword);
        assertEquals(result, expResult);
    }

    @Test
    public void shouldCheckIfIsCorrectPassword() throws Exception {
        System.out.println("shouldCheckIfIsCorrectPassword");
        User oldUser = new User("login6","[B@4e9f6d66","ab45bcb99f241934fbcaefde51bb389a8982dec110e9d332ba9a7a7a6191f345ebbed372ad97454debbfba4782adfeb1d64f9d15e8599726328e5399f3022e0a",true);
        String oldPassword = "123";
        boolean expResult = true;
        Coder coder = new Coder(new CorrectPasswordVerifierAdapter());

        boolean result = coder.isCorrectPassword(oldUser, oldPassword);
        assertEquals(result, expResult);
    }

    @Test
    public void shouldCheckIfGivenPasswordIsCorrectWhenPasswordsSavedWithHmac() throws Exception {
        System.out.println("shouldCheckIfGivenPasswordIsCorrectWhenPasswordsSavedWithHmac");
        User oldUser = new User("login6","[B@cee6738","b151f961c172df7c928ef2c8217b98ed01c2e47ee783f02573e8b628ccd28d49ec1a856b3e5432cc74f8392b292f99e57386981c683080f700cf474c0eec0b29",false);
        String givenPassword = "123";
        boolean expResult = true;
       CorrectPasswordVerifier verifier = new CorrectPasswordVerifier();
        boolean result = verifier.isTheSamePasswordSavedWithHmac(oldUser, givenPassword);
        assertEquals(result, expResult);
    }

    @Test
    public void shouldCheckIfGivenPasswordIsCorrectWhenPasswordsSavedWithSha() throws Exception {
        System.out.println("shouldCheckIfGivenPasswordIsCorrectWhenPasswordsSavedWithSha");
        User oldUser = new User("login6","[B@4e9f6d66","ab45bcb99f241934fbcaefde51bb389a8982dec110e9d332ba9a7a7a6191f345ebbed372ad97454debbfba4782adfeb1d64f9d15e8599726328e5399f3022e0a",true);
        String givenPassword = "123";
        boolean expResult = true;
        CorrectPasswordVerifier verifier = new CorrectPasswordVerifier();
        boolean result = verifier.isTheSamePasswordSavedWithSHA( givenPassword,oldUser);
        assertEquals(result, expResult);
    }

    @Test
    public void shouldCheckIfGivenPasswordIsCorrectWhenPasswordsSavedWithHmac_usingMock() throws Exception {
        System.out.println("shouldCheckIfGivenPasswordIsCorrectWhenPasswordsSavedWithHmac_usingMock");
        User oldUser = new User("login6","[B@cee6738","b151f961c172df7c928ef2c8217b98ed01c2e47ee783f02573e8b628ccd28d49ec1a856b3e5432cc74f8392b292f99e57386981c683080f700cf474c0eec0b29",false);
        String givenPassword = "123";
        expect(mockPasswordVerifier.isTheSamePasswordSavedWithHmac(oldUser,givenPassword)).andReturn(true);
        replay(mockPasswordVerifier);

        assertEquals(coder.getVerifier().isTheSamePasswordSavedWithHmac(oldUser, givenPassword), true);
    }

    @Test
    public void shouldCheckIfGivenPasswordIsCorrectWhenPasswordsSavedWithSha_usingMock() throws Exception {
        System.out.println("shouldCheckIfGivenPasswordIsCorrectWhenPasswordsSavedWithSha_usingMock");
        User oldUser = new User("login6","[B@4e9f6d66","ab45bcb99f241934fbcaefde51bb389a8982dec110e9d332ba9a7a7a6191f345ebbed372ad97454debbfba4782adfeb1d64f9d15e8599726328e5399f3022e0a",true);
        String givenPassword = "123";
        expect(mockPasswordVerifier.isTheSamePasswordSavedWithSHA(givenPassword,oldUser)).andReturn(true);
        replay(mockPasswordVerifier);

        assertEquals(coder.getVerifier().isTheSamePasswordSavedWithSHA(givenPassword,oldUser), true);

    }



}
