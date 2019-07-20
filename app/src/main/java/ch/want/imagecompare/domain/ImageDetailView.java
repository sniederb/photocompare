package ch.want.imagecompare.domain;

import android.graphics.Matrix;

import java.util.ArrayList;

import ch.want.imagecompare.data.ImageBean;

public interface ImageDetailView {
    void notifyDataSetChanged();

    void setCurrentIndex(int imageIndex);

    int getCurrentIndex();

    ImageBean getCurrentImageBean();

    void setCrossImageEventHandler(CrossViewEventHandler matrixChangeListener);

    void setImageList(ArrayList<ImageBean> galleryImageList);

    void disableMatrixListener();

    /**
     * Set the 'supp' matrix of the PhotoView instance
     *
     * @param matrix
     */
    void setMatrix(Matrix matrix);

    /**
     * Get the 'supp' matrix of the PhotoView instance
     *
     * @return
     */
    Matrix getMatrix();

    /**
     * Reset view back to 100% view (no zoom, no pan)
     */
    void resetMatrix();

    void enableMatrixListener();

    void setCheckboxStyleDark(boolean isDark);

    void setShowExif(boolean showExifDetails);
}
