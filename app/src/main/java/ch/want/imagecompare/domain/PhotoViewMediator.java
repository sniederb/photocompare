package ch.want.imagecompare.domain;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import ch.want.imagecompare.data.Dimension;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.compareimages.PanAndZoomState;

/**
 * This class handles mediation between the upper and lower photo view incl. context
 */
public class PhotoViewMediator {

    public static final int NO_VALID_IMAGE_INDEX = -999;
    private final ArrayList<ImageBean> galleryImageList = new ArrayList<>();
    private final ImageDetailView topView;
    private final ImageDetailView bottomView;
    // holding the currentIndex so onPageSelected() knows which direction we're going
    private int topViewIndex;
    private int bottomViewIndex;

    private boolean syncZoomAndPan = false;
    /**
     * <strong>Offset</strong> of scale and pan between top and bottom image
     */
    private PanAndZoomState panAndZoomOffset = null;
    private boolean checkboxesDarkMode = true;
    private boolean showExifDetails = true;

    public PhotoViewMediator(final ImageDetailView topView, final ImageDetailView bottomView) {
        this.topView = topView;
        this.topView.setImageList(galleryImageList);
        this.topView.setCrossImageEventHandler(new CrossViewEventHandler(this));
        this.bottomView = bottomView;
        this.bottomView.setImageList(galleryImageList);
        this.bottomView.setCrossImageEventHandler(new CrossViewEventHandler(this));
    }

    private boolean isSyncZoomAndPan() {
        return syncZoomAndPan;
    }

    public void setSyncZoomAndPan(final boolean syncZoomAndPan) {
        this.syncZoomAndPan = syncZoomAndPan;
    }

    public ArrayList<ImageBean> getGalleryImageList() {
        return galleryImageList;
    }

    public void initGalleryImageList(final List<ImageBean> newImages, final int topIndex, final int bottomIndex) {
        galleryImageList.clear();
        galleryImageList.addAll(newImages);
        topViewIndex = topIndex;
        if (topViewIndex < 0 || topViewIndex >= galleryImageList.size()) {
            topViewIndex = galleryImageList.size() - 1;
        }
        topView.notifyDataSetChanged();
        topView.setCurrentIndex(topViewIndex);
        deriveInitialBottomIndex(bottomIndex);
        syncZoomAndPan = true;
    }

    public boolean toggleCheckboxStyleDark() {
        checkboxesDarkMode = !checkboxesDarkMode;
        topView.setCheckboxStyleDark(checkboxesDarkMode);
        bottomView.setCheckboxStyleDark(checkboxesDarkMode);
        return checkboxesDarkMode;
    }

    public boolean toggleExifDisplay() {
        showExifDetails = !showExifDetails;
        topView.setShowExif(showExifDetails);
        bottomView.setShowExif(showExifDetails);
        return showExifDetails;
    }

    private void deriveInitialBottomIndex(final int bottomIndex) {
        bottomView.notifyDataSetChanged();
        bottomViewIndex = bottomIndex;
        if (bottomViewIndex == NO_VALID_IMAGE_INDEX) {
            bottomViewIndex = getNextValidImageIndex(bottomView, true);
            if (bottomViewIndex == NO_VALID_IMAGE_INDEX) {
                bottomViewIndex = getNextValidImageIndex(bottomView, false);
            }
        }
        if (bottomViewIndex != NO_VALID_IMAGE_INDEX) {
            bottomView.setCurrentIndex(bottomViewIndex);
        }
    }

    public int getTopIndex() {
        // if everything is fine, this should be equivalent to this.topViewIndex
        return topView.getCurrentIndex();
    }

    public int getBottomIndex() {
        // if everything is fine, this should be equivalent to this.bottomViewIndex
        return bottomView.getCurrentIndex();
    }

    int getIndexFromOtherView(final ImageDetailView thisView) {
        return thisView == topView ? getBottomIndex() : getTopIndex();
    }

    private boolean isImageIndexAllowed(final ImageDetailView sourceContext, final int candidate) {
        final int prohibitIndex = sourceContext == topView ? bottomView.getCurrentIndex() : topView.getCurrentIndex();
        return candidate != prohibitIndex;
    }

    int getNextValidImageIndex(final ImageDetailView forTargetView, final int invalidIndex) {
        final int lastValidIndex = forTargetView == topView ? topViewIndex : bottomViewIndex;
        // did user navigate to the right?
        final boolean searchUpwards = (invalidIndex > lastValidIndex ||
                // what if user navigated left, but there's nothing there anymore?
                invalidIndex == 0) &&
                // what if user navigated right, but we're at the end of the list?
                invalidIndex < (galleryImageList.size() - 2);
        return getNextValidImageIndex(forTargetView, searchUpwards);
    }

    private int getNextValidImageIndex(final ImageDetailView forTargetView, final boolean isDirectionUp) {
        int candidate = getIndexFromOtherView(forTargetView);
        if (isDirectionUp) {
            candidate++;
            return candidate >= galleryImageList.size() ? NO_VALID_IMAGE_INDEX : candidate;
        }
        candidate--;
        return candidate < 0 ? NO_VALID_IMAGE_INDEX : candidate;
    }

    boolean onPageSelected(final ImageDetailView sourceView, final int position) {
        panAndZoomOffset = null;
        final boolean positionAllowed = isImageIndexAllowed(sourceView, position);
        if (!positionAllowed) {
            sourceView.setCurrentIndex(getNextValidImageIndex(sourceView, position));
            return false;
        }
        if (sourceView == topView) {
            topViewIndex = position;
        } else {
            bottomViewIndex = position;
        }
        return true;
    }

    /**
     * Propagate pan/zoom change from one view to the other
     */
    void onPanOrZoomChanged(final ImageDetailView sourceView) {
        final ImageDetailView copyToView = sourceView == topView ? bottomView : topView;
        if (isSyncZoomAndPan()) {
            copyPanAndZoom(sourceView, copyToView);
        } else {
            updatePanAndZoomOffset();
        }
    }

    /**
     * Take pan and zoom from sourceView, and send that to the targetView.
     */
    private void copyPanAndZoom(final ImageDetailView sourceView, final ImageDetailView targetView) {
        copyPanAndZoom(sourceView.getPanAndZoomState(), targetView, sourceView == bottomView);
    }

    private void copyPanAndZoom(final PanAndZoomState sourcePanAndZoomState, final ImageDetailView targetView, final boolean sourceIsBottom) {
        targetView.disableStateChangedListener();
        try {
            final PanAndZoomState targetPanAndZoomState = getPanAndZoomWithOffset(sourcePanAndZoomState, sourceIsBottom);
            targetView.setPanAndZoomState(targetPanAndZoomState);
        } finally {
            targetView.enableStateChangedListener();
        }
    }

    private PanAndZoomState getPanAndZoomWithOffset(final PanAndZoomState sourcePanAndZoomState, final boolean sourceIsBottom) {
        if (panAndZoomOffset == null) {
            initPanAndZoomOffset();
        }
        final float scaleWithOffset;
        final PointF centerOffset = panAndZoomOffset.getCenterPoint().orElse(null);
        final PointF centerSource = sourcePanAndZoomState.getCenterPoint().orElse(null);
        PointF centerWithOffset = null;
        if (sourceIsBottom) {
            scaleWithOffset = sourcePanAndZoomState.getScale() * panAndZoomOffset.getScale();
            if ((centerOffset != null) && (centerSource != null)) {
                centerWithOffset = new PointF(centerSource.x + centerOffset.x, centerSource.y + centerOffset.y);
            }
        } else {
            scaleWithOffset = sourcePanAndZoomState.getScale() / panAndZoomOffset.getScale();
            if ((centerOffset != null) && (centerSource != null)) {
                centerWithOffset = new PointF(centerSource.x - centerOffset.x, centerSource.y - centerOffset.y);
            }
        }
        return new PanAndZoomState(scaleWithOffset, centerWithOffset);
    }

    private void initPanAndZoomOffset() {
        final Dimension topDimension = topView.getSourceDimension();
        final Dimension bottomDimension = bottomView.getSourceDimension();
        final float initScale = (float) bottomDimension.width / topDimension.width;
        panAndZoomOffset = new PanAndZoomState(initScale, new PointF(0, 0));
    }

    private void updatePanAndZoomOffset() {
        final PanAndZoomState topPanAndZoom = topView.getPanAndZoomState();
        final PanAndZoomState bottomPanAndZoom = bottomView.getPanAndZoomState();
        final float scaleOffset = topPanAndZoom.getScale() / bottomPanAndZoom.getScale();
        final PointF topCenterPoint = topPanAndZoom.getCenterPoint().orElse(null);
        final PointF bottomCenterPoint = bottomPanAndZoom.getCenterPoint().orElse(null);
        // FIXME: need to adapt for base image size here
        if ((topCenterPoint != null) && (bottomCenterPoint != null)) {
            final PointF centerOffset = new PointF(topCenterPoint.x - bottomCenterPoint.x, topCenterPoint.y - bottomCenterPoint.y);
            panAndZoomOffset = new PanAndZoomState(scaleOffset, centerOffset);
        } else {
            panAndZoomOffset = new PanAndZoomState(scaleOffset, null);
        }
    }

    public void resetState() {
        topView.resetPanAndZoomState();
        bottomView.resetPanAndZoomState();
    }
}

