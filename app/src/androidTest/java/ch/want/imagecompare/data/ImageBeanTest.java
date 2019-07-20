package ch.want.imagecompare.data;

import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

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
}