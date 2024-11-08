package com.NewBookiT.BookiT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.NewBookiT.BookiT.activities.ErrorActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})

public class ErrorActivityTest {

    @Test
    public void emailValidation_CorrectEmail_ReturnsTrue() {
        ErrorActivity activity = new ErrorActivity();
        assertTrue(activity.isEmailValid("test@example.com"));
    }

    @Test
    public void emailValidation_EmptyEmail_ReturnsFalse() {
        ErrorActivity activity = new ErrorActivity();
        assertFalse(activity.isEmailValid(""));
    }

    @Test
    public void emailValidation_IncorrectEmail_ReturnsFalse() {
        ErrorActivity activity = new ErrorActivity();
        assertFalse(activity.isEmailValid("test"));
    }
}
