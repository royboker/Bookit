package com.NewBookiT.BookiT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import com.NewBookiT.BookiT.activities.PdfEditActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class PdfEditActivityTest {

    private PdfEditActivity activity;

    @Before
    public void setup() {
        activity = new PdfEditActivity();
    }

    @Test
    public void titleValidation_ValidTitle_ReturnsTrue() {
        assertTrue(activity.isTitleValid("Example Title"));
    }

    @Test
    public void titleValidation_EmptyTitle_ReturnsFalse() {
        assertFalse(activity.isTitleValid(""));
    }

    @Test
    public void descriptionValidation_ValidDescription_ReturnsTrue() {
        assertTrue(activity.isDescriptionValid("Example Description"));
    }

    @Test
    public void descriptionValidation_EmptyDescription_ReturnsFalse() {
        assertFalse(activity.isDescriptionValid(""));
    }

    @Test
    public void selectedCategoryValidation_ValidCategory_ReturnsTrue() {
        assertTrue(activity.isSelectedCategoryValid("1"));
    }

    @Test
    public void selectedCategoryValidation_EmptyCategory_ReturnsFalse() {
        assertFalse(activity.isSelectedCategoryValid(""));
    }


}
