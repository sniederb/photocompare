package ch.want.imagecompare.domain;

import java.util.ArrayList;

import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.compareimages.PanAndZoomState;

public interface ImageDetailView {
    void notifyDataSetChanged();

    void setCurrentIndex(int imageIndex);

    int getCurrentIndex();

    ImageBean getCurrentImageBean();

    void setCrossImageEventHandler(CrossViewEventHandler matrixChangeListener);

    void setImageList(ArrayList<ImageBean> galleryImageList);

    void disableStateChangedListener();

    /**
     * Set the pan and zoom image state
     */
    void setPanAndZoomState(PanAndZoomState panAndZoomState);

    /**
     * Get the pan and zoom image state
     */
    PanAndZoomState getPanAndZoomState();

    /**
     * Reset view back to 100% view (no zoom, no pan)
     */
    void resetPanAndZoomState();

    void enableStateChangedListener();

    void setCheckboxStyleDark(boolean isDark);

    void setShowExif(boolean showExifDetails);
}
