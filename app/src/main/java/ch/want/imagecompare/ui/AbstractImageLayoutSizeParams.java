package ch.want.imagecompare.ui;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Base class to calculate image size in a grid. The resource layouts use "dp" as unit, so
 * pixel are calculated using {@link TypedValue#COMPLEX_UNIT_DIP}, which is
 * <pre>value * metrics.density</pre>
 * <ol>
 *     <li>ImageBeanListRecyclerViewAdapter creates a RecyclerView</li>
 *     <li>The ViewHolder varies, e.g. it might be view_image_only.xml</li>
 *     <li>ImageBeanListRecyclerViewAdapter#adjustViewBasedOnScreenWidth adjusts the dimensions of the ViewHolder</li>
 * </ol>
 */
public abstract class AbstractImageLayoutSizeParams {

    /**
     * For a given {@code columnCount} and overall {@code paddingInDip} (in DIP), this method calculates the column width
     * in pixel.
     */
    protected static int getColumnWidthInPixel(final int paddingInDip, final int columnCount, final Resources resources) {
        return getColumnWidthInPixel(paddingInDip, columnCount, resources.getDisplayMetrics());
    }

    static int getColumnWidthInPixel(final int paddingInDip, final int columnCount, final DisplayMetrics metrics) {
        final int paddingInPixel = marginInDipToPixel(paddingInDip, metrics);
        return (getWidthPixels(metrics) - (2 * paddingInPixel)) / columnCount;
    }

    /**
     * For a given {@code columnWidthInPixel}, probably calculated with {@link #getColumnWidthInPixel(int, int, Resources)}, this method
     * returns the image (=content) width in pixel given a {@code paddingInDip}.
     */
    protected static int getImageSizeInPixel(final int paddingInDip, final int columnWidthInPixel, final Resources resources) {
        return getImageSizeInPixel(paddingInDip, columnWidthInPixel, resources.getDisplayMetrics());
    }

    static int getImageSizeInPixel(final int paddingInDip, final int columnWidthInPixel, final DisplayMetrics metrics) {
        final int imageMarginInPixel = marginInDipToPixel(paddingInDip, metrics);
        return columnWidthInPixel - (2 * imageMarginInPixel);
    }

    /**
     * The margin calculated here is virtual, the actual margin is defined in the layout and will be applied by Android.
     * If this calculation errs towards a lower value, the layout will break because there's not enough space. If this
     * calculation errs towards a higher value, there will be a bit too much margin, but the column layout will still be intact.
     */
    private static int marginInDipToPixel(final int dimensionInDip, final DisplayMetrics metrics) {
        return (int) Math.ceil(dimensionInDip * metrics.density);
    }

    /**
     * Experience shows that when changing display size (eg. to 'smaller'), the device width is not reflected precisely.
     * Thus a 720px width display becomes width=564dp with a density of 1.275 (564dp x 1.275 = 719.1px). This
     * method doesn't simply use {@link DisplayMetrics#widthPixels}, but rather calculates via DPs.
     */
    private static int getWidthPixels(final DisplayMetrics metrics) {
        final int widthDp = (int) (metrics.widthPixels / metrics.density);
        return (int) (widthDp * metrics.density);
    }
}
