package com.NewBookiT.BookiT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.net.Uri;

import com.NewBookiT.BookiT.activities.ProfileEditActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class ProfileEditActivityTest {

    private ProfileEditActivity activity;

    @Before
    public void setup() {
        activity = new ProfileEditActivity();
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
    public void imageUriValidation_ValidUri_ReturnsTrue() {
        assertTrue(activity.isImageUriValid(Uri.parse("content://media/external/images/media/12345")));
    }

    @Test
    public void imageUriValidation_NullUri_ReturnsFalse() {
        assertFalse(activity.isImageUriValid(null));
    }
}
