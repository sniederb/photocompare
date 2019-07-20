package ch.want.imagecompare.data;

import android.net.Uri;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageBeanTest {

    @Test
    public void getSelectedImageBeans_noSelection() {
        // arrange
        final ImageBean unselectedImage = buildImageBean("foobar001.jpg", "file:///storage/emulated/0/Download/EOS77D/foobar001.jpg");
        final ArrayList<ImageBean> imageBeans = new ArrayList<>();
        imageBeans.add(unselectedImage);
        // act
        final List<ImageBean> selectedBeans = ImageBean.getSelectedImageBeans(imageBeans);
        // assert
        assertEquals(0, selectedBeans.size());
    }

    @Test
    public void getSelectedImageBeans_selection() {
        // arrange
        final ImageBean unselectedImage = buildImageBean("foobar001.jpg", "file:///storage/emulated/0/Download/EOS77D/foobar001.jpg");
        final ImageBean selectedImage = buildImageBean("foobar002.jpg", "file:///storage/emulated/0/Download/EOS77D/foobar002.jpg");
        selectedImage.setSelected(true);
        final ArrayList<ImageBean> imageBeans = new ArrayList<>();
        imageBeans.add(unselectedImage);
        imageBeans.add(selectedImage);
        // act
        final List<ImageBean> selectedBeans = ImageBean.getSelectedImageBeans(imageBeans);
        // assert
        assertEquals(1, selectedBeans.size());
        assertSame(selectedImage, selectedBeans.get(0));
    }

    private static ImageBean buildImageBean(final String displayName, final String pathToImage) {
        final Uri uri = mock(Uri.class);
        when(uri.toString()).thenReturn(pathToImage);
        return new ImageBean(displayName, uri);
    }
}
