package ch.want.imagecompare.ui.listfolders;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.want.imagecompare.TestSettings;

@RunWith(AndroidJUnit4.class)
public class ImageLayoutSizeParamsForImageAndTitleTest {

    private static final int WIDTH_PIXELS_NEXUS5 = 1080;
    @Rule
    public GrantPermissionRule permissionRule = TestSettings.permissionRule();

    @Test
    public void getViewSizeInPixel() {
        // arrange
        final Resources resources = getTestDeviceResources();
        assumeThat("Test runs on Nexus 5", resources.getDisplayMetrics().widthPixels, is(WIDTH_PIXELS_NEXUS5));
        final ImageLayoutSizeParamsForImageAndTitle testee = new ImageLayoutSizeParamsForImageAndTitle(resources);
        // act
        final int viewSize = testee.getViewSizeInPixel();
        // assert
        assertEquals(489, viewSize);
    }

    @Test
    public void getImageSizeInPixel() {
        // arrange
        final Resources resources = getTestDeviceResources();
        assumeThat("Test runs on Nexus 5", resources.getDisplayMetrics().widthPixels, is(WIDTH_PIXELS_NEXUS5));
        final ImageLayoutSizeParamsForImageAndTitle testee = new ImageLayoutSizeParamsForImageAndTitle(resources);
        // act
        final int imageSize = testee.getImageSizeInPixel();
        // assert
        assertEquals(405, imageSize);
    }

    private static Resources getTestDeviceResources() {
        final Context appContext = ApplicationProvider.getApplicationContext();
        return appContext.getResources();
    }
}