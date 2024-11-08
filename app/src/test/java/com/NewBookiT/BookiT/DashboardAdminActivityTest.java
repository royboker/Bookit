package com.NewBookiT.BookiT;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.NewBookiT.BookiT.activities.DashboardAdminActivity;

public class DashboardAdminActivityTest {

    @Test
    public void isPasswordValid_ValidPassword_ReturnsTrue() {
        String password = "123456";
        assertTrue(DashboardAdminActivity.isPasswordValid(password));
    }

    @Test
    public void isPasswordValid_InvalidPassword_ReturnsFalse() {
        String password = "12345";
        assertFalse(DashboardAdminActivity.isPasswordValid(password));
    }

    @Test
    public void isPasswordValid_PasswordWithSpecialCharacters_ReturnsTrue() {
        String password = "Passw0rd!";
        assertTrue(DashboardAdminActivity.isPasswordValid(password));
    }

    @Test
    public void isPasswordValid_PasswordWithOnlyLetters_ReturnsFalse() {
        String password = "abcdef";
        assertTrue(DashboardAdminActivity.isPasswordValid(password));
    }

    @Test
    public void isEmailValid_EmptyEmail_ReturnsFalse() {
        String email = "";
        assertFalse(DashboardAdminActivity.isEmailValid(email));
    }

    @Test
    public void isPasswordValid_EmptyPassword_ReturnsFalse() {
        String password = "";
        assertFalse(DashboardAdminActivity.isPasswordValid(password));
    }

    @Test
    public void isEmailValid_NullEmail_ReturnsFalse() {
        String email = null;
        assertFalse(DashboardAdminActivity.isEmailValid(email));
    }

    @Test
    public void isPasswordValid_NullPassword_ReturnsFalse() {
        String password = null;
        assertFalse(DashboardAdminActivity.isPasswordValid(password));
    }
}
