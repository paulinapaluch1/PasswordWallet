package com.bsi.ppaluch.crypto;

import org.testng.annotations.Test;

import static com.bsi.ppaluch.crypto.Coder.generatePasswordHash;
import static com.bsi.ppaluch.crypto.Coder.generateSalt;
import static org.testng.AssertJUnit.assertNotNull;

public class CoderTest {

    @Test
    public void shouldGeneratePasswordHash(){
        String masterPassword = "TESTpassword234567";
        String salt = generateSalt();
        String hash = generatePasswordHash(masterPassword, salt);

        assertNotNull(hash);
    }


}
