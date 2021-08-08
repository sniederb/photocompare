package ch.want.imagecompare.ui;

import android.util.DisplayMetrics;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AbstractImageLayoutSizeParamsTest {
    @Test
    public void getColumnWidthInPixel() {
        // arrange
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 3.8333F;
        displayMetrics.widthPixels = 1440;
        // act
        final int result = AbstractImageLayoutSizeParams.getColumnWidthInPixel(18, 3, displayMetrics);
        // assert
        assertEquals("Should err on the generous side", 433, result);
    }

    @Test
    public void getImageSizeInPixel() {
        // arrange
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 3.8333F;
        displayMetrics.widthPixels = 1440;
        // act
        final int result = AbstractImageLayoutSizeParams.getImageSizeInPixel(6, 433, displayMetrics);
        // assert
        assertEquals("Should err on the generous side", 387, result);
    }
}