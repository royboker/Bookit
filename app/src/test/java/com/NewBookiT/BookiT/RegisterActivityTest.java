package com.NewBookiT.BookiT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.NewBookiT.BookiT.activities.RegisterActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})

public class RegisterActivityTest {

    @Test
    public void nameValidation_CorrectName_ReturnsTrue() {
        RegisterActivity activity = new RegisterActivity();
        assertTrue(activity.isNameValid("John Doe"));
    }

    @Test
    public void nameValidation_EmptyName_ReturnsFalse() {
        RegisterActivity activity = new RegisterActivity();
        assertFalse(activity.isNameValid(""));
    }

    @Test
    public void emailValidation_CorrectEmail_ReturnsTrue() {
        RegisterActivity activity = new RegisterActivity();
        assertTrue(activity.isEmailValid("test@example.com"));
    }

    @Test
    public void emailValidation_IncorrectEmail_ReturnsFalse() {
        RegisterActivity activity = new RegisterActivity();
        assertFalse(activity.isEmailValid("test"));
    }

    @Test
    public void passwordValidation_CorrectPassword_ReturnsTrue() {
        RegisterActivity activity = new RegisterActivity();
        assertTrue(activity.isPasswordValid("123456"));
    }

    @Test
    public void passwordValidation_ShortPassword_ReturnsFalse() {
        RegisterActivity activity = new RegisterActivity();
        assertFalse(activity.isPasswordValid("12345"));
    }

    @Test
    public void passwordMatch_MatchingPasswords_ReturnsTrue() {
        RegisterActivity activity = new RegisterActivity();
        assertTrue(activity.doPasswordsMatch("123456", "123456"));
    }

    @Test
    public void passwordMatch_NonMatchingPasswords_ReturnsFalse() {
        RegisterActivity activity = new RegisterActivity();
        assertFalse(activity.doPasswordsMatch("123456", "654321"));
    }
}
