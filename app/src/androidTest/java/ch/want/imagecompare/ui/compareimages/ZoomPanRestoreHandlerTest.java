package ch.want.imagecompare.ui.compareimages;

import android.graphics.Matrix;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.viewpager.widget.ViewPager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class ZoomPanRestoreHandlerTest {

    private Matrix matrixAppliedByTestee;

    @Test
    public void eventOrderResourceReadyLayoutChangePagerIdle_copyOfMatrixApplied() {
        // arrange
        matrixAppliedByTestee = null;
        final Matrix matrix = new Matrix();
        final ZoomPanRestoreHandler testee = buildTestee();
        testee.setLastSuppMatrix(matrix);
        // act
        testee.onResourceReady(null, null, null, true, true);
        testee.onLayoutChange(null, 0, 0, 0, 0, 0, 0, 0, 0);
        testee.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        // assert
        assertNotNull(matrixAppliedByTestee);
        assertNotSame(matrixAppliedByTestee, matrix);
        assertEquals(matrixAppliedByTestee, matrix);
    }

    @Test
    public void eventOrderPagerIdleResourceReadyLayoutChange_copyOfMatrixApplied() {
        // arrange
        matrixAppliedByTestee = null;
        final Matrix matrix = new Matrix();
        final ZoomPanRestoreHandler testee = buildTestee();
        testee.setLastSuppMatrix(matrix);
        // act
        testee.onResourceReady(null, null, null, true, true);
        testee.onLayoutChange(null, 0, 0, 0, 0, 0, 0, 0, 0);
        testee.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        // assert
        assertNotNull(matrixAppliedByTestee);
        assertNotSame(matrixAppliedByTestee, matrix);
        assertEquals(matrixAppliedByTestee, matrix);
    }

    @Test
    public void eventOrderResourceReadyPagerIdleLayoutChange_copyOfMatrixApplied() {
        // arrange
        matrixAppliedByTestee = null;
        final Matrix matrix = new Matrix();
        final ZoomPanRestoreHandler testee = buildTestee();
        testee.setLastSuppMatrix(matrix);
        // act
        testee.onResourceReady(null, null, null, true, true);
        testee.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        testee.onLayoutChange(null, 0, 0, 0, 0, 0, 0, 0, 0);
        // assert
        assertNotNull(matrixAppliedByTestee);
        assertNotSame(matrixAppliedByTestee, matrix);
        assertEquals(matrixAppliedByTestee, matrix);
    }

    @Test
    public void eventOrderResourceReadyLayoutChange_matrixNotApplied() {
        // arrange
        matrixAppliedByTestee = null;
        final Matrix matrix = new Matrix();
        final ZoomPanRestoreHandler testee = buildTestee();
        testee.setLastSuppMatrix(matrix);
        // act
        testee.onResourceReady(null, null, null, true, true);
        testee.onLayoutChange(null, 0, 0, 0, 0, 0, 0, 0, 0);
        // assert
        assertNull(matrixAppliedByTestee);
    }

    private ZoomPanRestoreHandler buildTestee() {
        return new ZoomPanRestoreHandler() {

            @Override
            void onApplyZoomPanMatrix(final Matrix targetDisplayMatrix) {
                matrixAppliedByTestee = targetDisplayMatrix;
            }

            @Override
            void onNewPageSelected(final int position) {
            }
        };
    }
}