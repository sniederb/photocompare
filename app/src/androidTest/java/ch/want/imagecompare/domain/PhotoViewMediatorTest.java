package ch.want.imagecompare.domain;

import android.graphics.PointF;
import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.compareimages.PanAndZoomState;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PhotoViewMediatorTest {

    @Test
    public void getNextValidImageIndex_limitToLowerIndex() {
        // arrange
        final ArrayList<ImageBean> images = buildImageBeans(5);
        final PanAndZoomState topPanAndZoomState = new PanAndZoomState(1.2f, new PointF(0, 1));
        final PanAndZoomState bottomPanAndZoomState = new PanAndZoomState(1.1f, new PointF(1, 2));
        final ImageDetailViewStub topView = new ImageDetailViewStub(topPanAndZoomState);
        final ImageDetailViewStub bottomView = new ImageDetailViewStub(bottomPanAndZoomState);
        final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView);
        testee.initGalleryImageList(images, 0, 1);
        // act
        final int nextBottomViewIndex = testee.getNextValidImageIndex(bottomView, 0);
        // assert
        assertEquals(1, nextBottomViewIndex);
    }

    @Test
    public void onPageSelected_limitToLowerIndex() {
        // arrange
        final ArrayList<ImageBean> images = buildImageBeans(5);
        final PanAndZoomState topPanAndZoomState = new PanAndZoomState(1.2f, new PointF(0, 1));
        final PanAndZoomState bottomPanAndZoomState = new PanAndZoomState(1.1f, new PointF(1, 2));
        final ImageDetailViewStub topView = new ImageDetailViewStub(topPanAndZoomState);
        final ImageDetailViewStub bottomView = new ImageDetailViewStub(bottomPanAndZoomState);
        final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView);
        testee.initGalleryImageList(images, 0, 1);
        // act
        testee.onPageSelected(bottomView, 0);
        // assert
        assertEquals(1, bottomView.getCurrentIndex());
    }

    private static ArrayList<ImageBean> buildImageBeans(final int size) {
        final ArrayList<ImageBean> images = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            images.add(new ImageBean("image_" + i + ".jpg", Uri.parse("/storage/emulated/0/Download/image_" + i + ".jpg")));
        }
        return images;
    }

    private static class ImageDetailViewStub implements ImageDetailView {

        private final PanAndZoomState panAndZoomState;
        private ArrayList<ImageBean> galleryImageList;
        private int viewIndex;

        ImageDetailViewStub(final PanAndZoomState panAndZoomState) {
            this.panAndZoomState = panAndZoomState;
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
        public void setPanAndZoomState(final PanAndZoomState panAndZoomState) {
        }

        @Override
        public PanAndZoomState getPanAndZoomState() {
            return null;
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