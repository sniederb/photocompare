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
     * <p><strong>Offset</strong> of scale and pan between top and bottom image such
     * that offsets are applied positively (+ and x) from bottom to top, and
     * negatively (- and %) from top to bottom.</p>
     * <p>Note that the internal {@code centerPoint} is a <strong>ratio</strong></p>
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
            final PanAndZoomState targetPanAndZoomState = applyOffset(sourcePanAndZoomState, sourceIsBottom);
            targetView.setPanAndZoomState(targetPanAndZoomState);
        } finally {
            targetView.enableStateChangedListener();
        }
    }

    private PanAndZoomState applyOffset(final PanAndZoomState sourcePanAndZoomState, final boolean sourceIsBottom) {
        if (panAndZoomOffset == null) {
            initPanAndZoomOffset();
        }
        final float scaleWithOffset;
        if (sourceIsBottom) {
            scaleWithOffset = sourcePanAndZoomState.getScale() * panAndZoomOffset.getScale();
        } else {
            scaleWithOffset = sourcePanAndZoomState.getScale() / panAndZoomOffset.getScale();
        }
        final PointF centerSource = sourcePanAndZoomState.getCenterPoint().orElse(null);
        final PointF centerWithOffset = applyDimensionPointOffset(topView.getSourceDimension(), bottomView.getSourceDimension(), centerSource, sourceIsBottom);
        return new PanAndZoomState(scaleWithOffset, centerWithOffset);
    }

    /**
     * Create an initial {@link #panAndZoomOffset}
     */
    private void initPanAndZoomOffset() {
        final Dimension topDimension = topView.getSourceDimension();
        final Dimension bottomDimension = bottomView.getSourceDimension();
        final float dimensionScaleOffset = getDimensionScaleOffset(topDimension, bottomDimension);
        panAndZoomOffset = new PanAndZoomState(dimensionScaleOffset, null);
    }

    /**
     * Update the internal {@link #panAndZoomOffset} when {@link #topView} and {@link #bottomView}
     * are not synchronized.
     */
    private void updatePanAndZoomOffset() {
        final PanAndZoomState topPanAndZoom = topView.getPanAndZoomState();
        final Dimension topDimension = topView.getSourceDimension();
        final PanAndZoomState bottomPanAndZoom = bottomView.getPanAndZoomState();
        final Dimension bottomDimension = bottomView.getSourceDimension();
        // note that here it would be wrong to apply getDimensionScaleOffset() again
        final float scaleOffset = topPanAndZoom.getScale() / bottomPanAndZoom.getScale();
        //
        final PointF topCenterPoint = topPanAndZoom.getCenterPoint().orElse(null);
        final PointF bottomCenterPoint = bottomPanAndZoom.getCenterPoint().orElse(null);
        if ((topCenterPoint != null) && (bottomCenterPoint != null)) {
            final PointF centerOffset = getRelativeOffsetAsPoint(topDimension, topCenterPoint, bottomDimension, bottomCenterPoint);
            panAndZoomOffset = new PanAndZoomState(scaleOffset, centerOffset);
        } else {
            panAndZoomOffset = new PanAndZoomState(scaleOffset, null);
        }
    }

    private static float getDimensionScaleOffset(final Dimension topDimension, final Dimension bottomDimension) {
        return bottomDimension.getDiagonal() / topDimension.getDiagonal();
    }

    /**
     * This method creates a {@link PointF} instance, which holds <strong>ratios</strong> for the x/y axis
     */
    private static PointF getRelativeOffsetAsPoint(final Dimension topDimension, final PointF topCenterPoint, final Dimension bottomDimension, final PointF bottomCenterPoint) {
        final float ratioX = (topCenterPoint.x / topDimension.width) / (bottomCenterPoint.x / bottomDimension.width);
        final float ratioY = (topCenterPoint.y / topDimension.height) / (bottomCenterPoint.y / bottomDimension.height);
        return new PointF(ratioX, ratioY);
    }

    /**
     * Sync'ing pan between a 12MP and a 24MP image, eg. moving horizontally on the 24MP image would "over-move"
     * the 12MP image. This method adjusts for varying dimensions.
     */
    private PointF applyDimensionPointOffset(final Dimension topDimension, final Dimension bottomDimension, final PointF sourcePoint, final boolean sourceIsBottom) {
        if (sourcePoint == null) {
            return null;
        }
        final float relativeX;
        final float relativeY;
        if (sourceIsBottom) {
            relativeX = sourcePoint.x / bottomDimension.width * topDimension.width;
            relativeY = sourcePoint.y / bottomDimension.height * topDimension.height;
        } else {
            relativeX = sourcePoint.x / topDimension.width * bottomDimension.width;
            relativeY = sourcePoint.y / topDimension.height * bottomDimension.height;
        }
        return panAndZoomOffset.getCenterPoint().map(centerOffset -> {
            if (sourceIsBottom) {
                return new PointF(relativeX * centerOffset.x, relativeY * centerOffset.y);
            }
            return new PointF(relativeX / centerOffset.x, relativeY / centerOffset.y);
        }).orElseGet(() -> new PointF(relativeX, relativeY));
    }

    public void resetState() {
        topView.resetPanAndZoomState();
        bottomView.resetPanAndZoomState();
        panAndZoomOffset = null;
    }
}

