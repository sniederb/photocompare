package ch.want.imagecompare.domain;

import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.List;

import ch.want.imagecompare.data.ImageBean;

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
    private boolean checkboxesDarkMode = true;
    private boolean showExifDetails = true;
    private final Matrix deltaMatrixTopToBottom = new Matrix();

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
     * Propagate matrix change from one view to the other, and return the source matrix
     *
     * @param sourceView
     * @return
     */
    void onMatrixChanged(final ImageDetailView sourceView) {
        final ImageDetailView copyToView = sourceView == topView ? bottomView : topView;
        if (isSyncZoomAndPan()) {
            copyMatrix(sourceView, copyToView);
        } else {
            updateDeltaMatrix();
        }
    }

    public void resetMatrix() {
        resetDeltaMatrix();
        topView.resetMatrix();
        bottomView.resetMatrix();
    }

    private void resetDeltaMatrix() {
        final float[] deltaValues = new float[9];
        deltaValues[Matrix.MSCALE_X] = 1f;
        deltaValues[Matrix.MSKEW_X] = 0f;
        deltaValues[Matrix.MTRANS_X] = 0f;
        deltaValues[Matrix.MSKEW_Y] = 0f;
        deltaValues[Matrix.MSCALE_Y] = 1f;
        deltaValues[Matrix.MTRANS_Y] = 0f;
        deltaValues[Matrix.MPERSP_0] = 0f;
        deltaValues[Matrix.MPERSP_1] = 0f;
        deltaValues[Matrix.MPERSP_2] = 1f;
        deltaMatrixTopToBottom.setValues(deltaValues);
    }

    /**
     * Take matrix from sourceView, apply {@link #deltaMatrixTopToBottom}, and send that
     * to the targetView. As the {@link #deltaMatrixTopToBottom} is for transformation
     * from top to bottm, it needs to be inverted to sync from bottom to top.
     *
     * @param sourceView
     * @param targetView
     */
    private void copyMatrix(final ImageDetailView sourceView, final ImageDetailView targetView) {
        copyMatrix(sourceView.getMatrix(), targetView, sourceView == bottomView);
    }

    private void copyMatrix(final Matrix sourceMatrix, final ImageDetailView targetView, final boolean invertDeltaMatrix) {
        targetView.disableMatrixListener();
        try {
            final Matrix resultingMatrix = applyDeltaMatrixTopToBottom(sourceMatrix, invertDeltaMatrix);
            targetView.setMatrix(resultingMatrix);
        } finally {
            targetView.enableMatrixListener();
        }
    }

    /**
     * Take the top- and bottom-view matrices and derive a delta so that
     * {@link Matrix#setConcat(Matrix, Matrix)} with source and delta will result
     * in the bottom matrix.
     */
    private void updateDeltaMatrix() {
        final Matrix topMatrix = topView.getMatrix();
        final Matrix bottomMatrix = bottomView.getMatrix();
        updateDeltaMatrixTopToBottom(topMatrix, bottomMatrix);
    }

    /**
     * Build a delta matrix with a zoom ratio and trans offsets, so that running
     * {@link #applyDeltaMatrixTopToBottom(Matrix, boolean)} on {@code fromMatrix} will yield
     * {@code toMatrix}
     *
     * @param topMatrix
     * @param bottomMatrix
     * @return
     */
    private void updateDeltaMatrixTopToBottom(final Matrix topMatrix, final Matrix bottomMatrix) {
        final float[] fromMatrixValues = new float[9];
        topMatrix.getValues(fromMatrixValues);
        final float[] toMatrixValues = new float[9];
        bottomMatrix.getValues(toMatrixValues);
        final float[] deltaValues = new float[9];
        deltaValues[Matrix.MSCALE_X] = toMatrixValues[Matrix.MSCALE_X] / fromMatrixValues[Matrix.MSCALE_X];
        deltaValues[Matrix.MSKEW_X] = 0f;
        deltaValues[Matrix.MTRANS_X] = toMatrixValues[Matrix.MTRANS_X] - fromMatrixValues[Matrix.MTRANS_X];
        deltaValues[Matrix.MSKEW_Y] = 0f;
        deltaValues[Matrix.MSCALE_Y] = toMatrixValues[Matrix.MSCALE_Y] / fromMatrixValues[Matrix.MSCALE_Y];
        deltaValues[Matrix.MTRANS_Y] = toMatrixValues[Matrix.MTRANS_Y] - fromMatrixValues[Matrix.MTRANS_Y];
        deltaValues[Matrix.MPERSP_0] = 0f;
        deltaValues[Matrix.MPERSP_1] = 0f;
        deltaValues[Matrix.MPERSP_2] = 1f;
        deltaMatrixTopToBottom.setValues(deltaValues);
    }

    private Matrix applyDeltaMatrixTopToBottom(final Matrix fromMatrix, final boolean inverse) {
        final float[] targetMatrixValues = new float[9];
        fromMatrix.getValues(targetMatrixValues);
        final float[] deltaValues = new float[9];
        deltaMatrixTopToBottom.getValues(deltaValues);
        if (inverse) {
            targetMatrixValues[Matrix.MSCALE_X] = targetMatrixValues[Matrix.MSCALE_X] / deltaValues[Matrix.MSCALE_X];
            targetMatrixValues[Matrix.MTRANS_X] = targetMatrixValues[Matrix.MTRANS_X] - deltaValues[Matrix.MTRANS_X];
            targetMatrixValues[Matrix.MSCALE_Y] = targetMatrixValues[Matrix.MSCALE_Y] / deltaValues[Matrix.MSCALE_Y];
            targetMatrixValues[Matrix.MTRANS_Y] = targetMatrixValues[Matrix.MTRANS_Y] - deltaValues[Matrix.MTRANS_Y];
        } else {
            targetMatrixValues[Matrix.MSCALE_X] = targetMatrixValues[Matrix.MSCALE_X] * deltaValues[Matrix.MSCALE_X];
            targetMatrixValues[Matrix.MTRANS_X] = targetMatrixValues[Matrix.MTRANS_X] + deltaValues[Matrix.MTRANS_X];
            targetMatrixValues[Matrix.MSCALE_Y] = targetMatrixValues[Matrix.MSCALE_Y] * deltaValues[Matrix.MSCALE_Y];
            targetMatrixValues[Matrix.MTRANS_Y] = targetMatrixValues[Matrix.MTRANS_Y] + deltaValues[Matrix.MTRANS_Y];
        }
        // note that com.github.chrisbanes.photoview.PhotoViewAttacher.getDrawMatrix does:
        // mDrawMatrix.set(mBaseMatrix);
        // mDrawMatrix.postConcat(mSuppMatrix);
        final Matrix result = new Matrix();
        result.setValues(targetMatrixValues);
        return result;
    }
}

