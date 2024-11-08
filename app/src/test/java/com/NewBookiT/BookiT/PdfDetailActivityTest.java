package com.NewBookiT.BookiT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.net.Uri;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import com.NewBookiT.BookiT.activities.PdfDetailActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class PdfDetailActivityTest {

    private PdfDetailActivity activity;

    @Before
    public void setup() {
        activity = new PdfDetailActivity();
    }

    @Test
    public void isStringValid_WithValidString_ReturnsTrue() {
        assertTrue(activity.isStringValid("Valid String"));
    }

    @Test
    public void isStringValid_WithEmptyString_ReturnsFalse() {
        assertFalse(activity.isStringValid(""));
    }

    @Test
    public void isUriValid_WithValidUri_ReturnsTrue() {
        Uri testUri = Uri.parse("content://testUri");
        assertTrue(activity.isUriValid(testUri));
    }

    @Test
    public void isUriValid_WithNullUri_ReturnsFalse() {
        assertFalse(activity.isUriValid(null));
    }

    @Test
    public void isListNotEmpty_WithNotEmptyList_ReturnsTrue() {
        ArrayList<String> testList = new ArrayList<>();
        testList.add("Item");
        assertTrue(activity.isListNotEmpty(testList));
    }

    @Test
    public void isListNotEmpty_WithEmptyList_ReturnsFalse() {
        ArrayList<String> testList = new ArrayList<>();
        assertFalse(activity.isListNotEmpty(testList));
    }

}
