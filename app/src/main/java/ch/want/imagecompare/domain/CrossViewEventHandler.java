package ch.want.imagecompare.domain;

import android.graphics.PointF;

import ch.want.imagecompare.ui.compareimages.ImageViewEventListener;

/**
 * This class handles all events which are coupled across the two parts of the image compare.
 */
public class CrossViewEventHandler implements ImageViewEventListener {

    private final PhotoViewMediator photoViewMediator;
    private ImageDetailView imageDetailView;
    private boolean matrixListenerActive;

    private final int currentPageIndex = 0;

    CrossViewEventHandler(final PhotoViewMediator photoViewMediator) {
        this.photoViewMediator = photoViewMediator;
        matrixListenerActive = true;
    }

    public void setImageDetailView(final ImageDetailView imageDetailView) {
        this.imageDetailView = imageDetailView;
    }

    @Override
    public void onImageReady() {
        matrixListenerActive = true;
    }

    @Override
    public void onError() {
    }

    @Override
    public void onPanChanged(final PointF newCenter) {
        onPanOrZoomChanged();
    }

    @Override
    public void onZoomChanged(final float newScale) {
        onPanOrZoomChanged();
    }

    public void disableCrossViewEvents() {
        matrixListenerActive = false;
    }

    public void enableCrossViewEvents() {
        matrixListenerActive = true;
    }

    public boolean onIntentForNewImagePage(final int position) {
        // this will update the 'current' indexes on the photoViewMediator
        return imageDetailView != null && photoViewMediator.onPageSelected(imageDetailView, position);
    }

    public int getOtherImageIndex() {
        return photoViewMediator.getIndexFromOtherView(imageDetailView);
    }

    private void onPanOrZoomChanged() {
        if (matrixListenerActive && imageDetailView != null) {
            photoViewMediator.onPanOrZoomChanged(imageDetailView);
        }
    }
}
