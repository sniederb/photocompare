package ch.want.imagecompare.data;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class ImageBeanComparators {

    public static Comparator<ImageBean> byImageName() {
        return (a, b) -> StringUtils.compare(a.getDisplayName(), b.getDisplayName(), true);
    }

    /**
     * File create date is not accessible via Android, and is questionable anyway. Downloading
     * images from a camera to the phone will result in files with a create date of when the
     * download occurred, not "image taken". Thus this method sorts by filename.
     */
    public static Comparator<ImageBean> byDateAsc() {
        return byImageName();
    }

    public static Comparator<ImageBean> byDateDesc() {
        return byDateAsc().reversed();
    }
}
