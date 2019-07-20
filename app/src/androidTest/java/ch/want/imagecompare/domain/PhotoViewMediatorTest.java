package ch.want.imagecompare.domain;

import android.graphics.Matrix;
import android.net.Uri;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import ch.want.imagecompare.data.ImageBean;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class PhotoViewMediatorTest {

    @Test
    public void matrix_invertTwiceYieldsOriginalMatrix() {
        // arrange
        final Matrix matrix = buildMatrix(4.22f, 2602f, 1134f);
        // act
        final Matrix invertedMatrix = new Matrix();
        matrix.invert(invertedMatrix);
        final Matrix reInvertedMatrix = new Matrix();
        invertedMatrix.invert(reInvertedMatrix);
        // assert
        assertEquals(matrix, reInvertedMatrix);
    }

    @Test
    public void updateDeltaMatrixTopToBottom_applyDeltaMatrixTopToBottom() {
        // arrange
        final Matrix topMatrix = buildMatrix(4.22f, 2602f, 1134f);
        final Matrix bottomMatrix = buildMatrix(4.01f, 2602f, 1134f);
        final ImageDetailView topView = new ImageDetailViewStub(topMatrix);
        final ImageDetailView bottomView = new ImageDetailViewStub(bottomMatrix);
        final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView);
        // act
        testee.setSyncZoomAndPan(false);
        testee.onMatrixChanged(topView);
        testee.setSyncZoomAndPan(true);
        testee.onMatrixChanged(topView);
        // assert
        assertEquals(bottomMatrix, bottomView.getMatrix());
    }

    @Test
    public void getNextValidImageIndex_limitToLowerIndex() {
        // arrange
        final ArrayList<ImageBean> images = buildImageBeans(5);
        final Matrix topMatrix = buildMatrix(4.22f, 2602f, 1134f);
        final Matrix bottomMatrix = buildMatrix(4.01f, 2602f, 1134f);
        final ImageDetailViewStub topView = new ImageDetailViewStub(topMatrix);
        final ImageDetailViewStub bottomView = new ImageDetailViewStub(bottomMatrix);
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
        final Matrix topMatrix = buildMatrix(4.22f, 2602f, 1134f);
        final Matrix bottomMatrix = buildMatrix(4.01f, 2602f, 1134f);
        final ImageDetailViewStub topView = new ImageDetailViewStub(topMatrix);
        final ImageDetailViewStub bottomView = new ImageDetailViewStub(bottomMatrix);
        final PhotoViewMediator testee = new PhotoViewMediator(topView, bottomView);
        testee.initGalleryImageList(images, 0, 1);
        // act
        testee.onPageSelected(bottomView, 0);
        // assert
        assertEquals(1, bottomView.getCurrentIndex());
    }

    private static Matrix buildMatrix(final float zoom, final float imageWidth, final float imageHeight) {
        final float[] values = new float[9];
        values[Matrix.MSCALE_X] = zoom;
        values[Matrix.MSKEW_X] = 0f;
        values[Matrix.MTRANS_X] = -imageWidth;
        values[Matrix.MSKEW_Y] = 0f;
        values[Matrix.MSCALE_Y] = zoom;
        values[Matrix.MTRANS_Y] = -imageHeight;
        values[Matrix.MPERSP_0] = 0f;
        values[Matrix.MPERSP_1] = 0f;
        values[Matrix.MPERSP_2] = 1f;
        final Matrix matrix = new Matrix();
        matrix.setValues(values);
        //when(matrix.setValues(any())).thenCallRealMethod();
        return matrix;
    }

    private static ArrayList<ImageBean> buildImageBeans(final int size) {
        final ArrayList<ImageBean> images = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            images.add(new ImageBean("image_" + i + ".jpg", Uri.parse("/storage/emulated/0/Download/image_" + i + ".jpg")));
        }
        return images;
    }

    private static class ImageDetailViewStub implements ImageDetailView {

        private Matrix matrix;
        private ArrayList<ImageBean> galleryImageList;
        private int viewIndex;

        ImageDetailViewStub(final Matrix matrix) {
            this.matrix = matrix;
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
        public void disableMatrixListener() {
        }

        @Override
        public void setMatrix(final Matrix matrix) {
            this.matrix = matrix;
        }

        @Override
        public Matrix getMatrix() {
            return matrix;
        }

        @Override
        public void resetMatrix() {
        }

        @Override
        public void enableMatrixListener() {
        }

        @Override
        public void setCheckboxStyleDark(final boolean isDark) {
        }
    }
}