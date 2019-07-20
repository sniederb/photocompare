package ch.want.imagecompare.domain;

import android.graphics.RectF;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;

/**
 * This class handles all events which are coupled across the two parts of the image compare.
 */
public class CrossViewEventHandler implements OnMatrixChangedListener {

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
    public void onMatrixChanged(final RectF rect) {
        if (matrixListenerActive && imageDetailView != null) {
            photoViewMediator.onMatrixChanged(imageDetailView);
        }
    }

    public void disableMatrixListener() {
        matrixListenerActive = false;
    }

    public void enableMatrixListener() {
        matrixListenerActive = true;
    }

    public boolean onIntentForNewImagePage(final int position) {
        // this will update the 'current' indexes on the photoViewMediator
        return imageDetailView != null && photoViewMediator.onPageSelected(imageDetailView, position);
    }

    public int getOtherImageIndex() {
        return photoViewMediator.getIndexFromOtherView(imageDetailView);
    }
}
