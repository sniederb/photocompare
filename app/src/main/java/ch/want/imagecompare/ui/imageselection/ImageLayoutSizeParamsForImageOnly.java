package ch.want.imagecompare.ui.imageselection;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams;

public class ImageLayoutSizeParamsForImageOnly implements ImageLayoutSizeParams {
    private static final int IMAGES_PER_ROW_PORTRAIT = 3;
    private static final int IMAGES_PER_ROW_LANDSCAPE = 5;
    private final int viewSizeInPixel;
    private final int imageSizeInPixel;

    ImageLayoutSizeParamsForImageOnly(final Resources resources) {
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        // from res/layout/activity_selected_images.xml -> RecyclerView -> padding
        final int paddingInDp = 2 * 18;
        final int paddingInPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingInDp, metrics);
        viewSizeInPixel = (metrics.widthPixels - paddingInPixel) / getImagesPerRow(resources.getConfiguration());
        // reduce image size slightly to allow for proper display
        final int imageMarginInDp = 2 * 6;
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
