package ch.want.imagecompare.ui.compareimages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.viewpager.widget.ViewPager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ZoomPanRestoreHandlerTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
    private PanAndZoomState stateAppliedByTestee;

    @Before
    public void init() {
        stateAppliedByTestee = null;
    }

    @Test
    public void eventOrderResourceReadyLayoutChangePagerIdleShouldApplyPanAndZoom() {
        // arrange
        final ZoomPanRestoreHandler testee = buildTestee();
        // act
        testee.onImageReady();
        testee.onZoomChanged(1f);
        testee.checkStateAndApplyPanAndZoomState();
        testee.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        // assert
        assertNotNull(stateAppliedByTestee);
    }

    @Test
    public void eventOrderPagerIdleResourceReadyLayoutChangeShouldApplyPanAndZoom() {
        // arrange
        final ZoomPanRestoreHandler testee = buildTestee();
        // act
        testee.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        testee.onImageReady();
        testee.onZoomChanged(1f);
        testee.checkStateAndApplyPanAndZoomState();
        // assert
        assertNotNull(stateAppliedByTestee);
    }

    @Test
    public void eventOrderResourceReadyPagerIdleLayoutChangeShouldApplyPanAndZoom() {
        // arrange
        final ZoomPanRestoreHandler testee = buildTestee();
        // act
        testee.onImageReady();
        testee.onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE);
        testee.onZoomChanged(1f);
        testee.checkStateAndApplyPanAndZoomState();
        // assert
        assertNotNull(stateAppliedByTestee);
    }

    @Test
    public void eventOrderResourceReadyLayoutChangeShouldNotApplyPanAndZoom() {
        // arrange
        final ZoomPanRestoreHandler testee = buildTestee();
        // act
        testee.onImageReady();
        testee.onZoomChanged(1f);
        testee.checkStateAndApplyPanAndZoomState();
        // assert
        assertNull(stateAppliedByTestee);
    }

    private ZoomPanRestoreHandler buildTestee() {
        return new ZoomPanRestoreHandler() {

            @Override
            void onApplyPanAndZoomState(final PanAndZoomState targetPanAndZoomState) {
                stateAppliedByTestee = targetPanAndZoomState;
            }

            @Override
            void onNewPageSelected(final int position) {
            }
        };
    }
}