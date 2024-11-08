package com.NewBookiT.BookiT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import com.NewBookiT.BookiT.activities.PdfListAdminActivity;
import com.technifysoft.BookiT.models.ModelPdf;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class PdfListAdminActivityTest {

    private PdfListAdminActivity activity;

    @Before
    public void setup() {
        activity = new PdfListAdminActivity();
    }

    @Test
    public void searchQueryValidation_ValidQuery_ReturnsTrue() {
        assertTrue(activity.isSearchQueryValid("Science"));
    }

    @Test
    public void searchQueryValidation_EmptyQuery_ReturnsFalse() {
        assertFalse(activity.isSearchQueryValid(""));
    }

    @Test
    public void pdfListLoadedValidation_EmptyPdfList_ReturnsFalse() {
        ArrayList<ModelPdf> pdfList = new ArrayList<>();
        assertFalse(activity.isPdfListLoaded(pdfList));
    }
}
