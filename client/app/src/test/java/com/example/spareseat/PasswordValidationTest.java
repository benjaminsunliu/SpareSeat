package com.example.spareseat;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PasswordValidationTest {

    // ── Valid passwords ───────────────────────────────────────────

    @Test
    public void validPassword_returnsNull() {
        assertNull(SignUpActivity.getPasswordError("Password1"));
    }

    @Test
    public void validPasswordWithSymbols_returnsNull() {
        assertNull(SignUpActivity.getPasswordError("MyPass1!@#"));
    }

    @Test
    public void validPasswordExactlyEightChars_returnsNull() {
        assertNull(SignUpActivity.getPasswordError("Abcdef1g"));
    }

    // ── Empty / null ──────────────────────────────────────────────

    @Test
    public void nullPassword_returnsError() {
        assertNotNull(SignUpActivity.getPasswordError(null));
    }

    @Test
    public void emptyPassword_returnsError() {
        assertNotNull(SignUpActivity.getPasswordError(""));
    }

    // ── Length ────────────────────────────────────────────────────

    @Test
    public void tooShortPassword_returnsError() {
        assertNotNull(SignUpActivity.getPasswordError("Ab1"));
    }

    @Test
    public void sevenChars_returnsError() {
        assertNotNull(SignUpActivity.getPasswordError("Abcde1f"));
    }

    // ── Uppercase requirement ─────────────────────────────────────

    @Test
    public void noUppercase_returnsError() {
        assertNotNull(SignUpActivity.getPasswordError("password1"));
    }

    @Test
    public void allLowerWithNumber_returnsError() {
        assertNotNull(SignUpActivity.getPasswordError("abcdefg1"));
    }

    // ── Number requirement ────────────────────────────────────────

    @Test
    public void noNumber_returnsError() {
        assertNotNull(SignUpActivity.getPasswordError("PasswordABC"));
    }

    @Test
    public void allUpperNoNumber_returnsError() {
        assertNotNull(SignUpActivity.getPasswordError("ABCDEFGH"));
    }
}
