package ch.want.imagecompare.ui.listfolders;

import android.content.Context;
import android.content.res.Resources;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

@RunWith(AndroidJUnit4.class)
public class ImageLayoutSizeParamsForImageAndTitleTest {

    private static final int WIDTH_PIXELS_NEXUS5 = 1080;

    @Test
    public void getViewSizeInPixel() {
        // arrange
        final Resources resources = getTestDeviceResources();
        assumeThat(resources.getDisplayMetrics().widthPixels, is(WIDTH_PIXELS_NEXUS5));
        final ImageLayoutSizeParamsForImageAndTitle testee = new ImageLayoutSizeParamsForImageAndTitle(resources);
        // act
        final int viewSize = testee.getViewSizeInPixel();
        // assert
        assertEquals(492, viewSize);
    }

    @Test
    public void getImageSizeInPixel() {
        // arrange
        final Resources resources = getTestDeviceResources();
        assumeThat(resources.getDisplayMetrics().widthPixels, is(WIDTH_PIXELS_NEXUS5));
        final ImageLayoutSizeParamsForImageAndTitle testee = new ImageLayoutSizeParamsForImageAndTitle(resources);
        // act
        final int imageSize = testee.getImageSizeInPixel();
        // assert
        assertEquals(414, imageSize);
    }

    private static Resources getTestDeviceResources() {
        final Context appContext = ApplicationProvider.getApplicationContext();
        return appContext.getResources();
    }
}