package com.bsi.ppaluch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.bsi.ppaluch.crypto.Coder.generatePasswordHash;
import static com.bsi.ppaluch.crypto.Coder.generateSalt;
import static org.testng.AssertJUnit.assertNotNull;

@SpringBootTest
class ApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	public void shouldGeneratePasswordHash(){
		String masterPassword = "TESTpassword234567";
		String salt = generateSalt();
		String hash = generatePasswordHash(masterPassword, salt);

		assertNotNull(hash);
	}




}
