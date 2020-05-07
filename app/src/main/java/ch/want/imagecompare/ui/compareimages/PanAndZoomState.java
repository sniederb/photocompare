package ch.want.imagecompare.ui.compareimages;

import android.graphics.PointF;

public class PanAndZoomState {

    private final float scale;
    private final PointF centerPoint;

    PanAndZoomState() {
        scale = 1;
        centerPoint = new PointF(0, 0);
    }

    public PanAndZoomState(final float scale, final PointF centerPoint) {
        this.scale = scale;
        this.centerPoint = centerPoint;
    }

    PanAndZoomState(final PanAndZoomState currentPhotoViewSuppMatrix) {
        scale = currentPhotoViewSuppMatrix.getScale();
        centerPoint = currentPhotoViewSuppMatrix.getCenterPoint();
    }

    public PointF getCenterPoint() {
        return centerPoint;
    }

    public float getScale() {
        return scale;
    }
}
