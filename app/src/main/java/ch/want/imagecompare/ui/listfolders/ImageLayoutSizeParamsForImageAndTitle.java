package ch.want.imagecompare.ui.listfolders;

import android.content.res.Configuration;
import android.content.res.Resources;

import ch.want.imagecompare.ui.AbstractImageLayoutSizeParams;
import ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams;

class ImageLayoutSizeParamsForImageAndTitle extends AbstractImageLayoutSizeParams implements ImageLayoutSizeParams {
    private static final int IMAGES_PER_ROW_PORTRAIT = 2;
    private static final int IMAGES_PER_ROW_LANDSCAPE = 3;
    /**
     * from res/layout/activity_list_all_image_folders.xml -> RecyclerView -> paddingLeft/Right
     */
    private static final int PADDING_IN_DP = 18;
    /**
     * reduce image size to allow for text underneath (see res/layout/view_image_and_title.xml)
     */
    private static final int IMAGE_MARGIN_IN_DP = 15;

    private final int viewSizeInPixel;
    private final int imageSizeInPixel;

    /**
     * Calculate target size of the view and the contained image, based on screen size.
     *
     * @param resources
     */
    ImageLayoutSizeParamsForImageAndTitle(final Resources resources) {
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
