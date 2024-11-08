package com.NewBookiT.BookiT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.NewBookiT.BookiT.activities.CategoryAddActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})

public class CategoryAddActivityTest {

    @Test
    public void categoryValidation_ValidCategory_ReturnsTrue() {
        CategoryAddActivity activity = new CategoryAddActivity();
        assertTrue(activity.isCategoryValid("Fiction"));
    }

    @Test
    public void categoryValidation_EmptyCategory_ReturnsFalse() {
        CategoryAddActivity activity = new CategoryAddActivity();
        assertFalse(activity.isCategoryValid(""));
    }
}
