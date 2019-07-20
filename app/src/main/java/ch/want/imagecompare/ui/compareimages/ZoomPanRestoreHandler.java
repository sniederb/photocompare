package ch.want.imagecompare.ui.compareimages;

import android.graphics.Matrix;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.viewpager.widget.ViewPager;

/**
 * This class handles the responsibility of remembering zoom/pan of an image, and
 * restoring that matrix on the next image on paging.
 */
abstract class ZoomPanRestoreHandler implements ViewPager.OnPageChangeListener, View.OnLayoutChangeListener, RequestListener<Uri, GlideDrawable> {

    private int latestPagerIndex = 0;
    private boolean pagerStateIdle;
    private boolean imageResourceReady;
    private Matrix lastSuppMatrix;

    abstract void onApplyZoomPanMatrix(Matrix targetDisplayMatrix);

    /**
     * Called when the ViewPager has selected a new page. Beware that at the point
     * of this method being called, not all view elements have been layed out. Esp.
     * the PhotoView is still in the process of loading / layout
     *
     * @param position
     */
    abstract void onNewPageSelected(int position);

    @Override
    public boolean onException(final Exception e, final Uri model, final Target<GlideDrawable> target, final boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(final GlideDrawable resource, final Uri model, final Target<GlideDrawable> target, final boolean isFromMemoryCache, final boolean isFirstResource) {
        imageResourceReady = true;
        return false;
    }

    @Override
    public void onLayoutChange(final View v, final int left, final int top, final int right, final int bottom, final int oldLeft, final int oldTop, final int oldRight, final int oldBottom) {
        checkStateAndApplyMatrix();
    }

    @Override
    public void onPageSelected(final int position) {
        if (latestPagerIndex != position) {
            latestPagerIndex = position;
            onNewPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        pagerStateIdle = state == ViewPager.SCROLL_STATE_IDLE;
        checkStateAndApplyMatrix();
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
        // no-op
    }

    /**
     * Reset the internal state of "image resource is ready". Calling this method
     * ensures that the {@link #lastSuppMatrix} will not be applied anymore until
     * a new image resource is loaded.
     */
    void resetImageResourceState() {
        imageResourceReady = false;
    }

    /**
     * Set the supplMatrix of the PhotoView instance currently selected by the ViewPager.
     *
     * @param currentPhotoViewSuppMatrix
     */
    void setLastSuppMatrix(final Matrix currentPhotoViewSuppMatrix) {
        lastSuppMatrix = new Matrix(currentPhotoViewSuppMatrix);
    }

    private void checkStateAndApplyMatrix() {
        if (imageResourceReady && pagerStateIdle && lastSuppMatrix != null) {
            resetImageResourceState();
            onApplyZoomPanMatrix(lastSuppMatrix);
        }
    }
}
