package ch.want.imagecompare.data;

import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
 * Need instrumentation as we're using android.net.Uri
 */
@RunWith(AndroidJUnit4.class)
public class ImageBeanTest {

    @Test
    public void getPathToImage_uriIsStoredCorrectly() {
        // arrange
        final Uri uri = Uri.parse("ftp://ftp.is.co.za/rfc/rfc1808.txt");
        final ImageBean testee = new ImageBean("foobar", uri);
        // act
        final Uri uriFromGet = testee.getFileUri();
        // assert
        assertEquals(uri, uriFromGet);
    }

    @Test
    public void copySelectedState_uriIsStoredCorrectly() {
        // arrange
        final ArrayList<ImageBean> images = buildImageBeanList();
        final ArrayList<ImageBean> selectedImages = new ArrayList<>();
        selectedImages.add(new ImageBean("J0091158.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091158.JPG")));
        // act
        ImageBean.copySelectedState(selectedImages, images);
        // assert
        assertFalse(images.get(0).isSelected());
        assertTrue(images.get(1).isSelected());
        assertFalse(images.get(0).isSelected());
    }

    private static ArrayList<ImageBean> buildImageBeanList() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean("J0091157.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091157.JPG")));
        beans.add(new ImageBean("J0091158.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091158.JPG")));
        beans.add(new ImageBean("J0091159.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091159.JPG")));
        return beans;
    }
}