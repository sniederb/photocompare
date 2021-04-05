package ch.want.imagecompare.ui.listimages;

import android.content.res.Configuration;
import android.content.res.Resources;

import ch.want.imagecompare.ui.AbstractImageLayoutSizeParams;
import ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams;

class ImageLayoutSizeParamsForImageOnly extends AbstractImageLayoutSizeParams implements ImageLayoutSizeParams {
    private static final int IMAGES_PER_ROW_PORTRAIT = 3;
    private static final int IMAGES_PER_ROW_LANDSCAPE = 5;
    /**
     * from res/layout/activity_list_images_in_folder.xml -> RecyclerView -> paddingLeft/Right
     */
    private static final int PADDING_IN_DP = 2 * 18;
    /**
     * reduce image size slightly to allow for proper display. The LinearLayout has a 6dp padding,
     * defined in res/layout/view_image_only.xml
     */
    private static final int IMAGE_MARGIN_IN_DP = 2 * 6;

    private final int viewSizeInPixel;
    private final int imageSizeInPixel;

    ImageLayoutSizeParamsForImageOnly(final Resources resources) {
        viewSizeInPixel = getColumnWidthInPixel(PADDING_IN_DP, getImagesPerRow(resources.getConfiguration()), resources);
        imageSizeInPixel = getImageSizeInPixel(IMAGE_MARGIN_IN_DP, viewSizeInPixel, resources);
    }

    private static int getImagesPerRow(final Configuration configuration) {
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ? IMAGES_PER_ROW_LANDSCAPE : IMAGES_PER_ROW_PORTRAIT;
    }

    @Override
    public int getViewSizeInPixel() {
        return viewSizeInPixel;
    }

    @Override
    public int getImageSizeInPixel() {
        return imageSizeInPixel;
    }
}
