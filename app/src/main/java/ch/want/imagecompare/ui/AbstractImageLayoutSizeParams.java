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
 *     <li>The ViewHolder varies, eg. it might be view_image_only.xml</li>
 *     <li>ImageBeanListRecyclerViewAdapter#adjustViewBasedOnScreenWidth adjusts the dimensions of the ViewHolder</li>
 * </ol>
 */
public abstract class AbstractImageLayoutSizeParams {

    protected static int getColumnWidthInPixel(final int padding, final int columnCount, final Resources resources) {
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        final int paddingInPixel = (int) dipToPixel(padding, resources);
        return (getWidthPixels(metrics) - paddingInPixel) / columnCount;
    }

    protected static int getImageSizeInPixel(final int padding, final int columnWidth, final Resources resources) {
        final int imageMarginInPixel = (int) dipToPixel(padding, resources);
        return columnWidth - imageMarginInPixel;
    }

    private static float dipToPixel(final int dimensionInDip, final Resources resources) {
        return dimensionInDip * resources.getDisplayMetrics().density;
    }

    /**
     * Experience shows that when changing display size (eg. to 'smaller'), the device width is not reflected precisely.
     * Thus a 720px width display becomes width=564dp with a density of 1.275 (564dp x 1.275 = 719.1px). This
     * method doesn't simply use {@link DisplayMetrics#widthPixels}, but rather calculates via DPs.
     *
     * @return
     */
    private static int getWidthPixels(final DisplayMetrics metrics) {
        final int widthDp = (int) (metrics.widthPixels / metrics.density);
        return (int) (widthDp * metrics.density);
    }
}
