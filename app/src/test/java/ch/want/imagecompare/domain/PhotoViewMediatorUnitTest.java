package ch.want.imagecompare.domain;

import android.graphics.PointF;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import ch.want.imagecompare.data.Dimension;
import ch.want.imagecompare.ui.compareimages.PanAndZoomState;

public class PhotoViewMediatorUnitTest {

    @Test
    public void testOnPanOrZoomChangedWithHappyCase() {
        final ImageDetailView topView = createImageDetailView(new PanAndZoomState(6.5f, new FakePointF(320, 240)), new Dimension(4000, 3000));
        final ImageDetailView bottomView = createImageDetailView(new PanAndZoomState(1.8f, new FakePointF(600, 480)), new Dimension(4000, 3000));
        final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView);
        trainTesteeInternalOffset(testee, topView);
        Mockito.when(topView.getPanAndZoomState()).thenReturn(new PanAndZoomState(3.0f, new FakePointF(300, 220)));
        // act
        testee.onPanOrZoomChanged(topView);
        // assert
        final ArgumentCaptor<PanAndZoomState> captor = ArgumentCaptor.forClass(PanAndZoomState.class);
        Mockito.verify(bottomView).setPanAndZoomState(captor.capture());
        final PanAndZoomState targetForBottomView = captor.getValue();
        Assert.assertEquals(0.83f, targetForBottomView.getScale(), 0.01);
        Assert.assertTrue(targetForBottomView.getCenterPoint().isPresent());
        // in a unit test, we can't assert the PointF coordinates, as the android stubbing results in these always being 0/0
    }

    @Test
    public void testOnPanOrZoomChangedWithUnknowCenter() {
        final ImageDetailView topView = createImageDetailView(new PanAndZoomState(1f, null), new Dimension(4000, 3000));
        final ImageDetailView bottomView = createImageDetailView(new PanAndZoomState(1f, new FakePointF(12, 34)), new Dimension(4000, 3000));
        final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView);
        testee.setSyncZoomAndPan(false);
        // act
        testee.onPanOrZoomChanged(topView);
        testee.setSyncZoomAndPan(true);
        testee.onPanOrZoomChanged(topView);
        // assert
        final ArgumentCaptor<PanAndZoomState> captor = ArgumentCaptor.forClass(PanAndZoomState.class);
        Mockito.verify(bottomView).setPanAndZoomState(captor.capture());
        final PanAndZoomState targetForBottomView = captor.getValue();
        Assert.assertEquals(1f, targetForBottomView.getScale(), 0.01);
        Assert.assertFalse(targetForBottomView.getCenterPoint().isPresent());
    }

    @Test
    public void testOnPanOrZoomChangedWithZoomingForDifferentImagesSizes() {
        final ImageDetailView topView = createImageDetailView(new PanAndZoomState(2f, new FakePointF(2016, 1512)), new Dimension(4032, 3024));
        final ImageDetailView bottomView = createImageDetailView(new PanAndZoomState(1f, new FakePointF(3008, 2506)), new Dimension(6016, 4512));
        final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView);
        testee.setSyncZoomAndPan(true);
        // act
        testee.onPanOrZoomChanged(topView);
        // assert
        final ArgumentCaptor<PanAndZoomState> captor = ArgumentCaptor.forClass(PanAndZoomState.class);
        Mockito.verify(bottomView).setPanAndZoomState(captor.capture());
        final PanAndZoomState targetForBottomView = captor.getValue();
        Assert.assertEquals(1.34f, targetForBottomView.getScale(), 0.01);
        Assert.assertTrue(targetForBottomView.getCenterPoint().isPresent());
    }

    private static ImageDetailView createImageDetailView(final PanAndZoomState panAndZoomState, final Dimension dimension) {
        final ImageDetailView imageView = Mockito.mock(ImageDetailView.class);
        Mockito.when(imageView.getPanAndZoomState()).thenReturn(panAndZoomState);
        Mockito.when(imageView.getSourceDimension()).thenReturn(dimension);
        return imageView;
    }

    /**
     * This method fires an {@code onPanOrZoomChanged} event so the mediator initializes the internal state.
     */
    private static void trainTesteeInternalOffset(final PhotoViewMediator testee, final ImageDetailView sourceView) {
        testee.setSyncZoomAndPan(false);
        testee.onPanOrZoomChanged(sourceView);
        testee.setSyncZoomAndPan(true);
    }

    /**
     * Need to mock android-package stuff ...
     */
    public static class FakePointF extends PointF {
        FakePointF(final float x, final float y) {
            this.x = x;
            this.y = y;
        }
    }
}
