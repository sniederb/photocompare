package ch.want.imagecompare.domain;

import static org.junit.Assert.assertEquals;

import android.Manifest;
import android.graphics.PointF;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.want.imagecompare.TestSettings;
import ch.want.imagecompare.data.Dimension;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.compareimages.CompareImagesActivity;
import ch.want.imagecompare.ui.compareimages.PanAndZoomState;

@RunWith(AndroidJUnit4.class)
public class PhotoViewMediatorTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Test
    public void onPageSelected_limitToLowerIndex() {
        // arrange
        final ArrayList<ImageBean> images = buildImageBeans(5);
        final PanAndZoomState topPanAndZoomState = new PanAndZoomState(1.2f, new PointF(0, 1));
        final PanAndZoomState bottomPanAndZoomState = new PanAndZoomState(1.1f, new PointF(1, 2));
        final ImageDetailViewStub topView = new ImageDetailViewStub(topPanAndZoomState);
        final ImageDetailViewStub bottomView = new ImageDetailViewStub(bottomPanAndZoomState);
        try (final ActivityScenario<CompareImagesActivity> scenario = ActivityScenario.launch(CompareImagesActivity.class)) {
            scenario.onActivity(activity -> {
                final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView, activity);
                testee.initGalleryImageList(images, 0, 1);
                // act
                testee.onPageSelected(bottomView, 0);
                // assert
                assertEquals(1, bottomView.getCurrentIndex());
            });
        }
    }

    /**
     * This test ensures that when panning in sync on images of different sizes, both images reach the edges simultaneously.
     */
    @Test
    public void onPanOrZoomChangedWithPanningForDifferentImagesSizes() {
        final ImageDetailView topView = new ImageDetailViewStub(new PanAndZoomState(2f, new PointF(1500, 1400)), new Dimension(4032, 3024));
        final ImageDetailView bottomView = new ImageDetailViewStub(new PanAndZoomState(1.34f, new PointF(2506, 3008)), new Dimension(6016, 4512));
        try (final ActivityScenario<CompareImagesActivity> scenario = ActivityScenario.launch(CompareImagesActivity.class)) {
            scenario.onActivity(activity -> {
                final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView, activity);
                testee.setSyncZoomAndPan(true);
                // act
                testee.onPanOrZoomChanged(topView);
                // assert
                final PanAndZoomState targetForBottomView = bottomView.getPanAndZoomState();
                Assert.assertEquals(1.34f, targetForBottomView.getScale(), 0.01);
                Assert.assertTrue(targetForBottomView.getCenterPoint().isPresent());
                final PointF bottomCenterPoint = targetForBottomView.getCenterPoint().get();
                // 1500 / 4032 x 6016 = 2238.09
                Assert.assertEquals(2238.09f, bottomCenterPoint.x, 0.01);
                // 1400 / 3024 x 4512 = 2088.88
                Assert.assertEquals(2088.88f, bottomCenterPoint.y, 0.01);
            });
        }
    }

    /**
     * This test ensures that when panning unsync'ed and then switching sync back on, the
     * calculated offsets are consistent.
     */
    @Test
    public void onPanOrZoomChangedWithPanningAfterUnsyncForDifferentImagesSizes() {
        final ImageDetailView topView = new ImageDetailViewStub(new PanAndZoomState(2f, new PointF(1500, 1400)), new Dimension(4032, 3024));
        final ImageDetailView bottomView = new ImageDetailViewStub(new PanAndZoomState(1.34f, new PointF(2238, 2088)), new Dimension(6016, 4512));
        try (final ActivityScenario<CompareImagesActivity> scenario = ActivityScenario.launch(CompareImagesActivity.class)) {
            scenario.onActivity(activity -> {
                final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView, activity);
                trainTesteeInternalOffset(testee, topView);
                // act
                testee.onPanOrZoomChanged(topView);
                // assert
                final PanAndZoomState targetForBottomView = bottomView.getPanAndZoomState();
                Assert.assertEquals(1.34f, targetForBottomView.getScale(), 0.01);
                Assert.assertTrue(targetForBottomView.getCenterPoint().isPresent());
                final PointF bottomCenterPoint = targetForBottomView.getCenterPoint().get();
                // the internal offset state must give the same results
                // 1500 / 4032 x 6016 = 2238.09
                Assert.assertEquals(2238f, bottomCenterPoint.x, 1);
                // 1400 / 3024 x 4512 = 2088.88
                Assert.assertEquals(2088f, bottomCenterPoint.y, 1);
            });
        }
    }

    private static ArrayList<ImageBean> buildImageBeans(final int size) {
        final ArrayList<ImageBean> images = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            images.add(new ImageBean("image_" + i + ".jpg", TestSettings.getUri("image_" + i + ".jpg", null)));
        }
        return images;
    }

    /**
     * This method fires an {@code onPanOrZoomChanged} event so the mediator initializes the internal state.
     */
    private static void trainTesteeInternalOffset(final PhotoViewMediator testee, final ImageDetailView sourceView) {
        testee.setSyncZoomAndPan(false);
        testee.onPanOrZoomChanged(sourceView);
        testee.setSyncZoomAndPan(true);
    }

    private static class ImageDetailViewStub implements ImageDetailView {

        private PanAndZoomState panAndZoomState;
        private final Dimension dimension;
        private ArrayList<ImageBean> galleryImageList;
        private int viewIndex;

        ImageDetailViewStub(final PanAndZoomState panAndZoomState) {
            this.panAndZoomState = panAndZoomState;
            dimension = new Dimension(4000, 3000);
        }

        ImageDetailViewStub(final PanAndZoomState panAndZoomState, final Dimension dimension) {
            this.panAndZoomState = panAndZoomState;
            this.dimension = dimension;
        }

        @Override
        public void notifyDataSetChanged() {
        }

        @Override
        public void setCurrentIndex(final int imageIndex) {
            viewIndex = imageIndex;
        }

        @Override
        public int getCurrentIndex() {
            return viewIndex;
        }

        @Override
        public ImageBean getCurrentImageBean() {
            return new ImageBean("foobar", Uri.EMPTY);
        }

        @Override
        public void setCrossImageEventHandler(final CrossViewEventHandler matrixChangeListener) {
        }

        @Override
        public void setImageList(final ArrayList<ImageBean> galleryImageList) {
            this.galleryImageList = galleryImageList;
        }

        @Override
        public void disableStateChangedListener() {
        }

        @Override
        public void updateDynamicViewStateText(final float newScale) {
        }

        @Override
        public PanAndZoomState getPanAndZoomState() {
            return panAndZoomState;
        }

        @Override
        public void setPanAndZoomState(final PanAndZoomState newPanAndZoomState) {
            panAndZoomState = newPanAndZoomState;
        }

        @Override
        public Dimension getSourceDimension() {
            return dimension;
        }

        @Override
        public void resetPanAndZoomState() {
        }

        @Override
        public void enableStateChangedListener() {
        }

        @Override
        public void setCheckboxStyleDark(final boolean isDark) {
        }

        @Override
        public void setShowExif(final boolean showExifDetails) {
        }
    }
}