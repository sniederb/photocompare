package ch.want.imagecompare.domain;

import android.graphics.PointF;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import ch.want.imagecompare.ui.compareimages.PanAndZoomState;

public class PhotoViewMediatorUnitTest {

    @Test
    public void testOnPanOrZoomChangedWithHappyCase() {
        final ImageDetailView topView = Mockito.mock(ImageDetailView.class);
        Mockito.when(topView.getPanAndZoomState()).thenReturn(new PanAndZoomState(6.5f, new FakePointF(320, 240)));
        final ImageDetailView bottomView = Mockito.mock(ImageDetailView.class);
        Mockito.when(bottomView.getPanAndZoomState()).thenReturn(new PanAndZoomState(1.8f, new FakePointF(600, 480)));
        final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView);
        testee.setSyncZoomAndPan(false);
        // act
        testee.onPanOrZoomChanged(topView);
        // .. now enable sync, change pan and zoom of top and sync it down to bottom
        testee.setSyncZoomAndPan(true);
        Mockito.when(topView.getPanAndZoomState()).thenReturn(new PanAndZoomState(3.0f, new FakePointF(300, 220)));
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
        final ImageDetailView topView = Mockito.mock(ImageDetailView.class);
        Mockito.when(topView.getPanAndZoomState()).thenReturn(new PanAndZoomState(1, null));
        final ImageDetailView bottomView = Mockito.mock(ImageDetailView.class);
        Mockito.when(bottomView.getPanAndZoomState()).thenReturn(new PanAndZoomState(1, new FakePointF(12, 34)));
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

    /**
     * Need to mock android-package stuff ...
     *
     * @return
     */
    public static class FakePointF extends PointF {
        FakePointF(final float x, final float y) {
            this.x = x;
            this.y = y;
        }
    }
}
