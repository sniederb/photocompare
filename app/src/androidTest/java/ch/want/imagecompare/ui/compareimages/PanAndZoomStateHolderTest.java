package ch.want.imagecompare.ui.compareimages;

import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.want.imagecompare.TestSettings;

@RunWith(AndroidJUnit4.class)
public class PanAndZoomStateHolderTest {

    @Rule
    public GrantPermissionRule permissionRule = TestSettings.permissionRule();
    private PanAndZoomState stateAppliedByTestee;

    @Before
    public void init() {
        stateAppliedByTestee = null;
    }

    @Test
    public void onZoomChangedShouldStoreState() {
        // arrange
        final PanAndZoomStateHolder testee = buildTestee();
        // act
        testee.onZoomChanged(1f);
        // assert
        assertNotNull(stateAppliedByTestee);
    }

    private PanAndZoomStateHolder buildTestee() {
        return new PanAndZoomStateHolder() {

            protected void onPanOrZoomChanged(final PanAndZoomState newPanAndZoomState) {
                super.onPanOrZoomChanged(newPanAndZoomState);
                stateAppliedByTestee = newPanAndZoomState;
            }

            @Override
            public void onImageReady() {
            }

            @Override
            void onNewPageSelected(final int position) {
            }
        };
    }
}