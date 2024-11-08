package com.NewBookiT.BookiT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.net.Uri;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.NewBookiT.BookiT.activities.ProfileActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class ProfileActivityTest {

    private ProfileActivity activity;

    @Before
    public void setup() {
        activity = new ProfileActivity();
    }

    @Test
    public void nameValidation_ValidName_ReturnsTrue() {
        assertTrue(activity.isNameValid("John Doe"));
    }

    @Test
    public void nameValidation_EmptyName_ReturnsFalse() {
        assertFalse(activity.isNameValid(""));
    }

    @Test
    public void uriValidation_ValidUri_ReturnsTrue() {
        assertTrue(activity.isUriValid(Uri.parse("content://profile_pic_uri")));
    }

    @Test
    public void uriValidation_NullUri_ReturnsFalse() {
        assertFalse(activity.isUriValid(null));
    }

    @Test
    public void emailVerification_VerifiedEmail_ReturnsTrue() {
        assertTrue(activity.isEmailVerified());
    }
}
