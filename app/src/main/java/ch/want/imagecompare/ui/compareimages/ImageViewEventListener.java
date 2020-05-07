package ch.want.imagecompare.ui.compareimages;

import android.graphics.PointF;

public interface ImageViewEventListener {

    void onImageReady();

    void onError();

    void onPanChanged(final PointF newCenter);

    void onZoomChanged(final float newScale);
}
