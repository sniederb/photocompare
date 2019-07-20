package ch.want.imagecompare.ui.listfolders;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams;

class ImageLayoutSizeParamsForImageAndTitle implements ImageLayoutSizeParams {
    private static final int IMAGES_PER_ROW_PORTRAIT = 2;
    private static final int IMAGES_PER_ROW_LANDSCAPE = 3;
    private final int viewSizeInPixel;
    private final int imageSizeInPixel;

    /**
     * Calculate target size of the view and the contained image, based on screen size.
     *
     * @param resources
     */
    ImageLayoutSizeParamsForImageAndTitle(final Resources resources) {
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        // from res/layout/activity_list_all_image_folders.xml -> RecyclerView -> padding
        final int paddingInDp = 2 * 18;
        final int paddingInPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingInDp, metrics);
        viewSizeInPixel = (metrics.widthPixels - paddingInPixel) / getImagesPerRow(resources.getConfiguration());
        // reduce image size to allow for text underneath (see res/layout/view_image_and_title.xml)
        final int imageMarginInDp = 30;
        final int imageMarginInPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imageMarginInDp, metrics);
        imageSizeInPixel = viewSizeInPixel - imageMarginInPixel;
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
