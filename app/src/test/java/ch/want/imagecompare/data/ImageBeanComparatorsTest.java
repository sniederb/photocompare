package ch.want.imagecompare.data;

import android.net.Uri;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageBeanComparatorsTest {

    @Test
    public void testByImageName() {
        final List<ImageBean> imageBeans = createImageBeans();
        Collections.sort(imageBeans, ImageBeanComparators.byImageName());
        Assert.assertEquals("001.jpg", imageBeans.get(0).getDisplayName());
    }

    @Test
    public void testByDateAsc() {
        final List<ImageBean> imageBeans = createImageBeans();
        Collections.sort(imageBeans, ImageBeanComparators.byDateAsc());
        Assert.assertEquals("003.jpg", imageBeans.get(0).getDisplayName());
    }

    @Test
    public void testByDateDesc() {
        final List<ImageBean> imageBeans = createImageBeans();
        Collections.sort(imageBeans, ImageBeanComparators.byDateDesc());
        Assert.assertEquals("002.jpg", imageBeans.get(0).getDisplayName());
    }

    private static List<ImageBean> createImageBeans() {
        final List<ImageBean> result = new ArrayList<>();
        result.add(buildImageBean("003.jpg", 1234, ""));
        result.add(buildImageBean("001.jpg", 2234, ""));
        result.add(buildImageBean("002.jpg", 5534, ""));
        result.add(buildImageBean("004.jpg", 4434, ""));
        return result;
    }

    private static ImageBean buildImageBean(final String displayName, final long lastModified, final String pathToImage) {
        final Uri uri = mock(Uri.class);
        when(uri.toString()).thenReturn(pathToImage);
        return new ImageBean(displayName, lastModified, uri, uri);
    }
}