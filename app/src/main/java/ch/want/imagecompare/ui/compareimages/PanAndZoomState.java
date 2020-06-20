package ch.want.imagecompare.ui.compareimages;

import android.graphics.PointF;

import java.util.Optional;

public class PanAndZoomState {

    static final PanAndZoomState DEFAULT = new PanAndZoomState();
    private final float scale;
    private final PointF centerPoint;

    private PanAndZoomState() {
        scale = 0;
        centerPoint = null;
    }

    public PanAndZoomState(final float scale, final PointF centerPoint) {
        this.scale = scale;
        this.centerPoint = centerPoint;
    }

    PanAndZoomState(final PanAndZoomState currentPhotoViewSuppMatrix) {
        scale = currentPhotoViewSuppMatrix.getScale();
        centerPoint = currentPhotoViewSuppMatrix.getCenterPoint().orElse(null);
    }

    public Optional<PointF> getCenterPoint() {
        return Optional.ofNullable(centerPoint);
    }

    public float getScale() {
        return scale;
    }
}
