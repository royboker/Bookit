package com.NewBookiT.BookiT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.NewBookiT.BookiT.activities.LoginActivity;
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})

public class LoginActivityTest {

    @Test
    public void emailValidation_CorrectEmail_ReturnsTrue() {
        LoginActivity activity = new LoginActivity();
        assertTrue(activity.isEmailValid("test@example.com"));
    }

    @Test
    public void emailValidation_EmptyEmail_ReturnsFalse() {
        LoginActivity activity = new LoginActivity();
        assertFalse(activity.isEmailValid(""));
    }

    @Test
    public void emailValidation_IncorrectEmail_ReturnsFalse() {
        LoginActivity activity = new LoginActivity();
        assertFalse(activity.isEmailValid("test"));
    }

    @Test
    public void passwordValidation_CorrectPassword_ReturnsTrue() {
        LoginActivity activity = new LoginActivity();
        assertTrue(activity.isPasswordValid("123456"));
    }

    @Test
    public void passwordValidation_ShortPassword_ReturnsFalse() {
        LoginActivity activity = new LoginActivity();
        assertFalse(activity.isPasswordValid("12345"));
    }

    @Test
    public void passwordValidation_EmptyPassword_ReturnsFalse() {
        LoginActivity activity = new LoginActivity();
        assertFalse(activity.isPasswordValid(""));
    }
}
