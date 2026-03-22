package org.example.UtilTests;


import com.gym.crm.Util.PasswordGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Test
    void generatePassword_shouldReturnTenCharacters() {
        String password = passwordGenerator.generatePassword();
        assertEquals(10, password.length());
    }

    @Test
    void generatePassword_shouldOnlyContainValidCharacters() {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String password = passwordGenerator.generatePassword();

        for (char c : password.toCharArray()) {
            assertTrue(validChars.indexOf(c) >= 0,
                    "Invalid character found: " + c);
        }
    }

    @Test
    void generatePassword_shouldReturnDifferentPasswordsEachTime() {
        String password1 = passwordGenerator.generatePassword();
        String password2 = passwordGenerator.generatePassword();
        assertNotEquals(password1, password2);
    }

    @Test
    void generatePassword_shouldNotReturnNull() {
        assertNotNull(passwordGenerator.generatePassword());
    }
}
