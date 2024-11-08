package com.NewBookiT.BookiT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.net.Uri;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.NewBookiT.BookiT.activities.PdfAddActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class PdfAddActivityTest {

    private PdfAddActivity activity;

    @Before
    public void setup() {
        activity = new PdfAddActivity();
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
        assertTrue(activity.isSelectedCategoryValid("Science"));
    }

    @Test
    public void selectedCategoryValidation_EmptyCategory_ReturnsFalse() {
        assertFalse(activity.isSelectedCategoryValid(""));
    }

    @Test
    public void pdfSelected_ValidPdfSelected_ReturnsTrue() {
        assertTrue(activity.isPdfSelected(Uri.parse("content://pdf_uri")));
    }

    @Test
    public void pdfSelected_NoPdfSelected_ReturnsFalse() {
        assertFalse(activity.isPdfSelected(null));
    }

}
